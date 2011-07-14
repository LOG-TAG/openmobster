/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import <Foundation/Foundation.h>
#import "GenericAttributeManager.h"
#import "StringUtil.h"

/**
 * 
 * @author openmobster@gmail.com
 */
@interface Response : NSObject 
{
	@private
	GenericAttributeManager *attrMgr;
}

+(id) withInit;

-(void) setAttribute:(NSString *)name :(NSString *)value;
-(NSString *) getAttribute:(NSString *)name;
-(NSArray *) getNames;
-(NSArray *) getValues;
-(void) removeAttribute:(NSString *)name;
-(void) setListAttribute:(NSString *)name :(NSArray *)list;
-(NSArray *) getListAttribute:(NSString *)name;
-(NSString *)getStatusCode;
-(void) setStatusCode:(NSString *)statusCode;
-(NSString *)getStatusMsg;
-(void) setStatusMsg:(NSString *)statusMsg;

@end
