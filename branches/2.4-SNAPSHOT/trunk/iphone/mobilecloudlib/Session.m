#import "Session.h"


@implementation Session

@synthesize sessionId;
@synthesize target;
@synthesize source;
@synthesize anchor;
@synthesize app;

@synthesize clientInitPackage;
@synthesize serverInitPackage;
@synthesize clientSyncPackage;
@synthesize serverSyncPackage;
@synthesize clientClosePackage;
@synthesize serverClosePackage;

@synthesize currentMessage;
@synthesize phaseCode;
@synthesize syncType;
@synthesize maxClientSize;

@synthesize state;
@synthesize backgroundSync;
@synthesize hasSyncExecutedOnce;
@synthesize activeCommand;
@synthesize activeOperations;

@synthesize snapshotSize;

+(id) withInit
{
	Session *session = [[[Session alloc] init] autorelease];
	
	if(session.clientInitPackage == nil)
	{
		session.clientInitPackage = [SyncPackage withInit];
	}
	
	if(session.clientSyncPackage == nil)
	{
		session.clientSyncPackage = [SyncPackage withInit];
	}
	
	if(session.clientClosePackage == nil)
	{
		session.clientClosePackage = [SyncPackage withInit];
	}
	
	if(session.serverInitPackage == nil)
	{
		session.serverInitPackage = [SyncPackage withInit];
	}
	
	if(session.serverSyncPackage == nil)
	{
		session.serverSyncPackage = [SyncPackage withInit];
	}
	
	if(session.serverClosePackage == nil)
	{
		session.serverClosePackage = [SyncPackage withInit];
	}
	
	if(session.state == nil)
	{
		session.state = [GenericAttributeManager withInit];
	}
	
	return session;
}

-(NSString *) findDataSource:(SyncMessage *) message
{
	NSArray *alerts = message.alerts;
	
	if(alerts != nil)
	{
		for(Alert* alert in alerts)
		{
			NSArray *items = alert.items;
			if(items != nil)
			{
				for(Item *item in items)
				{
					NSString *itemSource = item.source;
					if(![StringUtil isEmpty:itemSource])
					{
						return itemSource;
					}
				}
			}
		}
	}
	
	return nil;
}

-(NSString *) findDataTarget:(SyncMessage *) message
{
	NSArray *alerts = message.alerts;
	
	if(alerts != nil)
	{
		for(Alert* alert in alerts)
		{
			NSArray *items = alert.items;
			if(items != nil)
			{
				for(Item *item in items)
				{
					NSString *itemTarget = item.target;
					if(![StringUtil isEmpty:itemTarget])
					{
						return itemTarget;
					}
				}
			}
		}
	}
	
	return nil;
}

-(NSString *) findDataSource
{
	NSMutableArray *all = [NSMutableArray array];
	[all addObjectsFromArray:self.clientInitPackage.messages];
	[all addObjectsFromArray:self.clientSyncPackage.messages];
	
	for(SyncMessage *cour in all)
	{
		NSString *dataSource = [self findDataSource:cour];
		if(![StringUtil isEmpty:dataSource])
		{
			return dataSource;
		}
	}
	
	return nil;
}

-(NSString *) findDataTarget
{
	NSMutableArray *all = [NSMutableArray array];
	[all addObjectsFromArray:self.clientInitPackage.messages];
	[all addObjectsFromArray:self.clientSyncPackage.messages];
	
	for(SyncMessage *cour in all)
	{
		NSString *dataTarget = [self findDataTarget:cour];
		if(![StringUtil isEmpty:dataTarget])
		{
			return dataTarget;
		}
	}
	return nil;
}

-(AbstractOperation *) findOperationCommand:(Status *) status
{
	NSString *cmd = status.cmd;
	NSString *messageRef = status.msgRef;
	NSString *cmdRef = status.cmdRef;
	
	NSMutableArray *syncMessages = [NSMutableArray array];
	[syncMessages addObjectsFromArray:self.clientSyncPackage.messages];
	
	for(SyncMessage *syncMessage in syncMessages)
	{
		NSString *messageId = syncMessage.messageId;
		
		if(![messageId isEqualToString:messageRef])
		{
			continue;
		}
		
		NSArray *commands = syncMessage.syncCommands;
		for(SyncCommand *command in commands)
		{
			NSArray *operationCommands = nil;
			if([cmd isEqualToString:_Add])
			{
				operationCommands = command.addCommands;
			}
			else if([cmd isEqualToString:_Replace])
			{
				operationCommands = command.replaceCommands;
			}
			else if([cmd isEqualToString:_Delete])
			{
				operationCommands = command.deleteCommands;
			}				
			
			for(AbstractOperation *op in operationCommands)
			{
				NSString *cmdId = op.cmdId;
				if([cmdId isEqualToString:cmdRef])
				{
					return op;
				}
			}
		}
	}
	
	return nil;
}

-(void)setAttribute:(NSString *)name :(id)value
{
	[state setAttribute:name :value];
}

-(id)getAttribute:(NSString *)name
{
	return [state getAttribute:name];
}

-(void)removeAttribute:(NSString *)name
{
	[state removeAttribute:name];
}

-(ChangeLogEntry *)findClientLogEntry:(Status *)status
{
	NSString *cmd = status.cmd;
	NSString *messageRef = status.msgRef;
	NSString *cmdRef = status.cmdRef;
	
	NSArray *messages = self.clientSyncPackage.messages;
	if(messages != nil)
	{
		for(SyncMessage *local in messages)
		{
			NSString *msgId = local.messageId;
			if(![msgId isEqualToString:messageRef])
			{
				continue;
			}
			
			NSArray *syncCommands = local.syncCommands;
			if(syncCommands != nil)
			{
				for(SyncCommand *command in syncCommands)
				{
					NSString *channel = command.source;
					
					NSArray *operations = nil;
					if([cmd isEqualToString:_Add])
					{
						operations = command.addCommands;
					}
					else if([cmd isEqualToString:_Replace])
					{
						operations = command.replaceCommands;
					}
					else if([cmd isEqualToString:_Delete])
					{
						operations = command.deleteCommands;
					}
					
					if(operations != nil)
					{
						for(AbstractOperation *operation in operations)
						{
							if([operation.cmdId isEqualToString:cmdRef])
							{
								NSArray *items = operation.items;
								if(items != nil)
								{
									Item *item = (Item *)[items objectAtIndex:0];
									
									DeviceSerializer *serializer = [DeviceSerializer getInstance];
									MobileObject *mobileObject = [serializer deserialize:item.data];
									
									//We got a match
									ChangeLogEntry *changelogEntry = [ChangeLogEntry getInstance:channel :cmd :mobileObject.recordId];
									return changelogEntry;
								}
							}
						}
					}
				}
			}
		}
	}
	return nil;
}

-(AbstractOperation *)getNextOperation
{
    if(self.activeOperations != nil && [self.activeOperations count]>0)
    {
        AbstractOperation *next = (AbstractOperation *)[self.activeOperations objectAtIndex:0]; 
        
        //remove this object from the list of active operations
        [self.activeOperations removeObjectAtIndex:0];
        
        return next;
    }
    return nil;
}

-(BOOL)isOperationSyncFinished
{
    if(self.activeOperations == nil || [self.activeOperations count]==0)
    {
        self.activeOperations = nil;
        return YES;
    }
    return NO;
}

-(BOOL)isOperationSyncActive
{
    if(self.activeOperations != nil)
    {
        return YES;
    }
    return NO;
}


-(BOOL) isSnapshotSizeSet
{
    if(self.snapshotSize > 0)
    {
        return YES;
    }
    return NO;
}
@end
