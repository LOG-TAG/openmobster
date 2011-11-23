//
//  LocationService.h
//  mobilecloudlib
//
//  Created by openmobster on 11/23/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "LocationContext.h"
#import "LocationRequest.h"
#import "LocationResponse.h"


@interface LocationService : NSObject 
{
    
}

+(id)withInit;

-(LocationContext *)invoke:(LocationRequest *)request :(LocationContext *)context;
@end
