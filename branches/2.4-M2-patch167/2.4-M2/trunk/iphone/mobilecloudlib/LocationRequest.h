//
//  LocationRequest.h
//  mobilecloudlib
//
//  Created by openmobster on 11/18/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "GenericAttributeManager.h"

/*!
 Represents a Location-oriented request to be made to the Cloud
 
 @author openmobster@gmail.com
 */
@interface LocationRequest : NSObject 
{
    @private
    GenericAttributeManager *attrMgr;
    NSString *service;
}

@property(nonatomic,retain)NSString *service;

/**
 * Creates an instance of the Location Request
 *
 * @param service - unique identifier of the Cloud-side Location Bean
 */
+(id) withInit:(NSString *)service;

/**
 * Sets arbitrary attributes representing the contextual data associated with this particular location request
 * 
 * @param name name of the attribute
 * @param value value of the attribute
 */
-(void) setAttribute:(NSString *)name :(NSString *)value;

/**
 * Gets an arbitrary attribute value from the location request
 * 
 * @param name name of the attribute
 * @return value of the attribute
 */
-(NSString *) getAttribute:(NSString *)name;

/**
 * Sets related "list" of data representing the contextual data associated 
 * with this particular location request
 * 
 * @param name name of this attribute
 * @param list the list to be set
 */
-(void) setListAttribute:(NSString *)name :(NSArray *)list;

/**
 * Gets a related "list" of data representing the contextual data associated 
 * with this particular location request
 * 
 * @param name name of the attribute
 * @return the list associated with this attribute
 */
-(NSArray *) getListAttribute:(NSString *)name;

/**
 * Sets related "map" of string data representing the contextual data associated 
 * with this particular location request
 * 
 * @param name
 * @param map
 */
-(void) setMapAttribute:(NSString *)name :(NSDictionary *)map;

/**
 * Gets a related "map" of string data representing the contextual data associated 
 * with this particular location request
 * 
 * @param name
 * @return
 */
-(NSDictionary *)getMapAttribute:(NSString *)name;

/**
 * Gets all the names that identify values of attributes in the service request
 * 
 * @return
 */
-(NSArray *)getNames;


/**
 * Removes an attribute associated with the service request
 * 
 * @param name
 */
-(void) removeAttribute:(NSString *) name;


/**
 * Gets an arbitrary attribute value from the service response
 * 
 * @param name
 * @return
 */
-(id) get:(NSString *) name;


/**
 * Gets the the unique identifier of the server side Mobile Service Bean component
 * 
 * @return
 */
-(NSString *) getService;
@end
