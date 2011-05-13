/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "Bootstrapper.h"
#import "UIKernel.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation Bootstrapper

+(id)withInit
{
	return [[[Bootstrapper alloc] init] autorelease];
}

-(TestSuite *)bootstrap:(NSString *)cloudIp
{
	//Prepare the environment
	Kernel *kernel = [Kernel getInstance];
	[kernel startup];
	
	[Configuration clear];

	
	//Prepare the testsuite
	TestSuite *suite = [TestSuite withInit];
	suite.email = @"blah2@gmail.com";
	suite.password = @"blahblah2";
	suite.cloudServer = cloudIp;

	
	//Prepare the TestContext
	TestContext *context = [TestContext withInit];
	[context setAttribute:@"identifier" :deviceIdentifier];
	suite.context = context;
	
	//Activate the device with the Cloud
	ActivationUtil *activationUtil = [ActivationUtil withInit];
	[activationUtil activateDevice:suite];
	
	//Startup the UIKernel
	UIKernel *uiKernel = [UIKernel getInstance];
	[uiKernel startup:nil];
	
	return suite;
}
@end
