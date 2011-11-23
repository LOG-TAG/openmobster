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

+(id)withInit
{
    LocationService *instance = [[[LocationService alloc] init] autorelease];
    return instance;
}

-(LocationContext *)invoke:(LocationRequest *)request :(LocationContext *)context
{
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

@end
