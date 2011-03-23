//
//  Item.m
//  iphone
//
//  Created by openmobster on 7/22/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

#import "Item.h"


@implementation Item

@synthesize target;
@synthesize source;
@synthesize meta;
@synthesize data;
@synthesize moreData;

+(id) withInit
{
	return [[[Item alloc] init] autorelease];
}

@end
