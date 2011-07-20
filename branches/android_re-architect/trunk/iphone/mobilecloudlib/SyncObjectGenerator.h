/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import <Foundation/Foundation.h>
#import "Session.h"
#import "SyncMessage.h"
#import "Alert.h"
#import "SyncCommand.h"
#import "Status.h"
#import "Item.h"
#import "Add.h"
#import "Replace.h"
#import "Delete.h"
#import "Credential.h"
#import "StringUtil.h"

/**
 * 
 * @author openmobster@gmail.com
 */
@interface SyncObjectGenerator : NSObject<NSXMLParserDelegate>
{
	@private
	Session *session;
	SyncMessage *syncMessage;
	Alert *courAlert;
	Status *courStatus;
	Item *courItem;
	SyncCommand *courCommand;
	Add *courAdd;
	Replace *courReplace;
	Delete *courDelete;
	
	NSMutableString *fullPath;
	NSMutableString *dataBuffer;
	
	//some helper
	NSString *alertPath;
	NSString *statusPath;
	NSString *syncPath;
	NSString *addPath;
	NSString *replacePath;
	NSString *deletePath;
	NSString *itemPath;
	NSString *moreDataPath;
	NSString *finalPath;
	NSString *deleteArchivePath;
	NSString *deleteSoftDeletePath;
	
	NSString *sessionIdPath;
	NSString *sourceUriPath;
	NSString *targetUriPath;
	NSString *msgIdPath;
	NSString *maxMsgSizePath;
}

+(id) withInit;

-(Session *) parse:(NSString *) syncXml;

@end
