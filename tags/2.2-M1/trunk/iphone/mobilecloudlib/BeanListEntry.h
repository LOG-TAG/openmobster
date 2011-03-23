/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import <Foundation/Foundation.h>
#import "StringUtil.h"

/**
 * 
 * @author openmobster@gmail.com
 */
@interface BeanListEntry : NSObject 
{
	@private
	NSMutableDictionary *properties;
	int index;
	NSString *listProperty;
}

@property (assign) int index;
@property (assign) NSString *listProperty;

+(BeanListEntry *)withInit:(int) index :(NSDictionary *) properties :(NSString *)listProperty;

-(NSString *) getProperty:(NSString *)propertyExpression;
-(void)setProperty:(NSString *)propertyExpression :(NSString *)value;
-(NSString *)getValue;
-(void)setValue:(NSString *)value;
-(NSDictionary *)getProperties;

//internal methods
-(NSString *)calculatePropertyUri:(NSString *)propertyExpression;
@end
