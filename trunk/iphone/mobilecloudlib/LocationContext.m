//
//  LocationContext.m
//  mobilecloudlib
//
//  Created by openmobster on 11/18/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import "LocationContext.h"


@implementation LocationContext

-(id)init
{
    if(self == [super init])
    {
        attrMgr = [[GenericAttributeManager alloc] initWithRetention];
    }
    return self;
}

+(id)withInit
{
    LocationContext *instance = [[[LocationContext alloc] init] autorelease];
    return instance;
}

-(void)dealloc
{
    [attrMgr release];
    [super dealloc];
}


-(void) setAttribute:(NSString *) name :(id) value
{
    [attrMgr setAttribute:name :value];
}


-(id) getAttribute:(NSString *) name
{
    return [attrMgr getAttribute:name];
}

-(NSArray *) getNames
{
    return [attrMgr getNames];
}

-(NSArray *) getValues
{
    return [attrMgr getValues];
}


-(void) removeAttribute:(NSString *) name
{
    [attrMgr removeAttribute:name];
}


-(NSString *) getLatitude
{
    return [attrMgr getAttribute:@"latitude"];
}



-(void) setLatitude:(NSString *) latitude
{
    [attrMgr setAttribute:@"latitude" :latitude];
}



-(NSString *) getLongitude
{
    return [attrMgr getAttribute:@"longitude"];
}


-(void) setLongitude:(NSString *) longitude
{
    [attrMgr setAttribute:@"longitude" :longitude];
}


-(Address *) getAddress;
{
    return (Address *)[attrMgr getAttribute:@"address"];
}

-(void) setAddress:(Address *) address
{
    [attrMgr setAttribute:@"address" :address];
}


-(NSArray *) getNearbyPlaces
{
    return (NSArray *)[attrMgr getAttribute:@"places"];
}


-(void) setNearbyPlaces:(NSArray *) places
{
    [attrMgr setAttribute:@"places" :places];
}


-(Place *) getPlaceDetails
{
    return (Place *)[attrMgr getAttribute:@"placeDetails"];
}


-(void) setPlaceDetails:(Place *) placeDetails
{
    [attrMgr setAttribute:@"placeDetails" :placeDetails];
}


-(NSArray *) getPlaceTypes
{
    return (NSArray *)[attrMgr getAttribute:@"placeTypes"];
}


-(void) setPlaceTypes:(NSArray *) placeTypes
{
    [attrMgr setAttribute:@"placeTypes" :placeTypes];
}


-(NSString *) getPlaceReference
{
    return (NSString *)[attrMgr getAttribute:@"placeReference"];
}


-(void) setPlaceReference:(NSString *) placeReference
{
    [attrMgr setAttribute:@"placeReference" :placeReference];
}


-(void) setRequest:(LocationRequest *) request
{
    [attrMgr setAttribute:@"request" :request];
}


-(LocationRequest *) getRequest
{
    return (LocationRequest *)[attrMgr getAttribute:@"request"];
}


-(void) setResponse:(LocationResponse *) response
{
    [attrMgr setAttribute:@"response" :response];
}


-(LocationResponse *) getResponse
{
    return (LocationResponse *)[attrMgr getAttribute:@"response"];
}

@end
