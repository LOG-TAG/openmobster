//
//  LocationService.m
//  mobilecloudlib
//
//  Created by openmobster on 11/23/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import "LocationService.h"
#import "NetSession.h"
#import "Configuration.h"
#import "NetworkConnector.h"
#import "CloudPayload.h"
#import "PayloadHandler.h"


@implementation LocationService

+(LocationContext *)invoke:(LocationRequest *)request :(LocationContext *)context
{
    if(request == nil || context == nil)
    {
        NSException *exception = [NSException exceptionWithName:@"IllegalStateException" reason:@"LocationService Validation Failure" userInfo:nil];
        @throw exception;
    }
    
    //LocationContext Validation
    BOOL coordinatesMissing = NO;
    NSString *latitude = [context getLatitude];
    NSString *longitude = [context getLongitude];
    if([StringUtil isEmpty:latitude] || [StringUtil isEmpty:longitude])
    {
        coordinatesMissing = YES;
    }
    
    BOOL addressMissing = NO;
    if([context getAddress] == nil)
    {
        addressMissing = YES;
    }
    
    BOOL referenceMissing = NO;
    NSString *reference = [context getPlaceReference];
    if([StringUtil isEmpty:reference])
    {
        referenceMissing = YES;
    }
    
    if(coordinatesMissing && addressMissing && referenceMissing)
    {
        NSException *exception = [NSException exceptionWithName:@"IllegalStateException" reason:@"LocationContext Validation Failure" userInfo:nil];
        @throw exception;
    }
    
    NetSession *session = NULL;
	@try
	{
		Configuration *conf = [Configuration getInstance];
		BOOL secure = [conf.sslActive boolValue];
		BOOL isActive = [conf.active boolValue];
		NetworkConnector *connector = [NetworkConnector getInstance];
		session = [connector openSession:secure];
		
		NSString *handshake = NULL;
		if(isActive)
		{
			NSString *deviceId = conf.deviceId;
			NSString *authHash = conf.authenticationHash;
			
			handshake = [NSString stringWithFormat:_LOCATION_PAYLOAD_ACTIVE,deviceId,authHash];
		}
		else 
		{
			handshake = _LOCATION_PAYLOAD_INACTIVE;
		}
        
        NSString *response = [session performHandshake:handshake];
        LocationContext *responseContext = nil;
		if([StringUtil indexOf:response :@"status=200"] != -1)
		{
			PayloadHandler *handler = [PayloadHandler withInit];
            [context setRequest:request];
            
            NSString *xml = [handler serializeRequest:context];
            
            response = [session sendPayload:xml];
            
            responseContext = [handler deserializeResponse:response];
		}
        
        return responseContext;
    }
    @finally 
    {
        if(session != nil)
        {
            [session close];
        }
    }
}

+(Place *)getPlaceDetails:(NSString *)placeReference
{
    LocationContext *locationContext = [LocationContext withInit];
    [locationContext setPlaceReference:placeReference];
    
    LocationRequest *request = [LocationRequest withInit:@"placeDetails"];
    
    LocationContext *responseContext = [LocationService invoke:request :locationContext];
    
    Place *placeDetails = [responseContext getPlaceDetails];
    
    return placeDetails;
}

@end
