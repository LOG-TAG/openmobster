#import "StorageTests.h"
#import "CloudDBManager.h"
#import "Configuration.h"
#import "PersistentMobileObject.h"
#import "Anchor.h"
#import "ChangeLogEntry.h"
#import "SyncError.h"
#import "QSStrings.h"


@implementation StorageTests

-(void) testConfiguration
{
	NSLog(@"Running testConfiguration.....");
	
	//Initialize the Storage service
	[CloudDBManager getInstance];
	
	//Get a Configuration Object
	Configuration *conf = [Configuration getInstance];
	
	//Print the default values
	NSLog(@"******************************");
	NSLog(@"ServerId: %@",conf.serverId);
	NSLog(@"DeviceId: %@",conf.deviceId);
	NSLog(@"MaxPacketSize: %@",conf.maxPacketSize);
	NSLog(@"IsActive: %@",conf.active);
	NSLog(@"******************************");
	
	//Test Transformable Types
	NSMutableDictionary *channels = [NSMutableDictionary dictionaryWithObject:@"channel1" forKey:@"1"];
	conf.channels = channels;
	
	NSMutableSet *appChannels = [NSMutableSet setWithObjects:@"appChannel1",@"appChannel2",nil];
	conf.appChannels = appChannels;
	
	[conf saveInstance];
	
	conf = [Configuration getInstance];
	channels = conf.channels;
	appChannels = conf.appChannels;
	
	//Output Channels
	NSLog(@"*****************************");
	NSArray *keys = [channels allKeys];
	for(NSString *key in keys)
	{
		NSString *value = (NSString *)[channels objectForKey:key];
		NSLog(@"%@=%@",key,value);
	}
	NSLog(@"*****************************");
	
	//Output AppChannels
	NSLog(@"*****************************");
	for(NSString *appChannel in appChannels)
	{
		NSLog(@"%@",appChannel);
	}
	NSLog(@"*****************************");
}

-(void) testMobileObjectPersistence
{
	NSLog(@"Running testMobileObjectPersistence......");
	
	//Initialize the Storage service
	[CloudDBManager getInstance];
    
    [PersistentMobileObject deleteAll:@"channel"];
	
	PersistentMobileObject *pm = [PersistentMobileObject newInstance:@"channel"];
	
	for(int i=0; i<5; i++)
	{
		NSString *uri = [NSString stringWithFormat:@"%d/uri",i];
		NSString *name = [NSString stringWithFormat:@"%d/name",i];
		NSString *value = [NSString stringWithFormat:@"%d/value",i];
		Field *field = [Field withInit:uri name:name value:value];
		[pm addField:field];
	}
	
	for(int i=0; i<5; i++)
	{
		NSString *arrayUri = [NSString stringWithFormat:@"%d/arrayUri",i];
		NSString *arrayLength = [NSString stringWithFormat:@"%d/arrayLength",i];
		NSString *arrayClass = [NSString stringWithFormat:@"%d/arrayClass",i];
		ArrayMetaData *arrayMetaData = [ArrayMetaData withInit:arrayUri 
												   arrayLength:arrayLength arrayClass:arrayClass];
		[pm addArrayMetaData:arrayMetaData];
	}
	
	[pm saveInstance];
	
	//Now read it back
	NSArray *mobileObjects = [PersistentMobileObject findByChannel:@"channel"];
	if(mobileObjects != nil)
	{
		for(PersistentMobileObject *local in mobileObjects)
		{
			NSLog(@"----------------------------------------");
			NSLog(@"Service: %@",local.service);
			STAssertTrue([local.service isEqualToString:@"channel"],nil);
			
			NSArray *fields = [local parseFields];
			if(fields != nil)
			{
				int count = [fields count];
				NSLog(@"# of fields: %d",count);
				STAssertTrue(count == 5,nil);
				for(int i=0; i<count; i++)
				{
					Field *localField = (Field *)[fields objectAtIndex:i];
					NSLog(@"Field URI:%@",localField.uri);
					NSLog(@"Field Name:%@",localField.name);
					NSLog(@"Field Value:%@",localField.value);
				}
			}
			
			NSArray *arrayMetaData = [local parseArrayMetaData];
			if(arrayMetaData != nil)
			{
				int count = [arrayMetaData count];
				NSLog(@"# of arrayMetaData: %d",count);
				STAssertTrue(count == 5,nil);
				for(int i=0; i<count; i++)
				{
					ArrayMetaData *localArrayMetaData = (ArrayMetaData *)[arrayMetaData objectAtIndex:i];
					NSLog(@"Array URI:%@",localArrayMetaData.arrayUri);
					NSLog(@"Array Length:%@",localArrayMetaData.arrayLength);
					NSLog(@"Array Class:%@",localArrayMetaData.arrayClass);
				}
			}
		}
	}
	
	//cleanup
	[PersistentMobileObject deleteAll:@"channel"];
}

