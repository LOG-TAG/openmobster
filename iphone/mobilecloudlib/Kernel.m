/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "Kernel.h"

static Kernel *singleton = nil;

/**
 * 
 * @author openmobster@gmail.com
 */
@implementation Kernel

+(Kernel *) getInstance
{
	if(singleton)
	{
		return singleton;
	}
	
	@synchronized([Kernel class])
	{
		if(singleton == nil)
		{
			singleton = [[Kernel alloc] init];
		}
	}
	return singleton;
}

-(BOOL)isRunning
{
	Registry *registry = [Registry getInstance];
	return [registry isStarted];
}

-(void)startup
{
	@synchronized(self)
	{
		Registry *registry = [Registry getInstance];
		if([registry isStarted])
		{
			//Check to see if device is activated with the cloud...if not,
			//show the activation dialog
			Configuration *conf = [Configuration getInstance];
			if(![conf isActivated])
			{
				UIKernel *uiKernel = [UIKernel getInstance];
				[uiKernel startup];
				[uiKernel forceActivation];
			}
			return;
		}
		
		//Initialize the singletons
		[DeviceSerializer getInstance];
		[CloudDBManager getInstance];
		
		NetworkConnector *networkConnector = [[[NetworkConnector alloc] init] autorelease];
		SyncDataSource *syncds = [[[SyncDataSource alloc] init] autorelease];
		SyncService *syncService = [[[SyncService alloc] init] autorelease];
		MobileObjectDatabase *mobileObjectDatabase = [[[MobileObjectDatabase alloc] init] autorelease];
		SyncScheduler *syncScheduler = [[[SyncScheduler alloc] init] autorelease];
		ProxyLoader *proxyLoader = [[[ProxyLoader alloc] init] autorelease];
		Bus *bus = [[[Bus alloc] init] autorelease];
		
		//Add background daemons
		[registry addService:syncScheduler];
		[registry addService:proxyLoader];
		
		//Add NetworkConnection services
		[registry addService:networkConnector];
		
		//Synchronization services
		[registry addService:syncds];
		[registry addService:syncService];
		
		//MobileObject services
		[registry addService:mobileObjectDatabase];
		
		//Bus service
		[registry addService:bus];
		
		[registry start];
		
		//Also start the UIKernel
		UIKernel *uiKernel = [UIKernel getInstance];
		[uiKernel startup];
		
		//Check to see if device is activated with the cloud...if not,
		//show the activation dialog
		Configuration *conf = [Configuration getInstance];
		if(![conf isActivated])
		{
			[uiKernel forceActivation];
		}
	}
}

-(void)shutdown
{
	@synchronized(self)
	{
		if([Registry isActive])
		{
			//release the registry
			Registry *registry = [Registry getInstance];
			[registry stop];
		}
		
		//Stop the DeviceSerializer
		[DeviceSerializer stop];
		
		//Stop the CloudDBManager
		[CloudDBManager stop];
		
		if(singleton != nil)
		{
			[singleton release];
			singleton = nil;
		}
		
		//stop the UIKernel
		UIKernel *uiKernel = [UIKernel getInstance];
		[uiKernel shutdown];
	}
}
@end
