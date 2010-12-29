/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "UIKernel.h"

static UIKernel *singleton = nil;


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation UIKernel

+(UIKernel *)getInstance
{
	if(singleton)
	{
		return singleton;
	}
	
	@synchronized([UIKernel class])
	{
		if(singleton == nil)
		{
			singleton = [[UIKernel alloc] init];
		}
	}
	return singleton;
}

-(void)startup
{
	//Implementation not needed yet
	@synchronized(self)
	{
		CommandService *commandService = [CommandService getInstance];
		[commandService stop];
	}
}

-(void)shutdown
{
	@synchronized(self)
	{
		CommandService *commandService = [CommandService getInstance];
		[commandService stop];
		if(singleton != nil)
		{
			[singleton release];
			singleton = nil;
		}
	}
}

-(BOOL)isRunning
{
	return YES; //if this is invoked...singleton instance is alive
}

-(void)forceActivation
{
	UIWindow *window = [[UIApplication sharedApplication] keyWindow]; 
	UIViewController *rootViewController = window.rootViewController;
	[CloudManager modalForceActivation:rootViewController];
}

-(void)launchCloudManager:(UIViewController *)caller
{
	[CloudManager modalCloudManager:caller];
}

-(void)launchDeviceActivation:(UIViewController *)caller
{
	[CloudManager modalActivateDevice:caller];
}
@end
