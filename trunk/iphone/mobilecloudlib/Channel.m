/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "Channel.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation Channel

@synthesize name;
@synthesize owner;
@synthesize writable;

-(void)dealloc
{
	[name release];
	[owner release];
	[super dealloc];
}

+(id)withInit:(NSString *)name :(NSString *)owner
{
	Channel *instance = [[[Channel alloc] init] autorelease];
	
	instance.name = name;
	instance.owner = owner;
	
	return instance;
}

+(id)withInit
{
	Channel *instance = [[[Channel alloc] init] autorelease];
	return instance;
}

+(id)withInit:(NSString *)name :(NSString *)owner :(BOOL)writable
{
	Channel *instance = [Channel withInit:name :owner];
	instance.writable = writable;
	
	return instance;
}
@end
