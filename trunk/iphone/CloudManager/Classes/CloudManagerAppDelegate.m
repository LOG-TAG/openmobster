/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "CloudManagerAppDelegate.h"
#import "AppService.h"
#import "CloudService.h"

@implementation CloudManagerAppDelegate

@synthesize window;
@synthesize mainView;


#pragma mark -
#pragma mark Application lifecycle

-(BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions 
{    
    // Override point for customization after application launch.
	[window addSubview:mainView.view];
    [window makeKeyAndVisible];
	
	[self startCloudService];
	
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
	AppService *appService = [AppService getInstance];
	[appService start];
	
	//FIXME: remove this
	Configuration *conf = [Configuration getInstance];
	NSDictionary *channelRegistry = [conf getChannelRegistry];
	NSArray *allKeys = [channelRegistry allKeys];
	NSLog(@"*********************************************");
	for(NSString *local in allKeys)
	{
		NSString *value = (NSString *)[channelRegistry valueForKey:local];
		NSLog(@"Key: %@, Value: %@",local,value);
	}
	NSLog(@"*********************************************");
	
	//Read-Only Channels
	NSArray *readonly = [appService readonlyChannels];
	for(Channel *local in readonly)
	{
		NSLog(@"ReadOnly : %@, Owner: %@",local.name,local.owner);
	}
	
	//Writable Channels
	NSArray *writable = [appService writableChannels];
	for(Channel *local in writable)
	{
		NSLog(@"Writable : %@, Owner: %@",local.name,local.owner);
	}
	
	BOOL isWritable = [appService isWritable:@"webappsync_ticket_channel"];
	NSLog(@"IsWritable(websync_ticket_channel) : %d",isWritable);
	
	isWritable = [appService isWritable:@"push_mail_channel"];
	NSLog(@"IsWritable(push_mail_channel) : %d",isWritable);
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
-(void)startCloudService
{
	@try 
	{
		CloudService *cloudService = [CloudService getInstance:mainView];
		
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