-(void) testAnchorPersistence
{
	NSLog(@"Running testAnchorPersistence.....");
	
	//Initialize the Storage service
	[CloudDBManager getInstance];
	
	//Get an anchor
	Anchor *anchor = [Anchor getInstance:@"channel"];
	
	//Print the default values
	NSLog(@"******************************");
	NSLog(@"OID: %@",anchor.oid);
	NSLog(@"LastSync: %@",anchor.lastSync);
	NSLog(@"NextSync: %@",anchor.nextSync);
	NSLog(@"Target: %@",anchor.target);
	NSLog(@"******************************");
	
	anchor.oid = @"oid";
	anchor.lastSync = @"lastSync";
	anchor.nextSync = @"nextSync";
	
	[anchor saveInstance];
	
	anchor = [Anchor getInstance:@"channel"];
	NSLog(@"******************************");
	NSLog(@"OID: %@",anchor.oid);
	NSLog(@"LastSync: %@",anchor.lastSync);
	NSLog(@"NextSync: %@",anchor.nextSync);
	NSLog(@"Target: %@",anchor.target);
	NSLog(@"******************************");
}

-(void) testChangeLogEntryPersistence
{
	NSLog(@"Running testChangeLogEntryPersistence.....");
	
	//Initialize the Storage service
	[CloudDBManager getInstance];
	
	//Get ChangeLogEntry
	ChangeLogEntry *object = [ChangeLogEntry getInstance:@"nodeId" :@"operation" :@"recordId"];
	
	//Print the default values
	NSLog(@"******************************");
	NSLog(@"NodeId: %@",object.nodeId);
	NSLog(@"Operation: %@",object.operation);
	NSLog(@"RecordId: %@",object.recordId);
	NSLog(@"******************************");
	
	object.nodeId = @"update://nodeId";
	object.operation = @"update://operation";
	object.recordId = @"update://recordId";
	
	[object saveInstance];
	
	object = [ChangeLogEntry getInstance:@"update://nodeId" :@"update://operation" :@"update://recordId"];
	
	//Print the default values
	NSLog(@"******************************");
	NSLog(@"NodeId: %@",object.nodeId);
	NSLog(@"Operation: %@",object.operation);
	NSLog(@"RecordId: %@",object.recordId);
	NSLog(@"******************************");
}

-(void) testSyncErrorPersistence
{
	NSLog(@"Running testSyncErrorPersistence.....");
	
	//Initialize the Storage service
	[CloudDBManager getInstance];
	
	//Get ChangeLogEntry
	SyncError *object = [SyncError getInstance:@"code" :@"source" :@"target"];
	
	//Print the default values
	NSLog(@"******************************");
	NSLog(@"Code: %@",object.code);
	NSLog(@"Source: %@",object.source);
	NSLog(@"Target: %@",object.target);
	NSLog(@"******************************");
	
	object.code = @"update://code";
	object.source = @"update://source";
	object.target = @"update://target";
	
	[object saveInstance];
	
	object = [SyncError getInstance:@"update://code" :@"update://source" :@"update://target"];
	
	//Print the default values
	NSLog(@"******************************");
	NSLog(@"Code: %@",object.code);
	NSLog(@"Source: %@",object.source);
	NSLog(@"Target: %@",object.target);
	NSLog(@"******************************");
}
@end
