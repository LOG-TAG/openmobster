/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "TestContext.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation TestContext

-init
{
	if(self = [super init])
	{
		//Initialize GenericAttributeManager
		mgr = [GenericAttributeManager withInit];
	}
	
	return self;
}

+(id) withInit
{
	TestContext *testContext = [[[TestContext alloc] init] autorelease];
	return testContext;
}

-(id) getAttribute: (NSString *)name
{
	return [mgr getAttribute:name];
}

-(void) setAttribute: (NSString *)name : (id)value
{
	[mgr setAttribute:name :value];
}

-(void) removeAttribute: (NSString *) name
{
	[mgr removeAttribute:name];
}

@end
