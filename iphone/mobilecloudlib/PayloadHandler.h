//
//  PayloadHandler.h
//  mobilecloudlib
//
//  Created by openmobster on 11/20/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "LocationContext.h"
#import "Place.h"
#import "Address.h"


@interface PayloadHandler : NSObject<NSXMLParserDelegate> 
{
    @private
    NSMutableString *dataBuffer;
    NSString *responsePayload;
    NSString *locationPayload;
}

@property (nonatomic,retain)NSMutableString *dataBuffer;
@property (nonatomic,retain)NSString *responsePayload;
@property (nonatomic,retain)NSString *locationPayload;

+(id)withInit;

-(NSString *)serializeRequest:(LocationContext *)locationContext;
-(LocationContext *)deserializeResponse:(NSString *)xml;

//internal use only
-(Place *) deserializePlace:(NSDictionary *)placeObj;
-(Address *) deserializeAddress:(NSDictionary *)addressObj;

@end
