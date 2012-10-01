//
//  Place.m
//  mobilecloudlib
//
//  Created by openmobster on 11/18/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import "Place.h"


@implementation Place

@synthesize address;
@synthesize phone;
@synthesize internationalPhoneNumber;
@synthesize url;
@synthesize website;
@synthesize icon;
@synthesize name;
@synthesize latitude;
@synthesize longitude;
@synthesize placeId;
@synthesize reference;
@synthesize rating;
@synthesize types;
@synthesize vicinity;
@synthesize htmlAttribution;

+(id)withInit
{
    Place *instance = [[[Place alloc] init] autorelease];
    return instance;
}

-(void) dealloc
{
    [address release];
    [phone release];
    [internationalPhoneNumber release];
    [url release];
    [website release];
    [icon release];
    [name release];
    [latitude release];
    [longitude release];
    [placeId release];
    [reference release];
    [rating release];
    [types release];
    [vicinity release];
    [htmlAttribution release];

    [super dealloc];
}
@end
