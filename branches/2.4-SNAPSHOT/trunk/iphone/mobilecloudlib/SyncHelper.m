/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "SyncHelper.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation SyncHelper

+(void)cleanupChangeLog:(Context *)context :(Session *)session
{
	SyncMessage *currentMessage = session.currentMessage;
	NSString *syncType = session.syncType;
	WorkflowManager *manager = (WorkflowManager *)[context getAttribute:@"manager"];
	SyncEngine *engine = [manager engine];
	NSString *activeNode = [manager activeNode];
	
	//If this is a SlowSync, ChangeLog not needed...blow them away on device and the cloud
	if([syncType isEqualToString:_SLOW_SYNC] && [activeNode isEqualToString:@"start_close"])
	{
		@try
		{
			[engine clearChangeLog];
		}
		@catch (NSException * e) 
		{
			[ErrorHandler handleException:e];
		}
		return;
	}
	
	NSArray *statuses = currentMessage.status;
	for(Status *local in statuses)
	{
		@try 
		{
			NSString *command = local.cmd;
			NSString *data = local.data;
			
			if(([command isEqualToString:_Add] || 
			   [command isEqualToString:_Delete] ||
			   [command isEqualToString:_Replace]) &&
			   (
				[data isEqualToString:_SUCCESS] ||
				[data isEqualToString:_CHUNK_SUCCESS]
			   )
			)
			{
				ChangeLogEntry *changeLogEntry = [session findClientLogEntry:local];
				if(changeLogEntry != nil)
				{
					//TODO: Long Object Support related logic...not urgent..may be never
					//needed
					[engine clearChangeLogEntry:changeLogEntry];
				}
			}
		}
		@catch (NSException * e) 
		{
			[ErrorHandler handleException:e];
		}
	}
}

+(SyncMessage *)normalSync:(Context *)context :(Session *)session
{
	NSString *syncType = session.syncType;
	
	int cmdId = 1; //id for the first command in the message
	
	//this as new commands are added to this message
	SyncMessage *reply = [SyncHelper setUpReply:session];
	
	//Process incoming commands from the Cloud
	[SyncHelper processSyncCommands:context :cmdId :session: reply];
	
	//Send any sync commands from the device side
	SyncCommand *syncCommand = session.activeCommand;
	if(
		[syncType isEqualToString:_TWO_WAY] ||
	    [syncType isEqualToString:_ONE_WAY_CLIENT]
	)
	{
        int numOfCommands = 1; //make this link to a constant whose value can be changed
        
        if(syncCommand == nil)
        {
            syncCommand = [SyncHelper generateSyncCommand:context :cmdId :session :reply];
            
            session.activeOperations = [syncCommand allOperations];
            
            [syncCommand clear];
            session.activeCommand = syncCommand;
        }
        else
        {
            [syncCommand clear];
        }
        
        if([session isOperationSyncActive])
        {
            syncCommand = session.activeCommand;
            [syncCommand clear];
            for(int i=0; i<numOfCommands; i++)
            {
                AbstractOperation *object = [session getNextOperation];
                if(object == nil)
                {
                    break;
                }
                [syncCommand addOperation:object];
            }
        }
        
        [reply.syncCommands removeAllObjects];
        [reply addCommand:syncCommand];
	}
    else if([syncType isEqualToString:_SLOW_SYNC])
    {
        syncCommand = [SyncHelper generateSyncCommand:context :cmdId :session :reply];
    }
	
	[SyncHelper setUpClientSyncFinal:session :reply :syncCommand];
    if([syncType isEqualToString:_TWO_WAY] ||
       [syncType isEqualToString:_ONE_WAY_CLIENT]
    )
    {
        if([session isOperationSyncFinished])
        {
            reply.final = YES;
        }
        else
        {
            reply.final = NO;
        }
    }
	
	return reply;
}

