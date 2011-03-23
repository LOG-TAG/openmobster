/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "EmailBean.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation EmailBean

@synthesize oid;
@synthesize from;
@synthesize to;
@synthesize subject;
@synthesize date;

-(void)dealloc
{
	[oid release];
	[from release];
	[to release];
	[subject release];
	[date release];
	[super dealloc];
}

+(id)withInit
{
	EmailBean *instance = [[EmailBean alloc] init];
	instance = [instance autorelease];
	return instance;
}

@end
