/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "TestSuite.h"
#import "UIKernel.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation TestSuite

@synthesize context;
@synthesize status;
@synthesize cloudServer;
@synthesize email;
@synthesize password;

-init
{
	if(self = [super init])
	{
		tests = [NSMutableArray array];
		errors = [NSMutableArray array];
	}
	
	return self;
}

+(id) withInit
{
	TestSuite *testSuite = [[[TestSuite alloc] init] autorelease];
	return testSuite;
}

-(void) addTest:(Test *) test
{
	[tests addObject:test];
	test.suite = self;
}

-(void) reportError:(NSString *)error
{
	[errors addObject:error];
}

-(void) execute
{
	for(Test *local in tests)
	{
		NSString *startMessage = [NSString stringWithFormat:@"Executing [%@]",[local getInfo]];
		NSLog(@"%@",startMessage);
		
		@try 
		{
			local.suite = self;
			
			[local setUp];
			
			[local runTest];
			
			[local tearDown];
			
			local.suite = nil;
		}
		@catch (SystemException *e) 
		{
			NSString *message = [e getMessage];
			[self reportError:message];
		}
	}
	
	//Prepare a Status Message
	if([errors count] > 0)
	{
		NSMutableString *buffer = [NSMutableString stringWithString:@"Errors---------------\n\n"];
		
		for(NSString *local in errors)
		{
			[buffer appendFormat:@"%@\n\n\n",local];
		}
		
		status = [NSString stringWithString:buffer];
	}
	else 
	{
		status = @"TestSuite successfully executed all tests.....";
	}
	
	//Cleanup
	Kernel *kernel = [Kernel getInstance];
	[kernel shutdown];
	UIKernel *uiKernel = [UIKernel getInstance];
	[uiKernel shutdown];
	
	NSLog(@"%@",status);
}
@end