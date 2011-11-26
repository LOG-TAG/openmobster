//
//  TestLocationService.m
//  mobilecloudlib
//
//  Created by openmobster on 11/23/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import "TestLocationService.h"
#import "LocationRequest.h"
#import "LocationContext.h"
#import "LocationResponse.h"
#import "LocationService.h"
#import "Address.h"


@implementation TestLocationService

+(id)withInit
{
    TestLocationService *instance = [[[TestLocationService alloc] init] autorelease];
    return instance;
}

-(NSString *)getInfo
{
    NSString *info = [NSString stringWithFormat:@"%@%@",[[self class] description],@"LocationService"];
	return info;  
}

-(void) runTest
{
    [self testByCoordinates];
    [self testByAddress];
}

-(void) testByCoordinates
{
    LocationRequest *request = [LocationRequest withInit:@"friends"];
    LocationContext *context = [LocationContext withInit];
    
    [context setLatitude:@"-33.8670522"];
    [context setLongitude:@"151.1957362"];
    
    [context setRadius:1000];
    
    LocationService *locationService = [LocationService withInit];
    
    LocationContext *responseContext = [locationService invoke:request :context];
    
    //Get the nearby places
    NSArray *nearbyPlaces = [responseContext getNearbyPlaces];
    for(Place *place in nearbyPlaces)
    {
        NSLog(@"Name: %@",place.name);
    }
    
    //Assert address
    Address *address = [responseContext getAddress];
    NSLog(@"Street: %@",address.street);
    NSLog(@"City: %@",address.city);
    NSLog(@"ZipCode: %@",address.zipCode);
    [self assertTrue :[address.street isEqualToString:@"37 Pirrama Rd"] :@"/address/street/check"];
    [self assertTrue :[address.city isEqualToString:@"Sydney"] :@"/address/city/check"];
    [self assertTrue :[address.zipCode isEqualToString:@"2009"] :@"/address/zipCode/check"];
    [self assertTrue :[address.latitude isEqualToString:@"-33.867052"] :@"/address/latitude/check"];
    [self assertTrue :[address.longitude isEqualToString:@"151.195736"] :@"/address/longitude/check"]; 
}

-(void) testByAddress
{
    LocationRequest *request = [LocationRequest withInit:@"friends"];
    LocationContext *context = [LocationContext withInit];
    
    Address *address = [Address withInit];
    address.street = @"2046 Dogwood Gardens Drive";
    address.city = @"Germantown";
    [context setAddress:address];
    
    [context setRadius:1000];
    
    LocationService *locationService = [LocationService withInit];
    
    LocationContext *responseContext = [locationService invoke:request :context];
    
    //Get the nearby places
    NSArray *nearbyPlaces = [responseContext getNearbyPlaces];
    for(Place *place in nearbyPlaces)
    {
        NSLog(@"Name: %@",place.name);
    }
    
    //Assert address
    address = [responseContext getAddress];
    NSLog(@"Street: %@",address.street);
    NSLog(@"City: %@",address.city);
    NSLog(@"ZipCode: %@",address.zipCode);
    NSLog(@"Latitude: %@",address.latitude);
    NSLog(@"Longitude: %@",address.longitude);
    [self assertTrue :[address.street isEqualToString:@"2046 Dogwood Gardens Dr"] :@"/address/street/check"];
    [self assertTrue :[address.city isEqualToString:@"Germantown"] :@"/address/city/check"];
    [self assertTrue :[address.zipCode isEqualToString:@"38139"] :@"/address/zipCode/check"];
    [self assertTrue :[address.latitude isEqualToString:@"35.093039"] :@"/address/latitude/check"];
    [self assertTrue :[address.longitude isEqualToString:@"-89.733933"] :@"/address/longitude/check"];
}

@end
