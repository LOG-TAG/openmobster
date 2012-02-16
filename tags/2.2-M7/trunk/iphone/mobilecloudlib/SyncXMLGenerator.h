/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import <Foundation/Foundation.h>
#import "SyncXMLTags.h"
#import "Anchor.h"
#import "Item.h"
#import "Status.h"
#import "Alert.h"
#import "SyncCommand.h"
#import "Add.h"
#import "Replace.h"
#import "Delete.h"
#import "SyncMessage.h"
#import "Session.h"
#import "Credential.h"
#import "XMLUtil.h"
#import "StringUtil.h"

/**
 * 
 * @author openmobster@gmail.com
 */
@interface SyncXMLGenerator : NSObject 

//invoked by external clients
+(NSString *) generateInitMessage:(Session *)session :(SyncMessage *)message;
+(NSString *) generateSyncMessage:(Session *)session :(SyncMessage *)message;
+(NSString *) generateAnchor:(Anchor *) anchor;

//these are invoked internally
+(NSString *) generateItem:(Item *)item;
+(NSString *) generateStatus:(Status *)status;
+(NSString *) generateAlert:(Alert *)alert;
+(NSString *) generateCommand:(SyncCommand *)command;
+(NSString *) generateAdd:(Add *)add;
+(NSString *) generateReplace:(Replace *)replace;
+(NSString *) generateDelete:(Delete *)delete;

@end
