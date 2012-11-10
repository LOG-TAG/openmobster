#import "SyncEngineTests.h"
#import "ChangeLogEntry.h"
#import "SyncError.h"
#import "SyncEngine.h"
#import "Anchor.h"
#import "SyncDataSource.h"
#import "Registry.h"
#import "GenericAttributeManager.h"
#import "MobileObjectDatabase.h"
#import "SyncConstants.h"
#import "Add.h"
#import "Replace.h"
#import "Delete.h"
#import "DeviceSerializer.h"


@implementation SyncEngineTests

-(void) testChangeLogEntry
{
	NSLog(@"Starting testChangeLogEntry........");
	
	ChangeLogEntry *entry1 = [ChangeLogEntry getInstance:@"nodeId" :@"operation" :@"recordId"];
	ChangeLogEntry *entry2 = [ChangeLogEntry getInstance:@"nodeId" :@"operation" :@"recordId"];
	
	BOOL equals = [entry1 isEqual:entry2];
	NSLog(@"Equals: %d",equals);
	STAssertTrue(equals,nil);
}

-(void) testSyncError
{
	NSLog(@"Starting testSyncError........");
	
	SyncError *error1 = [SyncError getInstance:@"code" :@"source" :@"target"];
	SyncError *error2 = [SyncError getInstance:@"code" :@"source" :@"target"];
	
	BOOL equals = [error1 isEqual:error2];
	NSLog(@"Equals: %d",equals);
	STAssertTrue(equals,nil);
}

-(void) testAnchorSupport
{
	NSLog(@"Starting testAnchorSupport.......");
	
	//Setup
	SyncDataSource *syncds = [[[SyncDataSource alloc] init] autorelease];
	Registry *registry = [Registry getInstance];
	[registry addService:syncds];
	SyncEngine *syncEngine = [SyncEngine withInit];
	
	NSMutableArray *array = [NSMutableArray array];
	[array addObject:@"channel1"];
	[array addObject:@"channel2"];
	
	for(NSString *local in array)
	{
		//Burn one anchor
		Anchor *anchor = [syncEngine createNewAnchor:local];
	
		for(int i=0; i<5; i++)
		{
			anchor = [syncEngine createNewAnchor:local];
			NSLog(@"------------------------------------------------");
			NSLog(@"Anchor Id: %@",anchor.oid);
			NSLog(@"Anchor Target: %@",anchor.target);
			NSLog(@"Anchor LastSync: %@",anchor.lastSync);
			NSLog(@"Anchor NextSync: %@",anchor.nextSync);
	
			NSString *nextSyncValue = anchor.nextSync;
	
			anchor = [syncEngine createNewAnchor:local];
			NSLog(@"------------------------------------------------");
			NSLog(@"Anchor Id: %@",anchor.oid);
			NSLog(@"Anchor Target: %@",anchor.target);
			NSLog(@"Anchor LastSync: %@",anchor.lastSync);
			NSLog(@"Anchor NextSync: %@",anchor.nextSync);
	
			//Assert
			STAssertTrue(anchor != nil,nil);
			STAssertTrue([anchor.target isEqualToString:local],nil);
			STAssertTrue(anchor.lastSync != nil,nil);
			STAssertTrue(anchor.nextSync != nil,nil);
			STAssertTrue([anchor.lastSync isEqualToString:nextSyncValue],nil);
			STAssertTrue(![anchor.nextSync isEqualToString:nextSyncValue],nil);
		}
	}
}

