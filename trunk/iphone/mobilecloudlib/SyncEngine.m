/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "SyncEngine.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation SyncEngine

+(id) withInit
{
	SyncEngine *syncEngine = [[[SyncEngine alloc] init] autorelease];
	return syncEngine;
}

//Read operations
-(NSArray *) getSlowSyncCommands:(int) messageSize channel:(NSString *) channel
{
	NSMutableArray *commands = [NSMutableArray array];
	MobileObjectDatabase *db = [MobileObjectDatabase getInstance];
	NSArray *all = [db readAll:channel];
	
	if(all != nil)
	{
		for(MobileObject *local in all)
		{
			AbstractOperation *command = [self getAddCommand:local];
			[commands addObject:command];
		}
	}
	
	return [NSArray arrayWithArray:commands];
}

-(NSArray *) getAddCommands:(int) messageSize channel:(NSString *) channel syncType:(NSString *) syncType
{
	NSMutableArray *commands = [NSMutableArray array];	
	NSArray *changelog = [self getChangeLog:channel operation:_Add];
	MobileObjectDatabase *db = [MobileObjectDatabase getInstance];
	if(changelog != nil)
	{
		for(ChangeLogEntry *local in changelog)
		{
			MobileObject *cour = [db read:channel :local.recordId];
			if(cour != nil)
			{
				//Create a Sync Add Command from this record data
				AbstractOperation *command = [self getAddCommand:cour];
				[commands addObject:command];
			}
		}
	}
	
	return [NSArray arrayWithArray:commands];
}

-(NSArray *) getReplaceCommands:(int) messageSize channel:(NSString *) channel syncType:(NSString *) syncType
{
	NSMutableArray *commands = [NSMutableArray array];	
	NSArray *changelog = [self getChangeLog:channel operation:_Replace];
	MobileObjectDatabase *db = [MobileObjectDatabase getInstance];
	if(changelog != nil)
	{
		for(ChangeLogEntry *local in changelog)
		{
			MobileObject *cour = [db read:channel :local.recordId];
			if(cour != nil)
			{
				//Create a Sync Replace Command from this record data
				AbstractOperation *command = [self getReplaceCommand:cour];
				[commands addObject:command];
			}
		}
	}
	
	return [NSArray arrayWithArray:commands];
}

-(NSArray *) getDeleteCommands:(NSString *) channel syncType:(NSString *) syncType
{
	NSMutableArray *commands = [NSMutableArray array];	
	NSArray *changelog = [self getChangeLog:channel operation:_Delete];
	MobileObjectDatabase *db = [MobileObjectDatabase getInstance];
	if(changelog != nil)
	{
		for(ChangeLogEntry *local in changelog)
		{
			MobileObject *cour = [db read:channel :local.recordId];
			if(cour != nil)
			{
				//Create a Sync Delete Command from this record data
				Delete *command = [Delete withInit];
				NSString *id = [self marshalId:local.recordId];
				Item *item = [Item withInit];
				
				item.data = id;
				[command addItem:item];
				
				[commands addObject:command];
			}
		}
	}
	
	return [NSArray arrayWithArray:commands];
}

//write operations
-(NSArray *) processSlowSyncCommand:(Session *) session channel:(NSString *) channel syncCommand:(SyncCommand *) syncCommand
{
	NSMutableArray *status = [NSMutableArray array];
	NSArray *allOperations = [syncCommand allOperations];
	
	if(allOperations != nil)
	{
		for(AbstractOperation *operation in allOperations)
		{
			if([operation isChunked])
			{
				continue;
			}
			
			Item *item = (Item *)[operation.items objectAtIndex:0];
			if([operation isKindOfClass:[Add class]])
			{
				@try 
				{
					MobileObject *mobileObject = [self unmarshal:channel :item.data];
				
					//delete record
					[self deleteRecord:session :mobileObject];
				
					//save record
					[self saveRecord:session :mobileObject];
				
					Status *local = [self getStatus:_SUCCESS :operation];
					[status addObject:local];
				}
				@catch (NSException * e) 
				{
					Status *local = [self getStatus:_COMMAND_FAILURE :operation];
					[status addObject:local];
				}
			}
			else if([operation isKindOfClass:[Delete class]])
			{
				@try
				{
					MobileObject *mobileObject = [self unmarshal:channel :item.data];
				
					//delete record
					[self deleteRecord:session :mobileObject];
				
					Status *local = [self getStatus:_SUCCESS :operation];
					[status addObject:local];
				}
				@catch (NSException * e) 
				{
					Status *local = [self getStatus:_COMMAND_FAILURE :operation];
					[status addObject:local];
				}
			}
		}
	}
	
	return [NSArray arrayWithArray:status];
}