+(SyncMessage *)bootSync:(Context *)context :(Session *)session
{
    if(session.hasSyncExecutedOnce)
    {
        WorkflowManager *manager = (WorkflowManager *)[context getAttribute:@"manager"];
        Session *activeSession = [manager activeSession];
        
        SyncMessage *incoming = activeSession.currentMessage;
        SyncMessage *outgoing = [SyncMessage withInit];
        int messageId = [incoming.messageId intValue];
        messageId++;
        outgoing.messageId = [NSString stringWithFormat:@"%d",messageId];
        
        int cmdId = 1; //id for the first command in this outgoing message
        
        //Consume the domain data by passing to the sync engine
        [SyncHelper processSyncCommands:context :cmdId :activeSession :outgoing];
        
        //ChangeLog Support
        [SyncHelper cleanupChangeLog:context :activeSession];
        
        //TODO: Map Support
        
        //Make this the final
        outgoing.final = YES;
        
        return outgoing;
    }
    
    
    
	int cmdId = 1; //id for the first command in this message
	SyncMessage *reply = [SyncHelper setUpReply:session];
	WorkflowManager *manager = (WorkflowManager *)[context getAttribute:@"manager"];
	SyncEngine *engine = [manager engine];
	
	SyncCommand *syncCommand = [SyncCommand withInit];
	syncCommand.cmdId = [NSString stringWithFormat:@"%d",cmdId++];
	syncCommand.source = [session findDataSource];
	syncCommand.target = [session findDataTarget];
	
	[engine startBootSync:session channel:syncCommand.target];
	
	[SyncHelper setUpClientSyncFinal:session :reply :syncCommand];
	
	return reply;
}

+(SyncMessage *)streamSync:(Context *)context :(Session *)session
{
	int cmdId = 1; //id for the first command in this message
	SyncMessage *reply = [SyncHelper setUpReply:session];
	
	SyncCommand *syncCommand = [SyncCommand withInit];
	syncCommand.cmdId = [NSString stringWithFormat:@"%d",cmdId++];
	syncCommand.source = [session findDataSource];
	syncCommand.target = [session findDataTarget];
	
	Add *add = [Add withInit];
	add.cmdId = [NSString stringWithFormat:@"%d",cmdId++];
	add.meta = (NSString *)[session getAttribute:_STREAM_RECORD_ID];
	[syncCommand addOperation:add];
	
	[reply addCommand:syncCommand];
	
	[SyncHelper setUpClientSyncFinal:session :reply :syncCommand];
	
	return reply;
}

+(void)processSyncCommands:(Context *) context :(int)cmdId :(Session *)session :(SyncMessage *)replyMessage
{
	WorkflowManager *manager = (WorkflowManager *)[context getAttribute:@"manager"];
	SyncEngine *engine = [manager engine];
	NSMutableArray *status = [NSMutableArray array];
	SyncMessage *currentMessage = session.currentMessage;
	
	NSArray *commands = currentMessage.syncCommands;
	NSString *syncType = session.syncType;
	if(commands == nil || [commands count] == 0)
	{
		//nothing to process
		return;
	}
	
	for(SyncCommand *syncCommand in commands)
	{
		Status *syncStatus = [Status withInit];
		syncStatus.cmdId = [NSString stringWithFormat:@"%d",cmdId++];
		syncStatus.cmd = _Sync;
		syncStatus.data = _SUCCESS;
		syncStatus.msgRef = currentMessage.messageId;
		syncStatus.cmdRef = syncCommand.cmdId;
		[syncStatus addSourceRef:syncCommand.source];
		[syncStatus addTargetRef:syncCommand.target];
		[status addObject:syncStatus];
		
		//hooking into the device side sync engine
		NSArray *processingStatus = nil;
		if(![syncType isEqualToString:_SLOW_SYNC])
		{
			processingStatus = [engine processSyncCommand:session channel:syncCommand.target syncCommand:syncCommand];
		}
		else 
		{
			processingStatus = [engine processSlowSyncCommand:session channel:syncCommand.target syncCommand:syncCommand];
		}
		
		//Process the status
		for(Status *local in processingStatus)
		{
			local.cmdId = [NSString stringWithFormat:@"%d",cmdId++];
			local.msgRef = currentMessage.messageId;
			[status addObject:local];
			[replyMessage addStatus:local];
		}
	}
}

