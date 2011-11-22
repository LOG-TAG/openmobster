//
//  Address.m
//  mobilecloudlib
//
//  Created by openmobster on 11/18/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import "Address.h"


@implementation Address

@synthesize street;
@synthesize city;
@synthesize state;
@synthesize country;
@synthesize zipCode;
@synthesize county;
@synthesize postal;
@synthesize latitude;
@synthesize longitude;
@synthesize radius;
@synthesize woeid;
@synthesize woetype;

+(id)withInit
{
    Address *address = [[[Address alloc] init] autorelease];
    return address;
}

-(void)dealloc
{
    [street release];
    [city release];
    [state release];
    [country release];
    [zipCode release];
    [county release];
    [postal release];
    [latitude release];
    [longitude release];
    [radius release];
    [woeid release];
    [woetype release];
    [super dealloc];
}

@end
