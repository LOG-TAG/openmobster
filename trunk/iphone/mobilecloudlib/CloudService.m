//
//  CloudService.m
//  mobilecloudlib
//
//  Created by openmobster on 7/4/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import "CloudService.h"
#import "SystemException.h"


static CloudService *singleton = nil;


@implementation CloudService

@synthesize kernel;
@synthesize bootupKernel;
@synthesize uiKernel;
@synthesize viewController;

+(CloudService *)getInstance
{
	if(singleton)
	{
		return singleton;
	}
	
	@synchronized([CloudService class])
	{
		if(singleton == nil)
		{
			singleton = [[CloudService alloc] init];
			
			singleton.kernel = [Kernel getInstance];
			singleton.bootupKernel = [BootupKernel getInstance];
			singleton.uiKernel = [UIKernel getInstance];
		}
	}
	return singleton;
}

-(void) dealloc
{
	[super dealloc];
	
	[self.kernel release];
	[self.bootupKernel release];
	[self.uiKernel release];
	
	if(self.viewController != nil)
	{
		[self.viewController release];
	}
}

+(CloudService *) getInstance:(UIViewController *)viewController
{
	CloudService *instance = [CloudService getInstance];
	instance.viewController = viewController;
	
	return instance;
}

-(void) startup
{
	@synchronized(self)
	{
		@try
		{
			//First start the core kernel
			[self.kernel startup];
	
			//Now the App level kernel
			[self.bootupKernel startup];
	
			//Now UIKernel if necessary
			if(self.viewController != nil)
			{
				[self.uiKernel startup:viewController];
			}
		}
		@catch(NSException *ex)
		{
			/*UIAlertView *dialog = [[UIAlertView alloc] 
								   initWithTitle:@"CloudService startup error"
								   message:@"An Error occurred while starting the Cloud Service" 
								   delegate:self 
								   cancelButtonTitle:@"OK" otherButtonTitles:nil];
			dialog = [dialog autorelease];
			[dialog show];*/
			SystemException *sys = [SystemException withContext:@"CloudService" method:@"startup" parameters:nil];
			@throw sys;
		}
	}
}

-(void) shutdown
{
	@synchronized(self)
	{
		//UIKernel if necessary
		if(self.viewController != nil)
		{
			[self.uiKernel shutdown];
		}
	
		//Now the App level kernel
		[self.bootupKernel shutdown];
	
		//Now shutdown the core kernel
		[self.kernel shutdown];
	
		if(singleton != nil)
		{
			[singleton release];
			singleton = nil;
		}
	}
}

-(void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
	exit(0);
}
@end