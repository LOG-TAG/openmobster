/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "Field.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation Field

@synthesize uri;
@synthesize name;
@synthesize value;

-(void)dealloc
{
	[uri release];
	[name release];
	[value release];
	[super dealloc];
}

+(id) withInit
{
	Field *field = [[[Field alloc] init] autorelease];
    return field;
}

+(id) withInit:(NSString *)uri name:(NSString *)name value:(NSString *)value
{
	Field *field = [Field withInit];
	
	field.uri = uri;
	field.name = name;
	field.value = value;
	
	return field;
}

- (BOOL)isEqual:(id)anObject
{
	if([anObject class] == [Field class])
	{
		Field *local = (Field *)anObject;
		if([local.uri isEqualToString:self.uri] &&
		   [local.name isEqualToString:self.name] &&
		   [local.value isEqualToString:self.value]
		)
		{
			return YES;
		}
	}
	return NO;
}
@end