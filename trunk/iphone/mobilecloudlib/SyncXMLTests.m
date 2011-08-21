#import "SyncXMLTests.h"
#import "Anchor.h"
#import "Item.h"
#import "Status.h"
#import "Alert.h"
#import "SyncCommand.h"
#import "Session.h"
#import "SyncMessage.h"
#import "Credential.h"
#import "SyncXMLGenerator.h"
#import "SyncObjectGenerator.h"

@implementation SyncXMLTests

-(void) testGenerateAnchorXml
{
	NSLog(@"Starting testGeneratorAnchorXML....");
	
	Anchor *anchor = [Anchor getInstance:@"testServerBean"];
	
	anchor.lastSync = @"mockLastSync";
	anchor.nextSync = @"mockNextSync";
	
	NSString *xml = [SyncXMLGenerator generateAnchor:anchor];
	
	NSLog(@"%@",xml);
}

-(void) testGenerateItemXml
{
	NSLog(@"Starting testGeneratorItemXML....");
	
	Item *item = [Item withInit];
	item.target = @"target";
	item.source = @"source";
	item.meta = @"meta";
	item.data = @"data";
	item.moreData = YES;
	
	NSString *xml = [SyncXMLGenerator generateItem:item];
	
	NSLog(@"%@",xml);
	
	item.moreData = NO;
	xml = [SyncXMLGenerator generateItem:item];
	
	NSLog(@"%@",xml);
}

-(void) testGenerateStatusXml
{
	NSLog(@"Starting testGeneratorStatusXML....");
	
	Status *status = [Status withInit];
	status.cmdId = @"cmdId";
	status.cmd = @"cmd";
	status.msgRef = @"msgRef";
	status.cmdRef = @"cmdRef";
	status.data = @"data";
	
	for(int i=0; i<5; i++)
	{
		Item *item = [Item withInit];
		item.target = @"target";
		item.source = @"source";
		item.meta = @"meta";
		item.data = @"data";
		item.moreData = YES;
		
		[status addItem:item];
		[status addSourceRef:@"sourceRef"];
		[status addTargetRef:@"targetRef"];
	}
		
	NSString *xml = [SyncXMLGenerator generateStatus:status];
	
	NSLog(@"%@",xml);
}

-(void) testGenerateAlertXml
{
	NSLog(@"Starting testGeneratorAlertXML....");
	
	Alert *alert = [Alert withInit];
	alert.cmdId = @"cmdId";
	alert.data = @"data";
	
	for(int i=0; i<5; i++)
	{
		Item *item = [Item withInit];
		item.target = @"target";
		item.source = @"source";
		item.meta = @"meta";
		item.data = @"data";
		item.moreData = YES;
		
		[alert addItem:item];
	}
	
	NSString *xml = [SyncXMLGenerator generateAlert:alert];
	
	NSLog(@"%@",xml);
}
 
-(void) testGenerateSyncCommandXml
{
	NSLog(@"Starting testGeneratorSyncCommandXML....");
	
	SyncCommand *command = [SyncCommand withInit];
	command.cmdId = @"cmdId";
	command.target = @"target";
	command.source = @"source";
	command.meta = @"meta";
	command.numberOfChanges = @"10";
	
	for(int i=0;i<2;i++)
	{
		Add *add = [Add withInit];
		add.cmdId = @"add/cmdId";
		add.meta = @"add/meta";
		
		Replace *replace = [Replace withInit];
		replace.cmdId = @"replace/cmdId";
		replace.meta = @"replace/meta";
		
		Delete *delete = [Delete withInit];
		delete.cmdId = @"delete/cmdId";
		delete.meta = @"delete/meta";
		delete.archive = YES;
		delete.softDelete = YES;
		
		for(int i=0;i<2;i++)
		{
			Item *item = [Item withInit];
			item.target = @"target";
			item.source = @"source";
			item.meta = @"meta";
			item.data = @"data";
			
			[add addItem:item];
			[replace addItem:item];
			[delete addItem:item];
		}
		
		[command addOperation:add];
		[command addOperation:replace];
		[command addOperation:delete];
	}
	
	NSString *xml = [SyncXMLGenerator generateCommand:command];
	
	NSLog(@"%@",xml);
}

