//
//  PayloadHandler.m
//  mobilecloudlib
//
//  Created by openmobster on 11/20/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import "PayloadHandler.h"
#import "LocationRequest.h"
#import "SBJsonWriter.h"
#import "SBJsonParser.h"
#import "StringUtil.h"
#import "XMLUtil.h"
#import "LocationResponse.h"


@implementation PayloadHandler

@synthesize dataBuffer;
@synthesize responsePayload;
@synthesize locationPayload;

-(void) dealloc
{
    [dataBuffer release];
    [responsePayload release];
    [locationPayload release];
    [super dealloc];
}

+(id)withInit
{
    PayloadHandler *instance = [[[PayloadHandler alloc] init] autorelease];
    return instance;
}

-(NSString *)serializeRequest:(LocationContext *)locationContext
{
    LocationRequest *request = [locationContext getRequest];
    
    //Serializing the request object
    SBJsonWriter *writer = [[SBJsonWriter alloc] init];
    NSMutableDictionary *requestObject = [NSMutableDictionary dictionary];
    
    NSArray *names = [request getNames];
    if(names != nil && [names count]>0)
    {
        for(NSString *name in names)
        {
            id value = [request get:name];
            [requestObject setValue:value forKey:name]; 
        }
    }
    
    NSString *requestPayload = [writer stringWithObject:requestObject];
    
    //Serialize the LocationContext object
    NSMutableDictionary *locationObject = [NSMutableDictionary dictionary];
    
    NSString *latitude = [locationContext getLatitude];
    if(![StringUtil isEmpty:latitude])
    {
        [locationObject setValue:latitude forKey:@"latitude"];
    }
    
    NSString *longitude = [locationContext getLongitude];
    if(![StringUtil isEmpty:longitude])
    {
        [locationObject setValue:longitude forKey:@"longitude"];
    }
    
    NSString *placeReference = [locationContext getPlaceReference];
    if(![StringUtil isEmpty:placeReference])
    {
        [locationObject setValue:placeReference forKey:@"placeReference"];
    }
    
    NSArray *placeTypes = [locationContext getPlaceTypes];
    if(placeTypes != nil && [placeTypes count]>0)
    {
        [locationObject setValue:placeTypes forKey:@"placeTypes"];
    }
    
    //rest of the location context
    names = [locationContext getNames];
    if(names != nil && [names count]>0)
    {
        for(NSString *name in names)
        {
            id value = [locationContext getAttribute:name];
            if([value isKindOfClass:[NSString class]])
            {
                [locationObject setValue:value forKey:name];
            }
        }
    }
    
    NSString *locationPayload = [writer stringWithObject:locationObject];
    
    NSMutableString *xml = [NSMutableString string];
    [xml appendString:@"<location-request>\n"];
    [xml appendFormat:@"<service>%@</service>\n",request.service];
    [xml appendFormat:@"<request-payload>%@</request-payload>\n",[XMLUtil addCData:requestPayload]];
    [xml appendFormat:@"<location-payload>%@</location-payload>\n",[XMLUtil addCData:locationPayload]];
    [xml appendString:@"</location-request>\n"];
    
    //cleanup
    [writer release];
    
    return [NSString stringWithString:xml];
}

