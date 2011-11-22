//
//  LocationContext.h
//  mobilecloudlib
//
//  Created by openmobster on 11/18/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "GenericAttributeManager.h"
#import "Address.h"
#import "Place.h"
#import "LocationRequest.h"
#import "LocationResponse.h"


@interface LocationContext : NSObject 
{
    @private
    GenericAttributeManager *attrMgr;
}

+(id)withInit;

/**
 * Sets arbitrary attributes representing the contextual data associated with this context
 * 
 * @param name
 * @param value
 */
-(void) setAttribute:(NSString *) name :(id) value;

/**
 * Gets an arbitrary attribute value from the context
 * 
 * @param name
 * @return
 */
-(id) getAttribute:(NSString *) name;

/**
 * Gets all the names that identify values of attributes in the context
 * 
 * @return
 */
-(NSArray *) getNames;

/**
 * Gets all the values of attributes in the context
 * 
 * @return
 */
-(NSArray *) getValues;

/**
 * Removes an attribute
 * 
 * @param name
 */
-(void) removeAttribute:(NSString *) name;

/**
 * Returns the Latitude of the address associated with this context
 * 
 * @return latitude
 */
-(NSString *) getLatitude;


/**
 * Sets the Latitude of the address associated with this context
 * 
 * @param latitude
 */
-(void) setLatitude:(NSString *) latitude;


/**
 * Gets the Longitude of the address associated with this context
 * 
 * @return longitude
 */
-(NSString *) getLongitude;


/**
 * Sets the Longitude of the address associated with this context
 * 
 * @param longitude
 */
-(void) setLongitude:(NSString *) longitude;

/**
 * Gets the Address associated with this context
 * 
 * @return address
 */
-(Address *) getAddress;

/**
 * Sets the Address associated with this context
 * 
 * @param address
 */
-(void) setAddress:(Address *) address;


/**
 * Gets a list of places near the address associated with the context
 * 
 * @return places
 */
-(NSArray *) getNearbyPlaces;

/**
 * Sets the list of places near the address associated with the context
 * 
 * @param places
 */
-(void) setNearbyPlaces:(NSArray *) places;

/**
 * Get the details associated with a place
 * 
 * @return
 */
-(Place *) getPlaceDetails;

/**
 * Set the details associated with a place
 * 
 * @param placeDetails
 */
-(void) setPlaceDetails:(Place *) placeDetails;

/**
 * Set the type of places to search for
 * 
 * @return
 */
-(NSArray *) getPlaceTypes;

/**
 * Get the type of places to be searched for
 * 
 * @param placeTypes
 */
-(void) setPlaceTypes:(NSArray *) placeTypes;

/**
 * 
 * @return
 */
-(NSString *) getPlaceReference;

/**
 * 
 * @param placeReference
 */
-(void) setPlaceReference:(NSString *) placeReference;

/**
 * Set the active request to be sent with a cloud invocation
 * 
 * @param request
 */
-(void) setRequest:(LocationRequest *) request;

/**
 * Get the active request that will be sent with a cloud invocation
 * 
 * @return the request
 */
-(LocationRequest *) getRequest;

/**
 * Set the active response received as a result of cloud invocation
 * 
 * @param response
 */
-(void) setResponse:(LocationResponse *) response;

/**
 * Get the active response received as a result of cloud invocation
 * 
 * @return the response
 */
-(LocationResponse *) getResponse;
@end
