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
 * Represents a Response sent back by an invocation of the server side Mobile Service Bean component
 *
 * @author openmobster@gmail.com
 */
@interface Response : NSObject 
{
	@private
	GenericAttributeManager *attrMgr;
}

/**
 * Creates an instance of the Response
 */
+(id) withInit;

/**
 * Sets arbitrary attributes representing the contextual data associated with this particular service response
 * 
 * @param name name of the attribute
 * @param value value of the attribute
 */
-(void) setAttribute:(NSString *)name :(NSString *)value;

/**
 * Gets an arbitrary attribute value from the service response
 * 
 * @param name name of the attribute
 * @return value of the attribute
 */
-(NSString *) getAttribute:(NSString *)name;

/**
 * Gets all the names that identify values of attributes in the service response
 * 
 * @return all the attribute names of the response
 */
-(NSArray *) getNames;

/**
 * Gets all the values of attributes in the service response
 * 
 * @return all the values of attributes
 */
-(NSArray *) getValues;

/**
 * Removes an attribute associated with the service response
 * 
 * @param name name of the attribute to be removed
 */
-(void) removeAttribute:(NSString *)name;

/**
 * Sets related "list" of data representing the contextual data associated 
 * with this particular service request
 * 
 * @param name name of the attribute
 * @param list list to be set as value
 */
-(void) setListAttribute:(NSString *)name :(NSArray *)list;

/**
 * Gets a related "list" of data representing the contextual data associated 
 * with this particular service request
 * 
 * @param name name of the list attribute
 * @return the list associated with this attribute
 */
-(NSArray *) getListAttribute:(NSString *)name;

/**
 * Gets the status code of the response
 * 
 * @return the status code
 */
-(NSString *)getStatusCode;

/**
 * Sets the status code of the response
 * 
 * @param statusCode the status code
 */
-(void) setStatusCode:(NSString *)statusCode;

/**
 * Get the status message set on the response
 * 
 * @return the status message
 */
-(NSString *)getStatusMsg;

/**
 * Sets the status message of the response
 * 
 * @param statusMsg the status message
 */
-(void) setStatusMsg:(NSString *)statusMsg;

@end
