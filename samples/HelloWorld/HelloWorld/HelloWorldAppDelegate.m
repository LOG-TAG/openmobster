//
//  HelloWorldAppDelegate.m
//  HelloWorld
//
//  Created by openmobster on 11/7/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import "HelloWorldAppDelegate.h"

@implementation HelloWorldAppDelegate


@synthesize window=_window;
@synthesize mobileos;
@synthesize desktopos;
@synthesize mobile;
@synthesize desktop;

-(id)init
{
    [super init];
    
    //setup the mobile os data
    self.mobile = [[NSMutableArray alloc] init];
    [mobile addObject:@"iOS"];
    [mobile addObject:@"Android"];
    [mobile addObject:@"Thats it!!! The rest suck ;)"];
    
    //setup the desktop os data
    self.desktop = [[NSMutableArray alloc] init];
    [desktop addObject:@"OSX"];
    [desktop addObject:@"Linux"];
    [desktop addObject:@"Winblows ;)"];
    
    return self;
}

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    // Override point for customization after application launch.
    [self.window makeKeyAndVisible];
    
    //show the initial mobile data
    mobilePtr = 0;
    self.mobileos.text = [self.mobile objectAtIndex:mobilePtr];
    
    //show the initial desktop data
    desktopPtr = 0;
    self.desktopos.text = [self.desktop objectAtIndex:desktopPtr];
    
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
}

-(IBAction)nextMobileOs:(id)sender
{
    mobilePtr++;
    if(mobilePtr >= [self.mobile count])
    {
        mobilePtr = 0;
    }
    
    self.mobileos.text = [self.mobile objectAtIndex:mobilePtr];
}

-(IBAction)nextDesktopOs:(id)sender;
{
    desktopPtr++;
    if(desktopPtr >= [self.desktop count])
    {
        desktopPtr = 0;
    }
    
    self.desktopos.text = [self.desktop objectAtIndex:desktopPtr];
}

- (void)dealloc
{
    [_window release];
    
    if(mobileos != nil)
    {
        [mobileos release];
    }
    
    if(desktopos != nil)
    {
        [desktopos release];
    }
    
    [mobile release];
    [desktop release];
    
    [super dealloc];
}
@end
