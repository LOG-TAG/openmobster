/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>
#import "CloudDBManager.h"
#import "Field.h"
#import "ArrayMetaData.h"
#import "MobileObject.h"
#import "GeneralTools.h"

/**
 * 
 * @author openmobster@gmail.com
 */
@interface PersistentMobileObject : NSManagedObject 
{

}
@property (nonatomic,retain) NSString *oid;
@property (nonatomic,retain) NSString *service;
@property (nonatomic,retain) NSArray *fields;
@property (nonatomic,retain) NSArray *arrayMetaData;
@property (nonatomic,retain) NSString *serverRecordId;
@property (nonatomic,retain) NSNumber *proxy;
@property (nonatomic,retain) NSNumber *createdOnDevice;
@property (nonatomic,retain) NSNumber *locked;
@property (nonatomic,retain) NSString *dirtyStatus;
@property (nonatomic,retain) NSString *nameValuePairs;

+(PersistentMobileObject *) newInstance:(NSString *)channel;
+(NSArray *) findByChannel:(NSString *) channel;
+(PersistentMobileObject *) findByOID:(NSString *)oid;
+(BOOL) delete:(PersistentMobileObject *)mobileObject;
+(BOOL) deleteAll:(NSString *)channel;
-(BOOL) saveInstance;

//some convenience methods
-(void) addField:(Field *) field;
-(NSArray *)parseFields;

-(void) addArrayMetaData:(ArrayMetaData *)arrayMetaData;
-(NSArray *)parseArrayMetaData;

-(MobileObject *)parseMobileObject;
-(void)setState:(MobileObject *)mobileObject;

@end