-(void) testErrorSupport
{
	NSLog(@"Starting testErrorSupport.......");
	
	//Setup
	SyncDataSource *syncds = [[[SyncDataSource alloc] init] autorelease];
	Registry *registry = [Registry getInstance];
	[registry addService:syncds];
	SyncEngine *syncEngine = [SyncEngine withInit];
	
	SyncError *error1 = [syncEngine saveError:@"source" :@"target" :@"200"];
	SyncError *error2 = [syncEngine saveError:@"source" :@"target" :@"500"];
	
	NSLog(@"Error1--------------------------------------------");
	NSLog(@"Source: %@",error1.source);
	NSLog(@"Target: %@",error1.target);
	NSLog(@"Code: %@",error1.code);
	
	NSLog(@"Error2--------------------------------------------");
	NSLog(@"Source: %@",error2.source);
	NSLog(@"Target: %@",error2.target);
	NSLog(@"Code: %@",error2.code);
	
	NSArray *all = [syncEngine readErrors];
	STAssertTrue([all containsObject:error1],nil);
	STAssertTrue([all containsObject:error2],nil);
	
	[syncEngine removeError:@"source" :@"target" :@"200"];
	
	all = [syncEngine readErrors];
	STAssertTrue(![all containsObject:error1],nil);
	STAssertTrue([all containsObject:error2],nil);
}

-(void) testChangeLogSupport
{
	NSLog(@"Starting testChangeLogSupport..........");
	
	SyncEngine *syncEngine = [SyncEngine withInit];
	
	//clear up
	[syncEngine clearChangeLog];
	
	NSMutableArray *entries = [NSMutableArray array];
	for(int i=0; i<5; i++)
	{
		GenericAttributeManager *instance = [GenericAttributeManager withInit];
		[instance setAttribute:@"nodeId" :@"channel"];
		[instance setAttribute:@"operation" :@"Add"];
		[instance setAttribute:@"recordId" :[NSString stringWithFormat:@"%d",i]];
		
		[entries addObject:instance];
	}
	[syncEngine addChangeLogEntries:entries];
	
	//getChangeLog
	NSArray *changelog = [syncEngine getChangeLog:@"channel" operation:@"Add"];
	STAssertTrue([changelog count]==5,nil);
	[self dumpChangeLog:changelog];
	
	//Clear an entry at top
	[syncEngine clearChangeLogEntry:(ChangeLogEntry *)[changelog objectAtIndex:0]];
	changelog = [syncEngine getChangeLog:@"channel" operation:@"Add"];
	STAssertTrue([changelog count]==4,nil);
	[self dumpChangeLog:changelog];
	
	//Clear an entry in the middle
	[syncEngine clearChangeLogEntry:(ChangeLogEntry *)[changelog objectAtIndex:2]];
	changelog = [syncEngine getChangeLog:@"channel" operation:@"Add"];
	STAssertTrue([changelog count]==3,nil);
	[self dumpChangeLog:changelog];
	
	//Clear an entry at the bottom
	int index = [changelog count]-1;
	[syncEngine clearChangeLogEntry:(ChangeLogEntry *)[changelog objectAtIndex:index]];
	changelog = [syncEngine getChangeLog:@"channel" operation:@"Add"];
	STAssertTrue([changelog count]==2,nil);
	[self dumpChangeLog:changelog];
	
	//Clear channel changelog
	[syncEngine clearChangeLog:@"channel"];
	changelog = [syncEngine getChangeLog:@"channel" operation:@"Add"];
	[self dumpChangeLog:changelog];
	STAssertTrue(changelog == nil || [changelog count] == 0,nil);
}

