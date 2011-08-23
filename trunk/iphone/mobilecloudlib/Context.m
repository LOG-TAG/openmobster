/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "Context.h"


@implementation Context

@synthesize attributes;

+(id) withInit
{
	Context *context = [[Context alloc] init];
	context.attributes = [GenericAttributeManager withInit];
	
	return [context autorelease];
}

-(void) setAttribute:(NSString *) name :(id) value
{
	[self.attributes setAttribute:name :value];
}

-(id) getAttribute:(NSString *) attribute
{
	return [self.attributes getAttribute:attribute];
}

-(void) removeAttribute:(NSString *) attribute
{
	[self.attributes removeAttribute:attribute];
}

@end
