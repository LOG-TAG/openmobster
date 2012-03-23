//
//  IntegrationTestAppDelegate.m
//  IntegrationTest
//
//  Created by openmobster on 8/12/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import "IntegrationTestAppDelegate.h"
#import "CloudService.h"

@implementation IntegrationTestAppDelegate


@synthesize window=_window;
@synthesize viewController;

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    // Override point for customization after application launch.
    [self.window addSubview:viewController.view];
    [self.window makeKeyAndVisible];
    
    //Bootstrap the Cloud
    [self startCloudService];
    
    return YES;
}

- (void)applicationWillResignActive:(UIApplication *)application
{
    /*
     Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
     Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
     */
}

- (void)applicationDidEnterBackground:(UIApplication *)application
{
    /*
     Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later. 
     If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
     */
}

- (void)applicationWillEnterForeground:(UIApplication *)application
{
    /*
     Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
     */
    AppService *appService = [AppService getInstance];
	[appService start];
}

- (void)applicationDidBecomeActive:(UIApplication *)application
{
    /*
     Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
     */
}

- (void)applicationWillTerminate:(UIApplication *)application
{
    /*
     Called when the application is about to terminate.
     Save data if appropriate.
     See also applicationDidEnterBackground:.
     */
    [self stopCloudService];
}

- (void)dealloc
{
    [_window release];
    [viewController release];
    [super dealloc];
}

//---OpenMobster Cloud Layer integration-------------------------------------------------------
-(void)startCloudService
{
	@try 
	{
		CloudService *cloudService = [CloudService getInstance:viewController];
		
		[cloudService startup];
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

@end