-(void) testGenerateInitMessageXml
{
	NSLog(@"Starting testGenerateInitMessageXml...");
	
	Session *session = [Session withInit];
	SyncMessage *message = [SyncMessage withInit];
	Credential *credential = [Credential withInit];
	
	session.source = @"session/source";
	session.target = @"session/target";
	session.sessionId = @"session/sessionId";
	
	message.messageId = @"messageId";
	credential.type = @"credential/type";
	credential.data = @"credential/data";
	message.credential = credential;
	message.final = YES;
	
	for(int i=0; i<2; i++)
	{
		Status *status = [Status withInit];
		status.cmdId = @"cmdId";
		status.cmd = @"cmd";
		status.msgRef = @"msgRef";
		status.cmdRef = @"cmdRef";
		status.data = @"data";
		
		Alert *alert = [Alert withInit];
		alert.cmdId = @"cmdId";
		alert.data = @"data";
		for(int i=0; i<3; i++)
		{
			Item *item = [Item withInit];
			item.target = @"target";
			item.source = @"source";
			item.meta = @"meta";
			item.data = @"data";
			item.moreData = YES;
			
			[status addItem:item];
			[alert addItem:item];
		}	
		
		[message addStatus:status];
		[message addAlert:alert];
	}
	
	NSString *xml = [SyncXMLGenerator generateInitMessage:session :message];
	
	NSLog(@"%@",xml);
}
 
-(void) testGenerateSyncMessageXml
{
	NSLog(@"Starting testGenerateSyncMessageXml...");
	
	Session *session = [Session withInit];
	SyncMessage *message = [SyncMessage withInit];
	Credential *credential = [Credential withInit];
	
	session.source = @"session/source";
	session.target = @"session/target";
	session.sessionId = @"session/sessionId";
	
	message.messageId = @"messageId";
	credential.type = @"credential/type";
	credential.data = @"credential/data";
	message.credential = credential;
	message.final = YES;
	
	for(int i=0; i<2; i++)
	{
		Status *status = [Status withInit];
		status.cmdId = @"cmdId";
		status.cmd = @"cmd";
		status.msgRef = @"msgRef";
		status.cmdRef = @"cmdRef";
		status.data = @"data";
		
		Alert *alert = [Alert withInit];
		alert.cmdId = @"cmdId";
		alert.data = @"data";
		for(int i=0; i<3; i++)
		{
			Item *item = [Item withInit];
			item.target = @"target";
			item.source = @"source";
			item.meta = @"meta";
			item.data = @"data";
			item.moreData = YES;
			
			[status addItem:item];
			[alert addItem:item];
		}
		
		SyncCommand *command = [SyncCommand withInit];
		command.cmdId = @"cmdId";
		command.target = @"target";
		command.source = @"source";
		command.meta = @"meta";
		command.numberOfChanges = @"10";
		
		[message addStatus:status];
		[message addAlert:alert];
		[message addCommand:command];
	}
	
	NSString *xml = [SyncXMLGenerator generateSyncMessage:session :message];
	
	NSLog(@"%@",xml);
}

