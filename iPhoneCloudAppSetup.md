### Introduction ###

When developing for the iPhone, OpenMobster is integrated as a static library. Besides the library, there are other resources that also need to be linked to within your project. This is a step-by-step guide that lets you accomplish this task.

#### Step 1: Locating the projects ####

For simplicity, if possible try to locate the two projects (Your project and mobilecloudlib project) within the same directory.

#### Step 2: Link libmobilecloudlib.a file ####

In your project, under Frameworks, do "Add Existing Files", locate the library stored in the build folder of mobilecloudlib.

#### Step 3: Link the required Frameworks ####
Under Frameworks, do "Add Existing Frameworks", locate both the frameworks and add them

  * CoreData.framework
  * CFNetwork.framework
  * CoreGraphics.framework
  * UIKit.framework

#### Step 4: Copy mobilecloudlib Resources to a new folder named OpenMobster ####

  * Create a New group named OpenMobster
  * From the mobilecloudlibproject, DragnDrop/Copy all the resources located under the app-bundle group

#### Step 5: Integrate via the App Delegate ####

You can add code to start and stop the Cloud infrastructure within your App delegate object. The code for doing this follows:

##### Start Cloud Service #####
```
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
```

##### Stop Cloud Service #####
```
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
```

##### Start Device Activation if it is not activated with the Cloud already #####
```
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
```

##### Do a Sync at Startup #####
```
-(void)sync
{
    CommandContext *commandContext = [CommandContext withInit:self.viewController];
    BackgroundSyncCommand *syncCommand = [BackgroundSyncCommand withInit];
    [commandContext setTarget:syncCommand];
    CommandService *service = [CommandService getInstance];
    [service execute:commandContext]; 
}
```

The above mentioned functions are then integrated with the App Delegate lifecycle to integrate the Cloud infrastructure with the App. The integration goes as follows in the lifecycle methods.

##### -(BOOL)application:(UIApplication **)application didFinishLaunchingWithOptions:(NSDictionary**)launchOptions #####
```
- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    //OpenMobster bootstrapping
    [self startCloudService];
    [self sync];
    
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
```

##### -(void)applicationWillEnterForeground:(UIApplication **)application #####
```
- (void)applicationWillEnterForeground:(UIApplication *)application
{
    /*
     Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
     */
    //OpenMobster bootstrapping
    [self sync];
    
    if(!self.pushRegistered)
    {
        [[UIApplication sharedApplication] 
         registerForRemoteNotificationTypes:
         (UIRemoteNotificationTypeAlert | 
          UIRemoteNotificationTypeBadge | 
          UIRemoteNotificationTypeSound)];
    }
}
```**

##### -(void)applicationWillTerminate:(UIApplication 