-(NSArray *) processSyncCommand:(Session *) session channel:(NSString *) channel syncCommand:(SyncCommand *) syncCommand
{
	NSMutableArray *status = [NSMutableArray array];
	
	//FIXME: Cloud Push integration
	
	//Process the Add commands
	[self processAddCommands:status :channel :session :syncCommand];
	
	//Process the Replace commands
	[self processReplaceCommands:status :channel :session :syncCommand];
	
	//Process the Delete commands
	[self processDeleteCommands:status :channel :session :syncCommand];
	
	return [NSArray arrayWithArray:status];
}

-(void) startBootSync:(Session *)session channel:(NSString *)channel
{
	[self clearAll:session :channel];
	[self clearChangeLog:channel];
}

//helper operations
-(void) processAddCommands:(NSMutableArray *) status :(NSString *) channel :(Session *)session :(SyncCommand *)syncCommand
{
	NSArray *commands = [syncCommand addCommands];
	if(commands != nil)
	{
		for(Add *command in commands)
		{
			//Long Object Support
			if([command isChunked])
			{
				continue;
			}
			
			@try
			{
				Item *item = (Item *)[command.items objectAtIndex:0];
				MobileObject *mobileObject = [self unmarshal:channel :item.data];
			
				//save record
				NSString *objectId = [self saveRecord:session :mobileObject];
			
				//FIXME:Cloud Push integration
			
				Status *local = [self getStatus:_SUCCESS :command];
				[status addObject:local];
			}
			@catch (NSException * e) 
			{
				Status *local = [self getStatus:_COMMAND_FAILURE :command];
				[status addObject:local];
			}
		}
	}
}

-(void) processReplaceCommands:(NSMutableArray *) status :(NSString *)channel :(Session *)session :(SyncCommand *)syncCommand
{
	NSArray *commands = [syncCommand replaceCommands];
	if(commands != nil)
	{
		for(Replace *command in commands)
		{
			//Long Object Support
			if([command isChunked])
			{
				continue;
			}
			
			@try
			{
				Item *item = (Item *)[command.items objectAtIndex:0];
				MobileObject *mobileObject = [self unmarshal:channel :item.data];
			
				//save record
				NSString *objectId = [self saveRecord:session :mobileObject];
			
				//FIXME:Cloud Push integration
			
				Status *local = [self getStatus:_SUCCESS :command];
				[status addObject:local];
			}
			@catch (NSException * e) 
			{
				Status *local = [self getStatus:_COMMAND_FAILURE :command];
				[status addObject:local];
			}
		}
	}
}

-(void) processDeleteCommands:(NSMutableArray *) status :(NSString *)channel :(Session *)session :(SyncCommand *)syncCommand
{
	NSArray *commands = [syncCommand deleteCommands];
	if(commands != nil)
	{
		for(Delete *command in commands)
		{
			//Long Object Support
			if([command isChunked])
			{
				continue;
			}
			
			@try
			{
				Item *item = (Item *)[command.items objectAtIndex:0];
				MobileObject *mobileObject = [self unmarshal:channel :item.data];
			
				//delete record
				[self deleteRecord:session :mobileObject];
			
				//FIXME:Cloud Push integration
			
				Status *local = [self getStatus:_SUCCESS :command];
				[status addObject:local];
			}
			@catch (NSException * e) 
			{
				Status *local = [self getStatus:_COMMAND_FAILURE :command];
				[status addObject:local];
			}
		}
	}
}

-(AbstractOperation *) getAddCommand:(MobileObject *)mobileObject
{
	AbstractOperation *commandInfo = [Add withInit];
	
	Item *item = [Item withInit];
	NSString *xml = [self marshal:mobileObject];
	[item setData:xml];
	[commandInfo addItem:item];
	
	return commandInfo;
}

-(AbstractOperation *)getReplaceCommand:(MobileObject *)mobileObject
{
	AbstractOperation *commandInfo = [Replace withInit];
	
	Item *item = [Item withInit];
	NSString *xml = [self marshal:mobileObject];
	[item setData:xml];
	[commandInfo addItem:item];
	
	return commandInfo;
}

-(NSString *)marshalId:(NSString *)id
{
	NSMutableString *buffer = [NSMutableString string];
	
	[buffer appendString:@"<mobileObject>\n"];
	[buffer appendFormat:@"<recordId>%@</recordId>\n",[XMLUtil cleanupXML:id]];
	[buffer appendString:@"</mobileObject>\n"];
	
	return [NSString stringWithString:buffer];
}

-(NSString *)marshal:(MobileObject *)mobileObject
{
	NSMutableString *buffer = [NSMutableString string];
	DeviceSerializer *serializer = [DeviceSerializer getInstance];
	
	NSString *xml = [serializer serialize:mobileObject];
	[buffer appendString:xml];
	
	return [NSString stringWithString:buffer];
}

-(MobileObject *)unmarshal:(NSString *)channel :(NSString *)xml
{
	DeviceSerializer *serializer = [DeviceSerializer getInstance];
	MobileObject *mobileObject = [serializer deserialize:xml];
	
	mobileObject.service = channel;
	
	return mobileObject;
}

-(void)clearAll:(Session *)session :(NSString *)channel
{
	MobileObjectDatabase *database = [MobileObjectDatabase getInstance];
	[database deleteAll:channel];
}

