/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import <Foundation/Foundation.h>
#import "Session.h"
#import "SyncXMLTags.h"
#import "SyncConstants.h"
#import "MobileObject.h"
#import "DeviceSerializer.h"
#import "Context.h"
#import "WorkflowManager.h"
#import "SyncEngine.h"
#import "ErrorHandler.h"
#import "SyncMessage.h"
#import "Credential.h"
#import "Configuration.h"

/**
 * 
 * @author openmobster@gmail.com
 */
@interface SyncHelper : NSObject 
{
}

//Sync related
+(void)cleanupChangeLog:(Context *)context :(Session *)session;
+(SyncMessage *)normalSync:(Context *)context :(Session *)session;
+(SyncMessage *)bootSync:(Context *)context :(Session *)session;
+(SyncMessage *)streamSync:(Context *)context :(Session *)session;

//Workflow Orchestration related
+(BOOL)containsFinal:(NSArray *)messages;
+(BOOL)initSuccess:(Session *)session;
+(BOOL)authorized:(Session *)session;
+(void)processNextNonce:(Session *)session;
+(BOOL)hasErrors:(SyncMessage *)currentMessage;

//Used internally
+(SyncMessage *)setUpReply:(Session *)session;
+(void)setUpClientSyncFinal:(Session *)session :(SyncMessage *)reply :(SyncCommand *)syncCommand;
+(void)processSyncCommands:(Context *) context :(int)cmdId :(Session *)session :(SyncMessage *)replyMessage;
+(void)processSyncCommands:(Context *) context :(int)cmdId :(Session *)session :(SyncMessage *)replyMessage;
+(SyncCommand *)generateSyncCommand:(Context *) context :(int)cmdId :(Session *)session :(SyncMessage *)replyMessage;

@end