/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import <Foundation/Foundation.h>
#import "Session.h"
#import "ChangeLogEntry.h"
#import "SyncError.h"
#import "Anchor.h"
#import "SyncDataSource.h"
#import "GeneralTools.h"
#import "MobileObject.h"
#import "MobileObjectDatabase.h"
#import "DeviceSerializer.h"
#import "SyncXMLTags.h"
#import "StringUtil.h"
#import "XMLUtil.h"
#import "SyncConstants.h"

/**
 * 
 * @author openmobster@gmail.com
 */
@interface SyncEngine : NSObject 
{

}

+(id) withInit;

//reading sync related
-(NSArray *) getSlowSyncCommands:(int) messageSize channel:(NSString *) channel;
-(NSArray *) getAddCommands:(int) messageSize channel:(NSString *) channel syncType:(NSString *) syncType;
-(NSArray *) getReplaceCommands:(int) messageSize channel:(NSString *) channel syncType:(NSString *) syncType;
-(NSArray *) getDeleteCommands:(NSString *) channel syncType:(NSString *) syncType;

//processing sync related
-(NSArray *) processSlowSyncCommand:(Session *) session channel:(NSString *) channel syncCommand:(SyncCommand *) syncCommand;
-(NSArray *) processSyncCommand:(Session *) session channel:(NSString *) channel syncCommand:(SyncCommand *) syncCommand;
-(void) startBootSync:(Session *)session channel:(NSString *)channel;

//changelog related
-(NSArray *) getChangeLog:(NSString *) channel operation:(NSString *) operation;
-(void) addChangeLogEntries:(NSArray *) entries;
-(void) clearChangeLogEntry:(ChangeLogEntry *) logEntry;
-(void) clearChangeLog;
-(void) clearChangeLog:(NSString *)channel;

//anchor related
-(Anchor *) createNewAnchor:(NSString *)target;

//error related
-(SyncError *) saveError:(NSString *)source :(NSString *)target :(NSString *)code;
-(void) removeError:(NSString *)source :(NSString *)target :(NSString *)code;
-(NSArray *)readErrors;

//utility
-(NSString *) generateSync;

//some internal operations
-(AbstractOperation *) getAddCommand:(MobileObject *)mobileObject;
-(AbstractOperation *) getReplaceCommand:(MobileObject *)mobileObject;
-(NSString *)marshalId:(NSString *)id;
-(NSString *)marshal:(MobileObject *)mobileObject;
-(MobileObject *)unmarshal:(NSString *)channel :(NSString *)xml;
-(void)clearAll:(Session *)session :(NSString *)channel;
-(Status *)getStatus:(NSString *)statusCode :(AbstractOperation *)operation;
-(void) processAddCommands:(NSMutableArray *) status :(NSString *)channel :(Session *)session :(SyncCommand *)syncCommand;
-(void) processReplaceCommands:(NSMutableArray *) status :(NSString *)channel :(Session *)session :(SyncCommand *)syncCommand;
-(void) processDeleteCommands:(NSMutableArray *) status :(NSString *)channel :(Session *)session :(SyncCommand *)syncCommand;
-(NSString *)saveRecord:(Session *)session :(MobileObject *)mobileObject;
-(void)deleteRecord:(Session *)session :(MobileObject *)mobileObject;
@end