-(void) testGetCommands
{
	NSLog(@"Starting testGetCommands............");
	
	//Test Setup
	Registry *registry = [Registry getInstance];
	MobileObjectDatabase *db = [[[MobileObjectDatabase alloc] init] autorelease];
	[registry addService:db];
	SyncEngine *syncEngine = [SyncEngine withInit];
	
	//cleanup
	[syncEngine clearChangeLog];
	[db deleteAll:@"channel"];
	
	//Setup mobile database
	[self setUpMobileDatabase:syncEngine];
	
	//Test GetAddCommands
	NSArray *addOperations = [syncEngine getAddCommands:0 channel:@"channel" syncType:_TWO_WAY];
	NSLog(@"Count :%d",[addOperations count]);
	STAssertTrue(addOperations != nil && [addOperations count]==5,nil);
	if(addOperations != nil)
	{
		for(Add *local in addOperations)
		{
			//[self printOperation:local];
			NSArray *items = local.items;
			Item *item = [items objectAtIndex:0];
			STAssertTrue(item != nil,nil);
			STAssertTrue(![StringUtil isEmpty:item.data],nil);
		}
	}
	
	//Test GetReplaceCommands
	NSArray *replaceOperations = [syncEngine getReplaceCommands:0 channel:@"channel" syncType:_TWO_WAY];
	NSLog(@"Count :%d",[replaceOperations count]);
	STAssertTrue(replaceOperations != nil && [replaceOperations count]==5,nil);
	if(replaceOperations != nil)
	{
		for(Replace *local in replaceOperations)
		{
			//[self printOperation:local];
			NSArray *items = local.items;
			Item *item = [items objectAtIndex:0];
			STAssertTrue(item != nil,nil);
			STAssertTrue(![StringUtil isEmpty:item.data],nil);
		}
	}
	
	//Test DeleteCommands
	NSArray *deleteOperations = [syncEngine getDeleteCommands:@"channel" syncType:_TWO_WAY];
	NSLog(@"Count :%d",[deleteOperations count]);
	STAssertTrue(deleteOperations != nil && [deleteOperations count]==5,nil);
	if(deleteOperations != nil)
	{
		for(Delete *local in deleteOperations)
		{
			//[self printOperation:local];
			NSArray *items = local.items;
			Item *item = [items objectAtIndex:0];
			STAssertTrue(item != nil,nil);
			STAssertTrue(![StringUtil isEmpty:item.data],nil);
		}
	}
	
	//Test GetSlowSyncCommands
	NSArray *all = [syncEngine getSlowSyncCommands:0 channel:@"channel"];
	NSLog(@"Count :%d",[all count]);
	STAssertTrue(all != nil && [all count]==15,nil);
	if(all != nil)
	{
		for(AbstractOperation *local in all)
		{
			//[self printOperation:local];
			NSArray *items = local.items;
			Item *item = [items objectAtIndex:0];
			STAssertTrue(item != nil,nil);
			STAssertTrue(![StringUtil isEmpty:item.data],nil);
		}
	}
}

-(void) testProcessSlowSyncCommands
{
	NSLog(@"Starting testProcessSlowSyncCommands.........");
	
	//Test Setup
	Registry *registry = [Registry getInstance];
	MobileObjectDatabase *db = [[[MobileObjectDatabase alloc] init] autorelease];
	DeviceSerializer *serializer = [DeviceSerializer getInstance];
	[registry addService:db];
	SyncEngine *syncEngine = [SyncEngine withInit];
	
	//cleanup
	[syncEngine clearChangeLog];
	[db deleteAll:@"channel"];
	
	//Setup mobile database
	[self setUpMobileDatabase:syncEngine];
	
	SyncCommand *syncCommand = [SyncCommand withInit];
	
	//Setup Add Operation
	for(int i=0 ;i<3; i++)
	{
		MobileObject *addObject = [self createArrayPOJO:@"testSlowSync" :5];
		addObject.service = @"channel";
        addObject.recordId = nil;
		NSString *xml = [serializer serialize:addObject];
		Item *item = [Item withInit];
		item.data = xml;
		item.source = @"channel";
		Add *command = [Add withInit];
		[command addItem:item];
		command.cmdId = [NSString stringWithFormat:@"%d",i];
		
		[syncCommand addOperation:command];
	}
	
	//Setup Delete Operation
	NSArray *all = [db readAll:@"channel"];
	for(MobileObject *local in all)
	{
		NSString *xml = [serializer serialize:local];
		Item *item = [Item withInit];
		item.data = xml;
		item.source = @"channel";
		Delete *command = [Delete withInit];
		[command addItem:item];
		command.cmdId = [syncEngine generateSync];
		
		[syncCommand addOperation:command];
	}
	
	//Execute and Assert
	NSArray *status = [syncEngine processSlowSyncCommand:nil channel:@"channel" syncCommand:syncCommand];
	STAssertTrue(status != nil,nil);
	for(Status *local in status)
	{
		[self printStatus:local];
		STAssertTrue([local.data isEqualToString:@"200"],nil);
	}
	all = [db readAll:@"channel"];
	STAssertTrue([all count]==3,nil);
}