//TODO: Long Object support...may be never
+(SyncCommand *)generateSyncCommand:(Context *) context :(int)cmdId :(Session *)session :(SyncMessage *)replyMessage
{
	WorkflowManager *manager = (WorkflowManager *)[context getAttribute:@"manager"];
	SyncEngine *engine = [manager engine];
	SyncCommand *syncCommand = [SyncCommand withInit];
	syncCommand.cmdId = [NSString stringWithFormat:@"%d",cmdId++];
	syncCommand.source = [session findDataSource];
	syncCommand.target = [session findDataTarget];
	NSString *syncType = session.syncType;
	int maxClientSize = session.maxClientSize;
	NSString *source = syncCommand.source;
	
	@try 
	{
		if(![syncType isEqualToString:_SLOW_SYNC])
		{
			NSArray *commands = [engine getAddCommands:maxClientSize channel:source syncType:syncType];
			for(AbstractOperation *local in commands)
			{
				local.cmdId = [NSString stringWithFormat:@"%d",cmdId++];
				[syncCommand addOperation:local];
			}
			
			commands = [engine getReplaceCommands:maxClientSize channel:source syncType:syncType];
			for(AbstractOperation *local in commands)
			{
				local.cmdId = [NSString stringWithFormat:@"%d",cmdId++];
				[syncCommand addOperation:local];
			}
			
			commands = [engine getDeleteCommands:source syncType:syncType];
			for(AbstractOperation *local in commands)
			{
				local.cmdId = [NSString stringWithFormat:@"%d",cmdId++];
				[syncCommand addOperation:local];
			}
		}
		else 
		{
			NSArray *slowSync = [engine getSlowSyncCommands:maxClientSize channel:source];
			for(AbstractOperation *local in slowSync)
			{
				local.cmdId = [NSString stringWithFormat:@"%d",cmdId++];
				[syncCommand addOperation:local];
			}
		}
	}
	@catch (NSException * e) 
	{
		[ErrorHandler handleException:e];
	}
	
	[replyMessage addCommand:syncCommand];
		
	return syncCommand;
}

+(void)setUpClientSyncFinal:(Session *)session :(SyncMessage *)reply :(SyncCommand *)syncCommand
{
	reply.final = YES;
}

+(SyncMessage *)setUpReply:(Session *)session
{
	SyncMessage *syncMessage = [SyncMessage withInit];
	
	SyncMessage *currentMessage = session.currentMessage;
	int messageId = [currentMessage.messageId intValue];
	messageId++;
	
	syncMessage.messageId = [NSString stringWithFormat:@"%d",messageId];
	
	return syncMessage;
}

+(BOOL)containsFinal:(NSArray *)messages
{
	if(messages != nil)
	{
		for(SyncMessage *local in messages)
		{
			if(local.final)
			{
				return YES;
			}
		}
	}
	
	return NO;
}


+(BOOL)initSuccess:(Session *)session
{
	NSArray *messages = session.serverInitPackage.messages;
	if(messages != nil)
	{
		for(SyncMessage *local in messages)
		{
			NSArray *statuses = local.status;
			if(statuses != nil)
			{
				for(Status *localStatus in statuses)
				{
					NSString *data = localStatus.data;
					if([data isEqualToString:_SUCCESS])
					{
						return true;
					}
				}
			}
		}
	}
	
	return NO;
}

+(BOOL)authorized:(Session *)session
{
	NSArray *messages = session.serverInitPackage.messages;
	if(messages != nil)
	{
		for(SyncMessage *local in messages)
		{
			NSArray *statuses = local.status;
			if(statuses != nil)
			{
				for(Status *localStatus in statuses)
				{
					NSString *data = localStatus.data;
					if([data isEqualToString:_AUTHENTICATION_FAILURE])
					{
						return NO;
					}
				}
			}
		}
	}
	return YES;
}

+(void)processNextNonce:(Session *)session
{
	Credential *credential = nil;
	
	NSArray *messages = session.serverInitPackage.messages;
	if(messages != nil)
	{
		for(SyncMessage *local in messages)
		{
			NSArray *statuses = local.status;
			if(statuses != nil)
			{
				for(Status *localStatus in statuses)
				{
					credential = localStatus.credential;
					if(credential != nil)
					{
						break;
					}
				}
			}
		}
	}
	
	if(credential != nil)
	{
		Configuration *conf = [Configuration getInstance];
		conf.authenticationNonce = credential.nextNonce;
		[conf saveInstance];
	}
}

+(BOOL)hasErrors:(SyncMessage *)currentMessage
{
	NSArray *alerts = currentMessage.alerts;
	if(alerts != nil)
	{
		for(Alert *local in alerts)
		{
			NSString *data = local.data;
			
			if([data isEqualToString:_ANCHOR_FAILURE])
			{
				return YES;
			}
		}
	}
	
	return NO;
}
@end
