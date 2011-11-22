//
//  LocationResponse.h
//  mobilecloudlib
//
//  Created by openmobster on 11/18/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "GenericAttributeManager.h"


@interface LocationResponse : NSObject 
{
    @private
    GenericAttributeManager *attrMgr;
}

/**
 * Creates an instance of the Location Response
 *
 */
+(id) withInit;

/**
 * Sets arbitrary attributes representing the contextual data associated with this particular location response
 * 
 * @param name name of the attribute
 * @param value value of the attribute
 */
-(void) setAttribute:(NSString *)name :(NSString *)value;

/**
 * Gets an arbitrary attribute value from the location response
 * 
 * @param name name of the attribute
 * @return value of the attribute
 */
-(NSString *) getAttribute:(NSString *)name;

/**
 * Sets related "list" of data representing the contextual data associated 
 * with this particular location response
 * 
 * @param name name of this attribute
 * @param list the list to be set
 */
-(void) setListAttribute:(NSString *)name :(NSArray *)list;

/**
 * Gets a related "list" of data representing the contextual data associated 
 * with this particular location response
 * 
 * @param name name of the attribute
 * @return the list associated with this attribute
 */
-(NSArray *) getListAttribute:(NSString *)name;

/**
 * Sets related "map" of string data representing the contextual data associated 
 * with this particular location response
 * 
 * @param name
 * @param map
 */
-(void) setMapAttribute:(NSString *)name :(NSDictionary *)map;

/**
 * Gets a related "map" of string data representing the contextual data associated 
 * with this particular location response
 * 
 * @param name
 * @return
 */
-(NSDictionary *)getMapAttribute:(NSString *)name;

/**
 * Gets all the names that identify values of attributes in the location response
 * 
 * @return
 */
-(NSArray *)getNames;


/**
 * Removes an attribute associated with the location response
 * 
 * @param name
 */
-(void) removeAttribute:(NSString *) name;


/**
 * Gets an arbitrary attribute value from the location response
 * 
 * @param name
 * @return
 */
-(id) get:(NSString *) name;

@end