-(LocationContext *)deserializeResponse:(NSString *)xml
{
    LocationContext *locationContext = [LocationContext withInit];
    
    NSData *xmlData = [xml dataUsingEncoding:NSUTF8StringEncoding];
	NSXMLParser *parser = [[[NSXMLParser alloc] initWithData:xmlData] autorelease];
	
	//Set the Deletegate to self
	[parser setDelegate:self];
	
	//Start parsing
	[parser parse];
    
    //JSON Parser
    SBJsonParser *jsonParser = [[SBJsonParser alloc] init];
    
    //Parse the LocationResponse
    LocationResponse *response = [LocationResponse withInit];
    NSDictionary *parsedResponse = (NSDictionary *)[jsonParser objectWithString:self.responsePayload];
    NSArray *names = [parsedResponse allKeys];
    if(names != nil && [names count]>0)
    {
        for(NSString *name in names)
        {
            id value = [parsedResponse objectForKey:name];
            
            if([value isKindOfClass:[NSString class]])
            {
                [response setAttribute:name :(NSString *)value];
            }
            else if([value isKindOfClass:[NSArray class]])
            {
                [response setListAttribute:name :(NSArray *)value];
            }
            else if([value isKindOfClass:[NSDictionary class]])
            {
                [response setMapAttribute:name :(NSDictionary *)value];
            }
        }
    }
    [locationContext setResponse:response];
    
    //Parse LocationContext
    NSDictionary *parsedContext = (NSDictionary *)[jsonParser objectWithString:self.locationPayload];
    names = [parsedContext allKeys];
    if(names != nil && [names count] > 0)
    {
        for(NSString *name in names)
        {
            id value = [parsedContext objectForKey:name];
            
            if([value isKindOfClass:[NSString class]])
            {
                [locationContext setAttribute:name :value];
            }
        }
    }
    
    //Parse the Places
    NSArray *placesArray = [parsedContext objectForKey:@"places"];
    if(placesArray != nil)
    {
        NSMutableArray *places = [NSMutableArray array];
        for(NSDictionary *placeDict in placesArray)
        {
            Place *place = [self deserializePlace:placeDict];
            [places addObject:place];
        }
        [locationContext setNearbyPlaces:[NSArray arrayWithArray:places]];
    }
    
    //Place Details
    NSDictionary *placeDetails = [parsedContext objectForKey:@"placeDetails"];
    if(placeDetails != nil)
    {
        Place *details = [self deserializePlace:placeDetails];
        [locationContext setPlaceDetails:details];
    }
    
    //Place Types
    NSArray *placeTypes = [parsedContext objectForKey:@"placeTypes"];
    if(placeTypes != nil)
    {
        [locationContext setPlaceTypes:placeTypes];
    }
    
    //Address
    NSDictionary *address = [parsedContext objectForKey:@"address"];
    if(address != nil)
    {
        Address *local = [self deserializeAddress:address];
        [locationContext setAddress:local];
    }
    
    //cleanup
    [jsonParser release];
    
    return locationContext;
}
//---------------------------------------------------------------------------------------------------------------------
-(Place *) deserializePlace:(NSDictionary *)placeObj
{
    Place *place = [Place withInit];
    
    NSString *value = [placeObj objectForKey:@"address"];
    if(![StringUtil isEmpty:value])
    {
        place.address = value;
    }
    
    value = [placeObj objectForKey:@"phone"];
    if(![StringUtil isEmpty:value])
    {
        place.phone = value;
    }
    
    value = [placeObj objectForKey:@"international_phone_number"];
    if(![StringUtil isEmpty:value])
    {
        place.internationalPhoneNumber = value;
    }
    
    value = [placeObj objectForKey:@"url"];
    if(![StringUtil isEmpty:value])
    {
        place.url = value;
    }
    
    value = [placeObj objectForKey:@"website"];
    if(![StringUtil isEmpty:value])
    {
        place.website = value;
    }
    
    value = [placeObj objectForKey:@"icon"];
    if(![StringUtil isEmpty:value])
    {
        place.icon = value;
    }
    
    value = [placeObj objectForKey:@"name"];
    if(![StringUtil isEmpty:value])
    {
        place.name = value;
    }
    
    value = [placeObj objectForKey:@"latitude"];
    if(![StringUtil isEmpty:value])
    {
        place.latitude = value;
    }
    
    value = [placeObj objectForKey:@"longitude"];
    if(![StringUtil isEmpty:value])
    {
        place.longitude = value;
    }
    
    value = [placeObj objectForKey:@"id"];
    if(![StringUtil isEmpty:value])
    {
        place.placeId = value;
    }
    
    value = [placeObj objectForKey:@"reference"];
    if(![StringUtil isEmpty:value])
    {
        place.reference = value;
    }
    
    value = [placeObj objectForKey:@"rating"];
    if(![StringUtil isEmpty:value])
    {
        place.rating = value;
    }
    
    value = [placeObj objectForKey:@"vicinity"];
    if(![StringUtil isEmpty:value])
    {
        place.vicinity = value;
    }
    
    value = [placeObj objectForKey:@"html_attribution"];
    if(![StringUtil isEmpty:value])
    {
        place.htmlAttribution = value;
    }
    
    NSArray *types = [placeObj objectForKey:@"types"];
    if(types != nil && [types count]>0)
    {
        place.types = types;
    }
    
    return place;
}

-(Address *) deserializeAddress:(NSDictionary *)addressObj
{
    Address *address = [Address withInit];
    
    NSString *value = [addressObj objectForKey:@"street"];
    if(![StringUtil isEmpty:value])
    {
        address.street = value;
    }
    
    value = [addressObj objectForKey:@"city"];
    if(![StringUtil isEmpty:value])
    {
        address.city = value;
    }
    
    value = [addressObj objectForKey:@"state"];
    if(![StringUtil isEmpty:value])
    {
        address.state = value;
    }
    
    value = [addressObj objectForKey:@"country"];
    if(![StringUtil isEmpty:value])
    {
        address.country = value;
    }
    
    value = [addressObj objectForKey:@"zipcode"];
    if(![StringUtil isEmpty:value])
    {
        address.zipCode = value;
    }
    
    value = [addressObj objectForKey:@"county"];
    if(![StringUtil isEmpty:value])
    {
        address.county = value;
    }
    
    value = [addressObj objectForKey:@"postal"];
    if(![StringUtil isEmpty:value])
    {
        address.postal = value;
    }
    
    value = [addressObj objectForKey:@"latitude"];
    if(![StringUtil isEmpty:value])
    {
        address.latitude = value;
    }
    
    value = [addressObj objectForKey:@"longitude"];
    if(![StringUtil isEmpty:value])
    {
        address.longitude = value;
    }
    
    value = [addressObj objectForKey:@"radius"];
    if(![StringUtil isEmpty:value])
    {
        address.radius = value;
    }
    
    value = [addressObj objectForKey:@"woetype"];
    if(![StringUtil isEmpty:value])
    {
        address.woetype = value;
    }
    
    value = [addressObj objectForKey:@"woeid"];
    if(![StringUtil isEmpty:value])
    {
        address.woeid = value;
    }
    
    return address;
}

-(void)parserDidStartDocument:(NSXMLParser *)parser
{
	self.dataBuffer = [NSMutableString stringWithString:@""]; 
}

- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName 
{
    //nothing
}

-(void)parser:(NSXMLParser *)parser didEndElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName
{
    if([elementName isEqualToString:@"response-payload"])
    {
        self.responsePayload = [NSString stringWithString:dataBuffer];
        [dataBuffer setString:@""];
    }
    else if([elementName isEqualToString:@"location-payload"])
    {
        self.locationPayload = [NSString stringWithString:dataBuffer];
        [dataBuffer setString:@""];
    }
}

-(void)parser:(NSXMLParser *)parser foundCharacters:(NSString *)string
{
    //nothing
}

-(void)parser:(NSXMLParser *)parser foundCDATA:(NSData *)CDATABlock
{
    if(CDATABlock != nil)
	{
		NSString *string = [[NSString alloc] initWithData:CDATABlock encoding:NSUTF8StringEncoding];
		string = [string autorelease];
		if(![StringUtil isEmpty:string])
		{
			[dataBuffer appendString:string];
		}		
	} 
}
@end