-(Status *)getStatus:(NSString *)statusCode :(AbstractOperation *)operation
{
	Status *status = [Status withInit];
	
	if([operation isKindOfClass:[Add class]])
	{
		status.cmd = _Add;
	}
	else if([operation isKindOfClass:[Replace class]])
	{
		status.cmd = _Replace;
	}
	else if([operation isKindOfClass:[Delete class]])
	{
		status.cmd = _Delete;
	}
	
	status.data = statusCode;
	status.cmdRef = operation.cmdId;
	Item *item = (Item *)[operation.items objectAtIndex:0];
	
	if(![StringUtil isEmpty:item.source])
	{
		[status addSourceRef:item.source];
	}
	
	return status;
}

//ChangeLog related functions
-(NSArray *) getChangeLog:(NSString *) channel operation:(NSString *) operation
{
	NSMutableArray *local = [NSMutableArray array];
	
	NSArray *changelog = [ChangeLogEntry all];
	if(changelog != nil)
	{
		for(ChangeLogEntry *entry in changelog)
		{
			NSString *localChannel = entry.nodeId;
			NSString *localOperation = entry.operation;
			
			if([localChannel isEqualToString:channel] &&
			   [localOperation isEqualToString:operation]
			)
			{
				[local addObject:entry];
			}
		}
	}
	
	return [NSArray arrayWithArray:local];
}

-(void) addChangeLogEntries:(NSArray *) entries
{
	if(entries != nil)
	{
		for(GenericAttributeManager *local in entries)
		{
			NSString *nodeId = [local getAttribute:@"nodeId"];
			NSString *operation = [local getAttribute:@"operation"];
			NSString *recordId = [local getAttribute:@"recordId"];
			[ChangeLogEntry getInstance:nodeId :operation :recordId]; 
		}
	}
}

-(void) clearChangeLog
{
	[ChangeLogEntry deleteAll];
}

-(void) clearChangeLogEntry:(ChangeLogEntry *) logEntry
{
	[ChangeLogEntry delete:logEntry];
}

-(void) clearChangeLog:(NSString *)channel
{
	NSArray *changelog = [ChangeLogEntry all];
	if(changelog != nil)
	{
		NSMutableArray *entries = [NSMutableArray array];
		for(ChangeLogEntry *entry in changelog)
		{
			NSString *localChannel = entry.nodeId;
			if([localChannel isEqualToString:channel])
			{
				[entries addObject:entry];
			}
		}
		[ChangeLogEntry deleteEntries:entries];
	}	
}

//anchor related
-(Anchor *) createNewAnchor:(NSString *)target
{
	Anchor *currentAnchor = [Anchor getInstance:target];
	
	if(![StringUtil isEmpty:currentAnchor.oid])
	{
		//Calculate the next sync
		NSString *nextSync = [self generateSync];
		currentAnchor.lastSync = currentAnchor.nextSync;
		currentAnchor.nextSync = nextSync;
	}
	else 
	{
		//This is the first time the anchor is established for the target
		currentAnchor.oid = [self generateSync];
		currentAnchor.target = target;
		
		//Calculate the last sync
		NSString *lastSync = [self generateSync];
		currentAnchor.lastSync = lastSync;
		
		//Calculate the next sync
		currentAnchor.nextSync = lastSync;
	}
	
	[currentAnchor saveInstance];

	return currentAnchor;
}

-(NSString *) generateSync
{
	return [GeneralTools generateUniqueId];
}

//Error related
-(SyncError *) saveError:(NSString *)source :(NSString *)target :(NSString *)code
{
	return [SyncError getInstance:code :source :target];
}

-(void) removeError:(NSString *)source :(NSString *)target :(NSString *)code
{
	SyncError *syncError = [self saveError:source :target :code];
	[SyncError delete:syncError];
}

-(NSArray *)readErrors
{
	return [SyncError all];
}

//Record persistence related
-(NSString *)saveRecord:(Session *)session :(MobileObject *)mobileObject
{
	NSString *recordId = mobileObject.recordId;
	MobileObjectDatabase *db = [MobileObjectDatabase getInstance];
	MobileObject *mo = [db read:mobileObject.service :recordId];
	
	if(mo != nil)
	{
		//update
		mobileObject.recordId = mo.recordId;
		[db update:mobileObject];
	}
	else 
	{
	    //create
		recordId = [db create:mobileObject];
		
		//TODO: when record mapping is added..not needed now
		/*
		if(serverId != nil && deviceId != nil)
		{
			
		}*/
	}

	
	return recordId;
}

-(void)deleteRecord:(Session *)session :(MobileObject *)mobileObject
{
	NSString *recordId = mobileObject.recordId;
	MobileObjectDatabase *db = [MobileObjectDatabase getInstance];
	MobileObject *objectToDelete = [db read:mobileObject.service :recordId];
	if(objectToDelete != nil)
	{
		[db delete:objectToDelete];
	}
}
@end