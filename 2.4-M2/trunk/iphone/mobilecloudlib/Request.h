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
 *  Represents a service request that will be sent to the cloud side Mobile Service Bean component
 * 
 * @author openmobster@gmail.com
 */
@interface Request : NSObject 
{
	@private
	GenericAttributeManager *attrMgr;
	NSString *service;
}

@property (assign) NSString *service;

/**
 * Creates a service request
 * 
 * @param service - the unique identifier of the cloud side Mobile Service Bean component
 */
+(id) withInit:(NSString *)service;

/**
 * Sets arbitrary attributes representing the contextual data associated with this particular service request
 * 
 * @param name name of the attribute
 * @param value value of the attribute
 */
-(void) setAttribute:(NSString *)name :(NSString *)value;

/**
 * Gets an arbitrary attribute value from the service request
 * 
 * @param name name of the attribute
 * @return value of the attribute
 */
-(NSString *) getAttribute:(NSString *)name;

/**
 * Gets all the names that identify values of attributes in the service request
 * 
 * @return all the attribute names
 */
-(NSArray *) getNames;

/**
 * Gets all the values of attributes in the service request
 * 
 * @return all the attribute values
 */
-(NSArray *) getValues;

/**
 * Removes an attribute associated with the service request
 * 
 * @param name attribute to be removed from the object
 */
-(void) removeAttribute:(NSString *)name;

/**
 * Sets related "list" of data representing the contextual data associated 
 * with this particular service request
 * 
 * @param name name of this attribute
 * @param list the list to be set
 */
-(void) setListAttribute:(NSString *)name :(NSArray *)list;

/**
 * Gets a related "list" of data representing the contextual data associated 
 * with this particular service request
 * 
 * @param name name of the attribute
 * @return the list associated with this attribute
 */
-(NSArray *) getListAttribute:(NSString *)name;

@end
