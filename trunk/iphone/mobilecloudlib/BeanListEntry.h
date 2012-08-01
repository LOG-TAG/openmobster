/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import <Foundation/Foundation.h>
#import "StringUtil.h"

/*!
 BeanListEntry represent members of a BeanList
 @author openmobster@gmail.com
 */
@interface BeanListEntry : NSObject 
{
	@private
	NSMutableDictionary *properties;
	int index;
	NSString *listProperty;
}

@property (assign) int index;
@property (nonatomic,retain) NSString *listProperty;
@property (nonatomic,retain) NSMutableDictionary *properties;

+(BeanListEntry *)withInit:(int) index :(NSDictionary *) properties :(NSString *)listProperty;
+(BeanListEntry *)withInit:(NSString *)listProperty;

/**
 * Reads a property value on this object using a property expression
 * 
 * @return the value of the specified property
 */
-(NSString *) getProperty:(NSString *)propertyExpression;

/**
 * Set the property value on this object using a property expression
 * 
 * @param propertyExpression expression to signify the property to be set
 * @param value value to be set
 */
-(void)setProperty:(NSString *)propertyExpression :(NSString *)value;

/**
 * Reads a binary property value on this object using a property expression
 * 
 * @return the value of the specified property
 */
-(NSData *) getBinaryProperty:(NSString *)propertyExpression;

/**
 * Set the binary property value on this object using a property expression
 * 
 * @param propertyExpression expression to signify the property to be set
 * @param value value to be set
 */
-(void)setBinaryProperty:(NSString *)propertyExpression :(NSData *)value;

/**
 * If this object carries a single property, read it using getValue
 * 
 * @return the value of this object
 */
-(NSString *)getValue;

/**
 * Sets the single property of this object
 * 
 * @param value value to set
 */
-(void)setValue:(NSString *)value;

/**
 * All the properties of this object
 * 
 * @return all the properties of this object
 */
-(NSDictionary *)getProperties;

//internal methods
/**
 * This method is for internal use only
 */
-(NSString *)calculatePropertyUri:(NSString *)propertyExpression;
@end
