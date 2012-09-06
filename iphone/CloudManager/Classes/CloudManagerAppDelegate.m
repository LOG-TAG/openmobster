/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "CloudManagerAppDelegate.h"

#import "CloudService.h"
#import "CommandContext.h"
#import "CommandService.h"
#import "BackgroundSyncCommand.h"

@implementation CloudManagerAppDelegate

@synthesize window;
@synthesize mainView;


#pragma mark -
#pragma mark Application lifecycle

-(BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions 
{   
    //OpenMobster bootstrapping
    [self startCloudService];
    [self sync];
    
    //Add the CloudManager button to the mainView NavigationController
	UIBarButtonItem *button = [[UIBarButtonItem alloc] initWithTitle:@"Cloud Manager" style:UIBarButtonItemStyleDone target:self action:@selector(launchCloudManager:)];
    self.mainView.topViewController.navigationItem.leftBarButtonItem = button;
	[button release];
    
    // Override point for customization after application launch.
	[window addSubview:mainView.view];
    [window makeKeyAndVisible];
	
	//OpenMobster bootstrapping
    [self startActivation];
	
	return YES;
}


-(void)applicationDidEnterBackground:(UIApplication *)application 
{
    /*
     Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later. 
     If your application supports background execution, called instead of applicationWillTerminate: when the user quits.
     */
}


-(void)applicationWillEnterForeground:(UIApplication *)application 
{
    /*
     Called as part of  transition from the background to the inactive state: here you can undo many of the changes made on entering the background.
     */
    //OpenMobster bootstrapping
    [self sync];
}

-(void)applicationWillResignActive:(UIApplication *)application 
{
    /*
     Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
     Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
     */
}

-(void)applicationDidBecomeActive:(UIApplication *)application 
{
    /*
     Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
     */
}


-(void)applicationWillTerminate:(UIApplication *)application 
{
    /*
     Called when the application is about to terminate.
     See also applicationDidEnterBackground:.
     */
	[self stopCloudService];
}


#pragma mark -
#pragma mark Memory management

-(void)applicationDidReceiveMemoryWarning:(UIApplication *)application 
{
    /*
     Free up as much memory as possible by purging cached data objects that can be recreated (or reloaded from disk) later.
     */
}


-(void)dealloc 
{
	[mainView release];
    [window release];
    [super dealloc];
}
//---OpenMobster Cloud Layer integration-------------------------------------------------------
-(IBAction)launchCloudManager:(id)sender
{
    //Launch the CloudManager App
    [CloudManager modalCloudManager:self.window.rootViewController];
}

-(void)startCloudService
{
	@try 
	{
		CloudService *cloudService = [CloudService getInstance];
		[cloudService startup];
	}
	@catch (NSException * e) 
	{
		//something caused the kernel to crash
		//stop the kernel
		[self stopCloudService];
	}
}

-(void)startActivation
{
	@try 
	{
		CloudService *cloudService = [CloudService getInstance];
		[cloudService forceActivation:self.window.rootViewController];
	}
	@catch (NSException * e) 
	{
		//something caused the kernel to crash
		//stop the kernel
		[self stopCloudService];
	}
}

-(void)stopCloudService
{
	@try
	{
		CloudService *cloudService = [CloudService getInstance];
		[cloudService shutdown];
	}
	@catch (NSException *e) 
	{
		
	}
}

-(void)sync
{
    CommandContext *commandContext = [CommandContext withInit:self.window.rootViewController];
    BackgroundSyncCommand *syncCommand = [BackgroundSyncCommand withInit];
    [commandContext setTarget:syncCommand];
    CommandService *service = [CommandService getInstance];
    [service execute:commandContext]; 
}
@end