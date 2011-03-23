/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "TestSimpleAccessors.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation TestSimpleAccessors

+(id)withInit
{
	return [[[TestSimpleAccessors alloc] init] autorelease];
}

-(void) runTest
{
	NSLog(@"MobileBean sample Access test.......");
}
@end
