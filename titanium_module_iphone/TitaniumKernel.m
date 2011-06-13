//
//  TitaniumKernel.m
//  titanium_module_iphone
//
//  Created by openmobster on 5/31/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import "TitaniumKernel.h"
#import "Kernel.h"
#import "BootupKernel.h"
#import "AppConfig.h"
#import "AppService.h"

static TitaniumKernel *singleton;

@implementation TitaniumKernel

+(TitaniumKernel *) withInit
{
	singleton = [[TitaniumKernel alloc] init];
	singleton -> isRunning = NO;
	return singleton;
}

+(TitaniumKernel *) getInstance
{
	return singleton;
}

-(void) start
{
	@try 
	{
		Kernel *kernel = [Kernel getInstance];
		[kernel startup];
		
		BootupKernel *bootupKernel = [BootupKernel getInstance];
		[bootupKernel startup];
		
		//Load the App Configuration
		NSString *path = [NSString stringWithFormat:@"modules/%@/openmobster-app.xml",@"org.openmobster.cloud"];
		AppConfig *appConfig = [AppConfig getInstance];
		[appConfig start:path];
		
		//Start the App service
		AppService *appService = [AppService getInstance];
		[appService start];
	}
	@catch (NSException * e) 
	{
		//something caused the kernel to crash
		//stop the kernel
		[self stop];
	}
}

-(void) stop
{
	@try
	{
		BootupKernel *bootupKernel = [BootupKernel getInstance];
		[bootupKernel shutdown];
		
		Kernel *kernel = [Kernel getInstance];
		[kernel shutdown];
	}
	@catch (NSException *e) 
	{
		//don't worry about it
	}
	@finally
	{
		if(singleton != nil)
		{
			[singleton release];
			singleton = nil;
		}
	}
}

+(void) startTx
{
	if(singleton == nil)
	{
		[TitaniumKernel withInit];
	}
	
	TitaniumKernel *kernel = [TitaniumKernel getInstance];
	if(!(kernel -> isRunning))
	{
		[kernel start];
		kernel -> isRunning = YES;
	}
}

+(void) startApp
{
	[TitaniumKernel startTx];
	
	AppService *appService = [AppService getInstance];
	[appService start];
}
@end
