/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import <Foundation/Foundation.h>
#import "MobileObject.h"
#import "SystemException.h"
#import "StringUtil.h"
#import "MobileObjectDatabase.h"
#import "BeanList.h"
#import "BeanListEntry.h"
#import "MobileBeanMetaData.h"
#import "SyncService.h"
#import "ErrorHandler.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@interface MobileBean : NSObject 
{
	@private
	MobileObject *data;
	BOOL isDirty;
	BOOL isNew;
	BOOL readonly;
}

@property (retain) MobileObject *data;
@property (assign) BOOL isDirty;
@property (assign) BOOL isNew;
@property (assign) BOOL readonly;


+(id)withInit:(MobileObject *)data;
+(MobileBean *)newInstance:(NSString *)channel;
+(MobileBean *)readById:(NSString *)channel :(NSString *)oid;
+(NSArray *)readAll:(NSString *)channel;
+(NSArray *)filterProxies:(NSArray *)mobileObjects;
+(BOOL)isBooted:(NSString *)channel;


-(NSString *)getChannel;
-(NSString *)getId;
-(NSString *)getCloudId;
-(BOOL)isInitialized;
-(BOOL)isCreatedOnDevice;
-(BOOL)isProxy;
-(NSString *)getValue:(NSString *)fieldUri;
-(void)setValue:(NSString *)fieldUri :(NSString *)value;

-(BeanList *)readList:(NSString *)listProperty;
-(void)saveList:(BeanList *)list;
-(void)clearList:(NSString *)listProperty;
-(void)addBean:(NSString *)listProperty :(BeanListEntry *)bean;
-(void)removeBean:(NSString *)listProperty :(int) elementAt;

-(void)clearAll;
-(void)clearMetaData;
-(void)delete;
-(void)save;
-(void)refresh;
@end