-(void) testParseSyncXml
{
	NSLog(@"Starting testParseSyncXml...");
	
	Session *session = [Session withInit];
	SyncMessage *message = [SyncMessage withInit];
	Credential *credential = [Credential withInit];
	
	session.source = @"session/source";
	session.target = @"session/target";
	session.sessionId = @"session/sessionId";
	
	message.messageId = @"messageId";
	credential.type = @"credential/type";
	credential.data = @"credential/data";
	message.credential = credential;
	message.final = YES;
	
	for(int i=0; i<2; i++)
	{
		Status *status = [Status withInit];
		status.cmdId = @"cmdId";
		status.cmd = @"cmd";
		status.msgRef = @"msgRef";
		status.cmdRef = @"cmdRef";
		status.data = @"data";
		
		Alert *alert = [Alert withInit];
		alert.cmdId = @"cmdId";
		alert.data = @"data";
		for(int i=0; i<3; i++)
		{
			Item *item = [Item withInit];
			item.target = @"target";
			item.source = @"source";
			item.meta = @"meta";
			item.data = @"data";
			item.moreData = YES;
			
			[status addItem:item];
			[alert addItem:item];
		}
		
		SyncCommand *command = [SyncCommand withInit];
		command.cmdId = @"cmdId";
		command.target = @"target";
		command.source = @"source";
		command.meta = @"meta";
		command.numberOfChanges = @"10";
		
		[message addStatus:status];
		[message addAlert:alert];
		[message addCommand:command];
	}
	
	NSString *xml = [SyncXMLGenerator generateSyncMessage:session :message];
	//NSLog(@"%@",xml);	
	
	SyncObjectGenerator *objectGenerator = [SyncObjectGenerator withInit];
	Session *parsedSession = [objectGenerator parse:xml];
	SyncMessage *parsedMessage = parsedSession.currentMessage;
	Status *parsedStatus = (Status *)[parsedMessage.status objectAtIndex:0];
	SyncCommand *parsedCommand = (SyncCommand *)[parsedMessage.syncCommands objectAtIndex:0];
	Alert *parsedAlert = (Alert *)[parsedMessage.alerts objectAtIndex:0];
	
	//Assert the parsed session object
	NSLog(@"Session Source: %@",parsedSession.source);
	NSLog(@"Session Target: %@",parsedSession.target);
	NSLog(@"Session Id: %@",parsedSession.sessionId);
	
	NSLog(@"MessageId: %@",parsedMessage.messageId);
	NSLog(@"MessageFinal: %d",parsedMessage.final);
	
	NSLog(@"Status cmdId: %@",parsedStatus.cmdId);
	NSLog(@"Status cmd: %@",parsedStatus.cmd);
	NSLog(@"Status msgRef: %@",parsedStatus.msgRef);
	NSLog(@"Status cmdRef: %@",parsedStatus.cmdRef);
	NSLog(@"Status data: %@",parsedStatus.data);
	NSArray *statusItems = parsedStatus.items;
	for(Item *item in statusItems)
	{
		NSLog(@"Status Item Target: %@",item.target);
		NSLog(@"Status Item Source: %@",item.source);
		NSLog(@"Status Item Meta: %@",item.meta);
		NSLog(@"Status Item Data: %@",item.data);
		NSLog(@"Status Item MoreData: %d",item.moreData);
	}
	
	NSLog(@"Alert cmdId: %@",parsedAlert.cmdId);
	NSLog(@"Alert data: %@",parsedAlert.data);
	NSArray *alertItems = parsedAlert.items;
	for(Item *item in alertItems)
	{
		NSLog(@"Alert Item Target: %@",item.target);
		NSLog(@"Alert Item Source: %@",item.source);
		NSLog(@"Alert Item Meta: %@",item.meta);
		NSLog(@"Alert Item Data: %@",item.data);
		NSLog(@"Alert Item MoreData: %d",item.moreData);
	}
	
	NSLog(@"SyncCommand cmdId: %@",parsedCommand.cmdId);
	NSLog(@"SyncCommand target: %@",parsedCommand.target);
	NSLog(@"SyncCommand source: %@",parsedCommand.source);
	NSLog(@"SyncCommand meta: %@",parsedCommand.meta);
	NSLog(@"SyncCommand numberOfChanges: %@",parsedCommand.numberOfChanges);
}
@end