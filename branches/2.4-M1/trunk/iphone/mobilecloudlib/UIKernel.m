/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "UIKernel.h"
#import "AppService.h"
#import "AppConfig.h"
#import "AppSession.h"

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

-(void)dealloc
{
	[super dealloc];
}

-(void)startup
{
	//Implementation not needed yet
	@synchronized(self)
	{
		Registry *registry = [Registry getInstance];
		
		CommandService *commandService = [CommandService getInstance];
		[commandService stop];
		
		//Register the UIKernel components here
		if([AppConfig getInstance] == nil)
		{
			AppConfig *appConfig = [[[AppConfig alloc] init] autorelease];
			[registry addService:appConfig];
			[appConfig start];
		}
		if([AppService getInstance] == nil)
		{
			AppService *appService = [[[AppService alloc] init] autorelease];
			[registry addService:appService];
			[appService start];
		}
		if([AppSession getInstance] == nil)
		{
			AppSession *appSession = [[[AppSession alloc] init] autorelease];
			[registry addService:appSession];
			[appSession start];
		}
		
		
		/*Configuration *conf = [Configuration getInstance];
		if(![conf isActivated])
		{
			[self forceActivation:home];
		}*/
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
			[self release];
			singleton = nil;
		}
	}
}

-(BOOL)isRunning
{
	return YES; //if this is invoked...singleton instance is alive
}

-(void)forceActivation:(UIViewController *)caller
{	
	[CloudManager modalForceActivation:caller];
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
