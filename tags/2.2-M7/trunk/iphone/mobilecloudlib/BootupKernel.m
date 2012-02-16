//
//  BootupKernel.m
//  mobilecloudlib
//
//  Created by openmobster on 5/25/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import "BootupKernel.h"
#import "Registry.h"
#import "AppConfig.h"
#import "AppService.h"
#import "AppSession.h"
#import "CloudService.h"

static BootupKernel *singleton = nil;


@implementation BootupKernel

+(BootupKernel *)getInstance
{
	if(singleton)
	{
		return singleton;
	}
	
	@synchronized([BootupKernel class])
	{
		if(singleton == nil)
		{
			singleton = [[BootupKernel alloc] init];
		}
	}
	return singleton;
}

-(void)startup
{
	@synchronized(self)
	{
		Registry *registry = [Registry getInstance];
		
		//Register the BootupKernel components here
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
		
		Configuration *conf = [Configuration getInstance];
		if(![conf isActivated])
		{
			//Hopefully the UIKernel will force in-app activation
		}
	}
}

-(void)shutdown
{
	@synchronized(self)
	{
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
@end