-(void) testSyncAddCommands
{
	NSLog(@"Starting testSyncAddCommands.........");
	
	//Test Setup
	Registry *registry = [Registry getInstance];
	MobileObjectDatabase *db = [[[MobileObjectDatabase alloc] init] autorelease];
	DeviceSerializer *serializer = [DeviceSerializer getInstance];
	[registry addService:db];
	SyncEngine *syncEngine = [SyncEngine withInit];
	
	//cleanup
	[syncEngine clearChangeLog];
	[db deleteAll:@"channel"];
	
	//Setup mobile database
	[self setUpMobileDatabase:syncEngine];
	
	SyncCommand *syncCommand = [SyncCommand withInit];
	
	//Setup Add Operation
	for(int i=0 ;i<3; i++)
	{
		MobileObject *addObject = [self createArrayPOJO:@"testSyncAddCommand" :5];
		addObject.service = @"channel";
        addObject.recordId = nil;
		NSString *xml = [serializer serialize:addObject];
		Item *item = [Item withInit];
		item.data = xml;
		item.source = @"channel";
		Add *command = [Add withInit];
		[command addItem:item];
		command.cmdId = [NSString stringWithFormat:@"%d",i];
		
		[syncCommand addOperation:command];
	}
	
	//Execute and Assert
	NSArray *status = [syncEngine processSyncCommand:nil channel:@"channel" syncCommand:syncCommand];
	STAssertTrue(status != nil,nil);
	for(Status *local in status)
	{
		[self printStatus:local];
		STAssertTrue([local.data isEqualToString:@"200"],nil);
	}
	NSArray *all = [db readAll:@"channel"];
	STAssertTrue([all count]==18,nil);
}

-(void) testSyncReplaceCommands
{
	NSLog(@"Starting testSyncReplaceCommands.........");
	
	//Test Setup
	Registry *registry = [Registry getInstance];
	MobileObjectDatabase *db = [[[MobileObjectDatabase alloc] init] autorelease];
	DeviceSerializer *serializer = [DeviceSerializer getInstance];
	[registry addService:db];
	SyncEngine *syncEngine = [SyncEngine withInit];
	
	//cleanup
	[syncEngine clearChangeLog];
	[db deleteAll:@"channel"];
	
	//Setup mobile database
	[self setUpMobileDatabase:syncEngine];
	
	SyncCommand *syncCommand = [SyncCommand withInit];
	
	//Setup Replace Operation
	NSArray *all = [db readAll:@"channel"];
	for(MobileObject *local in all)
	{
		[local setValue:@"value" value:@"updated"];
		NSString *xml = [serializer serialize:local];
		Item *item = [Item withInit];
		item.data = xml;
		item.source = @"channel";
		Replace *command = [Replace withInit];
		[command addItem:item];
		
		[syncCommand addOperation:command];
	}
	
	//Execute and Assert
	NSArray *status = [syncEngine processSyncCommand:nil channel:@"channel" syncCommand:syncCommand];
	STAssertTrue([status count]==15,nil);
	for(Status *local in status)
	{
		//[self printStatus:local];
		STAssertTrue([local.data isEqualToString:@"200"],nil);
	}
	
	all = [db readAll:@"channel"];
	STAssertTrue([all count]==15,nil);
	for(MobileObject *local in all)
	{
		NSString *value = [local getValue:@"value"];
		//NSLog(@"Value: %@",value);
		
		STAssertTrue([value isEqualToString:@"updated"],nil);
	}
}

-(void) testSyncDeleteCommands
{
	NSLog(@"Starting testSyncDeleteCommands.........");
	
	//Test Setup
	Registry *registry = [Registry getInstance];
	MobileObjectDatabase *db = [[[MobileObjectDatabase alloc] init] autorelease];
	DeviceSerializer *serializer = [DeviceSerializer getInstance];
	[registry addService:db];
	SyncEngine *syncEngine = [SyncEngine withInit];
	
	//cleanup
	[syncEngine clearChangeLog];
	[db deleteAll:@"channel"];
	
	//Setup mobile database
	[self setUpMobileDatabase:syncEngine];
	
	SyncCommand *syncCommand = [SyncCommand withInit];
	
	//Setup Delete Operation
	NSArray *all = [db readAll:@"channel"];
	for(MobileObject *local in all)
	{
		NSString *xml = [serializer serialize:local];
		Item *item = [Item withInit];
		item.data = xml;
		item.source = @"channel";
		Delete *command = [Delete withInit];
		[command addItem:item];
		
		[syncCommand addOperation:command];
	}
	
	//Execute and Assert
	NSArray *status = [syncEngine processSyncCommand:nil channel:@"channel" syncCommand:syncCommand];
	STAssertTrue([status count]==15,nil);
	for(Status *local in status)
	{
		[self printStatus:local];
		STAssertTrue([local.data isEqualToString:@"200"],nil);
	}
	
	all = [db readAll:@"channel"];
	STAssertTrue(all == nil || [all count]==0,nil);
}

