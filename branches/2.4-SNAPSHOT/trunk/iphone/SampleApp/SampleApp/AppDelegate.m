//
//  AppDelegate.m
//  SampleApp
//
//  Created by openmobster on 8/31/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "AppDelegate.h"

#import "ViewController.h"
#import "SaveTicket.h"

#import "CloudService.h"
#import "CommandContext.h"
#import "CommandService.h"
#import "BackgroundSyncCommand.h"
#import "SubmitDeviceToken.h"

@implementation AppDelegate

@synthesize window = _window;
@synthesize viewController = _viewController;
@synthesize navigationController;
@synthesize pushRegistered;

- (void)dealloc
{
    [_window release];
    [_viewController release];
    [navigationController release];
    [super dealloc];
}

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    //OpenMobster bootstrapping
    [self startCloudService];
    //[self sync];
    
    self.window = [[[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]] autorelease];
    
    
    // Override point for customization after application launch.
    if ([[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPhone) 
    {
        self.viewController = [[[ViewController alloc] initWithNibName:@"ViewController_iPhone" bundle:nil] autorelease];
    } 
    else 
    {
        self.viewController = [[[ViewController alloc] initWithNibName:@"ViewController_iPad" bundle:nil] autorelease];
    }
    
    //setup the NavigationController
    self.navigationController = [[UINavigationController alloc] initWithRootViewController:self.viewController];
	
	//Add the CloudManager button to the navbar
	UIBarButtonItem *button = [[UIBarButtonItem alloc] initWithTitle:@"Cloud Manager" style:UIBarButtonItemStyleDone target:self.viewController action:@selector(launchCloudManager:)];
    
	self.navigationController.topViewController.navigationItem.leftBarButtonItem = button;
	[button release];
    
    //Add the Create button to the nav bar
    UIBarButtonItem *create = [[UIBarButtonItem alloc] initWithTitle:@"Create" style:UIBarButtonItemStyleDone target:self.viewController action:@selector(launchCreateBean)];
    
	self.navigationController.topViewController.navigationItem.rightBarButtonItem = create;
	[create release];
    
    
    self.window.rootViewController = self.navigationController;
    [self.window makeKeyAndVisible];
    
    
    //OpenMobster bootstrapping
    [self startActivation];
    
    //Register the App for Push notifications
    [[UIApplication sharedApplication] 
	 registerForRemoteNotificationTypes:
	 (UIRemoteNotificationTypeAlert | 
	  UIRemoteNotificationTypeBadge | 
	  UIRemoteNotificationTypeSound)];

    
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
    //OpenMobster bootstrapping
    //[self sync];
    
    if(!self.pushRegistered)
    {
        [[UIApplication sharedApplication] 
         registerForRemoteNotificationTypes:
         (UIRemoteNotificationTypeAlert | 
          UIRemoteNotificationTypeBadge | 
          UIRemoteNotificationTypeSound)];
    }
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
    //OpenMobster bootstrapping
    [self stopCloudService];
}
//-------Push Notification Callbacks-----------------------------------------------------------------------
- (void)application:(UIApplication *)app didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken 
{ 
	//Used only for debugging
    /*NSString *str = [NSString stringWithFormat:@"Device Token=%@",deviceToken];
	//show this in an alert dialog
	UIAlertView *dialog = [[UIAlertView alloc] initWithTitle:@"Device Token"
													 message:str delegate:nil 
										   cancelButtonTitle:@"OK" otherButtonTitles:nil];
	dialog = [dialog autorelease];
	[dialog show];*/
	
	NSString *deviceTokenStr = [NSString stringWithFormat:@"%@",deviceToken];
	deviceTokenStr = [StringUtil replaceAll:deviceTokenStr :@"<" :@""];
	deviceTokenStr = [StringUtil replaceAll:deviceTokenStr :@">" :@""];
	
	@try 
	{
		SubmitDeviceToken *submit = [SubmitDeviceToken withInit];
		[submit submit:deviceTokenStr];
        
        self.pushRegistered = YES;
	}
	@catch (SystemException * syse) 
	{
		UIAlertView *dialog = [[UIAlertView alloc] 
							   initWithTitle:@"Token Registration Error"
							   message:@"Device Token Cloud Registration Failed. Please make sure your device is activated with the Cloud" 
							   delegate:nil 
							   cancelButtonTitle:@"OK" otherButtonTitles:nil];
		dialog = [dialog autorelease];
		[dialog show];
	}
}

- (void)application:(UIApplication *)app didFailToRegisterForRemoteNotificationsWithError:(NSError *)err { 
	
    NSString *str = [NSString stringWithFormat: @"Error: %@", err];
    
	UIAlertView *dialog = [[UIAlertView alloc] 
						   initWithTitle:@"Token Registration Error"
						   message:str 
						   delegate:nil 
						   cancelButtonTitle:@"OK" otherButtonTitles:nil];
	dialog = [dialog autorelease];
	[dialog show];
}

- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo 
{
    //Here you must execute logic related to the user clicking on the notification
    //And launching the App
    
    
    //Here, it just displays the key-value pairs of incoming data in a dialog box
	NSMutableString *buffer = [NSMutableString string];
    for (id key in userInfo) 
	{
        id value = [userInfo objectForKey:key];
        [buffer appendFormat:@"Key=%@,Value=%@\n",key,value,nil];
    }    
    
    //Show the buffer
    UIAlertView *dialog = [[UIAlertView alloc] 
						   initWithTitle:@"Push"
						   message:buffer
						   delegate:nil 
						   cancelButtonTitle:@"OK" otherButtonTitles:nil];
	dialog = [dialog autorelease];
	[dialog show];

}
//----------------OpenMobster Cloud App integration--------------------------------------------------------
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
    CommandContext *commandContext = [CommandContext withInit:self.viewController];
    BackgroundSyncCommand *syncCommand = [BackgroundSyncCommand withInit];
    [commandContext setTarget:syncCommand];
    CommandService *service = [CommandService getInstance];
    [service execute:commandContext]; 
}
@end
