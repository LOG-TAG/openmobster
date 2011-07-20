/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import <Foundation/Foundation.h>
#import "Field.h"
#import "ArrayMetaData.h"
#import "StringUtil.h"

/**
 * 
 * @author openmobster@gmail.com
 */
@interface MobileObject : NSObject 
{
	@private
	NSString *service;
	NSString *recordId;
	NSString *serverRecordId;
	BOOL proxy;
	BOOL createdOnDevice;
	BOOL locked;
	NSString *dirtyStatus;
	NSMutableArray *fields;
	NSMutableArray *arrayMetaData;
}

@property (retain) NSString *service; //not-null
@property (retain) NSString *recordId; //not-null
@property (retain) NSString *serverRecordId;
@property (retain) NSString *dirtyStatus;
@property (retain) NSMutableArray *fields;
@property (retain) NSMutableArray *arrayMetaData;
@property (assign) BOOL proxy;
@property (assign) BOOL createdOnDevice;
@property (assign) BOOL locked;

+(id) withInit;

-(NSString *) getValue:(NSString *)uri;
-(void) setValue:(NSString *)uri value:(NSString *)value;

-(int) getArrayLength:(NSString *) arrayUri;
-(void) clearArray:(NSString *) indexedPropertyName;
-(void) removeArrayElement:(NSString *) arrayUri :(int) elementAt;
-(NSDictionary *) getArrayElement:(NSString *)arrayUri :(int) elementIndex;
-(void) addToArray:(NSString *) indexedPropertyName :(NSDictionary *) properties;


//used internally
-(Field *) findField:(NSString *)inputUri;
-(BOOL) doesUriMatch:(NSString *)inputUri :(NSString *)fieldUri;
-(int) calculateArrayIndex:(NSString *)fieldUri;
-(NSString *) calculateArrayUri:(NSString *)fieldUri;
-(ArrayMetaData *) findArrayMetaData:(NSString *)arrayUri;
-(NSArray *) findArrayFields:(NSString *) arrayUri;
-(NSArray *) findArrayElementFields:(NSString *) arrayUri :(int) elementIndex;
-(int) findIndexValueInsertionPoint:(NSString *)indexedPropertyName;

//some convenience methods
-(void)addField:(Field *)field;
-(void)addArrayMetaData:(ArrayMetaData *)arrayMetaData;
@end