-(void) testStartBootSync
{
	NSLog(@"Starting testSyncDeleteCommands.........");
	
	//Test Setup
	Registry *registry = [Registry getInstance];
	MobileObjectDatabase *db = [[[MobileObjectDatabase alloc] init] autorelease];
	[registry addService:db];
	SyncEngine *syncEngine = [SyncEngine withInit];
	
	//cleanup
	[syncEngine clearChangeLog];
	[db deleteAll:@"channel"];
	
	//Setup mobile database
	[self setUpMobileDatabase:syncEngine];
	
	NSArray *all = [db readAll:@"channel"];
	NSArray *changelog = [syncEngine getChangeLog:@"channel" operation:@"Add"];
	STAssertTrue([all count]>0,nil);
	STAssertTrue([changelog count]>0,nil);
	
	[syncEngine startBootSync:nil channel:@"channel"];
	
	all = [db readAll:@"channel"];
	changelog = [syncEngine getChangeLog:@"channel" operation:@"Add"];
	STAssertTrue(all == nil || [all count] == 0,nil);
	STAssertTrue(changelog == nil || [changelog count] == 0,nil);
}
//------------------------------------------------------------------------------------
-(void) dumpChangeLog:(NSArray *)changelog
{
	NSLog(@"-----ChangeLog Dump-----------------------");
	if(changelog != nil && [changelog count]>0)
	{
		for(ChangeLogEntry *local in changelog)
		{
			NSLog(@"RecordId: %@",local.recordId);
			NSLog(@"--------------------------------------");
		}
	}
	else 
	{
		NSLog(@"ChangeLog is Empty!!!");
	}

}

-(void) setUpMobileDatabase:(SyncEngine *)syncEngine
{
	MobileObjectDatabase *db = [MobileObjectDatabase getInstance];
	
	NSMutableArray *entries = [NSMutableArray array];
	
	//Setup Add
	for(int i=0; i<5; i++)
	{
		NSString *value = [NSString stringWithFormat:@"pojo/%d",i];
		MobileObject *local = [self createArrayPOJO:value :5];
        local.recordId = nil;
		local.service = @"channel";
		NSString *oid = [db create:local];
		
		GenericAttributeManager *instance = [GenericAttributeManager withInit];
		[instance setAttribute:@"nodeId" :local.service];
		[instance setAttribute:@"operation" :_Add];
		[instance setAttribute:@"recordId" :oid];
		[entries addObject:instance];
	}
	[syncEngine addChangeLogEntries:entries];
	
	//Setup Replace
	for(int i=5; i<10; i++)
	{
		NSString *value = [NSString stringWithFormat:@"pojo/%d",i];
		MobileObject *local = [self createArrayPOJO:value :5];
		local.service = @"channel";
        local.recordId = nil;
		NSString *oid = [db create:local];
		
		GenericAttributeManager *instance = [GenericAttributeManager withInit];
		[instance setAttribute:@"nodeId" :local.service];
		[instance setAttribute:@"operation" :_Replace];
		[instance setAttribute:@"recordId" :oid];
		[entries addObject:instance];
	}
	[syncEngine addChangeLogEntries:entries];
	
	//Setup Delete
	for(int i=10; i<15; i++)
	{
		NSString *value = [NSString stringWithFormat:@"pojo/%d",i];
		MobileObject *local = [self createArrayPOJO:value :5];
		local.service = @"channel";
        local.recordId = nil;
		NSString *oid = [db create:local];
		
		GenericAttributeManager *instance = [GenericAttributeManager withInit];
		[instance setAttribute:@"nodeId" :local.service];
		[instance setAttribute:@"operation" :_Delete];
		[instance setAttribute:@"recordId" :oid];
		[entries addObject:instance];
	}
	[syncEngine addChangeLogEntries:entries];
}

