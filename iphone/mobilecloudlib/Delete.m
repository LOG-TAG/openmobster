//
//  Delete.m
//  iphone
//
//  Created by openmobster on 7/22/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

#import "Delete.h"


@implementation Delete

@synthesize archive;
@synthesize softDelete;

+(id) withInit
{
	return [[[Delete alloc] init] autorelease];
}

@end