-(MobileObject *) createPOJOWithStrings:(NSString *)value
{
	MobileObject *mobileObject = [MobileObject withInit];
	mobileObject.recordId = @"recordId";
	mobileObject.serverRecordId = @"serverRecordId";
	
	[mobileObject setValue:@"value" value:value];
	
	return mobileObject;
}

-(MobileObject *) createNestedPOJO
{
	MobileObject *mobileObject = [MobileObject withInit];
	mobileObject.recordId = @"recordId";
	mobileObject.serverRecordId = @"serverRecordId";
	
	[mobileObject setValue:@"root" value:@"root"];
	
	[mobileObject setValue:@"root.child1" value:@"child1"];
	
	return mobileObject;
}

-(MobileObject *) createArrayPOJO:(NSString *)value :(int)size
{
	MobileObject *mobileObject = [self createPOJOWithStrings:value];
	mobileObject.recordId = @"recordId";
	mobileObject.serverRecordId = @"serverRecordId";
	
	for(int i=0; i<size; i++)
	{
		NSMutableDictionary *properties = [NSMutableDictionary dictionary];
		NSString *childName = [NSString stringWithFormat:@"child://%d",i];
		NSString *attachmentName = [NSString stringWithFormat:@"attachment://%d",i];
		NSString *attachmentSize = [NSString stringWithFormat:@"%dK",i];
		
		[properties setObject:childName forKey:@"/childName"];
		[properties setObject:attachmentName forKey:@"/attachment/name"];
		[properties setObject:attachmentSize forKey:@"/attachment/size"];
		
		[mobileObject addToArray:@"child.children" :properties];
	}
	
	return mobileObject;
}

-(void) print:(MobileObject *)mobileObject
{
	NSLog(@"Service: %@",mobileObject.service);
	
	NSArray *fields = mobileObject.fields;
	if(fields != nil)
	{
		for(Field *local in fields)
		{
			NSLog(@"Uri: %@",local.uri);
			NSLog(@"Name: %@",local.name);
			NSLog(@"Value: %@",local.value);
			NSLog(@"----------------------------------");
		}
	}
	else 
	{
		NSLog(@"Fields Not Found!!!");
	}
	
	NSArray *arrayMetaData = mobileObject.arrayMetaData;
	if(arrayMetaData != nil)
	{
		for(ArrayMetaData *local in arrayMetaData)
		{
			NSLog(@"ArrayUri: %@",local.arrayUri);
			NSLog(@"ArrayLength: %@",local.arrayLength);
			NSLog(@"ArrayClass: %@",local.arrayClass);
			NSLog(@"----------------------------------");
		}
	}
	else 
	{
		NSLog(@"Fields Not Found!!!");
	}
}

-(void) printOperation:(AbstractOperation *)operation
{
	NSLog(@"-------------------------------------------");
	if([operation isKindOfClass:[Delete class]])
	{
		Delete *delete = (Delete *)operation;
		NSLog(@"SoftDel: %@",delete.softDelete);
		NSLog(@"Archive: %@",delete.archive);
	}
	NSArray *items = [operation items];
	if(items != nil)
	{
		for(Item *local in items)
		{
			NSLog(@"Data: %@",local.data);
		}
	}
	else 
	{
		NSLog(@"No Items Found!!!");
	}
}

-(void) printStatus:(Status *)status
{
	NSLog(@"-------------------------------------------");
	NSLog(@"Cmd: %@",status.cmd);
	NSLog(@"Data: %@",status.data);
	NSLog(@"CmdRef: %@",status.cmdRef);
	NSString *sourceRef = (NSString *)[status.sourceRefs objectAtIndex:0];
	NSLog(@"SourceRef : %@",sourceRef);
}
@end
