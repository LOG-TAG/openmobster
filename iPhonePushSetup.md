#### Introduction ####
This is a guide to integrate the iPhone Apple Push Notification (APN) based system with the OpenMobster Push Service. It consists of several provisioning steps from the Apple side and integration via the Management Console on the OpenMobster Side.


---


#### Apple Provisioning ####

##### Step 1: Obtain the Application Certificate #####

In order to push via the APN service, the provider side (OpenMobster->APN connection) requires a certificate for each App registered for Push Notifications. The best instructions for doing the proper provisioning and obtaining a certificate is explained at : [http://mobiforge.com/developing/story/programming-apple-push-notification-services](http://mobiforge.com/developing/story/programming-apple-push-notification-services).

##### Step 2: Getting an aps\_production\_identity.p12 certificate #####

Once you have downloaded the aps\_production\_identity.cer file from the Apple Provisioning Portal

  * Import the aps\_production\_identity.cer into the KeyChain. Double-clicking the file will do it

  * Select both certificate and private key (associated to the application you wish to use to send notifications)

  * Right click, and select Export 2 elements, give a name (for example : aps\_production\_identity.p12) and password (for example : p@ssw0rd) and then export as p12.


---


#### OpenMobster Provisioning ####

##### Step 1: Register the App and the Device Token #####

On the OpenMobster side, Apps that want Push notifications must be registered with the OpenMobster system. The Device Token is also needed to be registered as it is a requirement for the Apple Push Notification Service. This registration is as follows:

  * Code of Application Delegate:
```
     - (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {    
    
    // Override point for customization after application launch.

    // Add the view controller's view to the window and display.
    [self.window addSubview:viewController.view];
    [self.window makeKeyAndVisible];
	
	//Bootstrap the Cloud services
	[self startCloudService];
	
    //This registers the App for Push Notifications
    [[UIApplication sharedApplication] 
	 registerForRemoteNotificationTypes:
	 (UIRemoteNotificationTypeAlert | 
	  UIRemoteNotificationTypeBadge | 
	  UIRemoteNotificationTypeSound)];

    return YES;
}

```

  * If the registation is successful a callback is invoked on the delegate. It goes as follows:
```
     - (void)application:(UIApplication *)app didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken 
{ 
	NSString *deviceTokenStr = [NSString stringWithFormat:@"%@",deviceToken];
	deviceTokenStr = [StringUtil replaceAll:deviceTokenStr :@"<" :@""];
	deviceTokenStr = [StringUtil replaceAll:deviceTokenStr :@">" :@""];
	
	NSLog(@"DeviceToken: %@",deviceTokenStr);

	@try 
	{
		SubmitDeviceToken *submit = [SubmitDeviceToken withInit];
		[submit submit:deviceTokenStr];
	}
	@catch (SystemException * syse) 
	{
		UIAlertView *dialog = [[UIAlertView alloc] 
							   initWithTitle:@"Token Registration Error"
							   message:@"Device Token Cloud Registration Failed. Please make sure your device is activated with the Cloud using the ActivationApp. Re-start this App to start the token registration again" 
							   delegate:nil 
							   cancelButtonTitle:@"OK" otherButtonTitles:nil];
		dialog = [dialog autorelease];
		[dialog show];
	}
}
```

These two operations registers the Application for Push notifications both on the device and on the OpenMobster Push Service.

##### Step 2: Upload the certificate .p12 file #####

  * Login to the Management Console: http://cloud-server-address/console

  * Select **Push Setup**

  * Find the App associated with this certificate

  * Upload the certificate and supply its password

  * If successfull, the icon next to the App will turn green

##### Step 3: Send a Test Push #####

  * Click on the App

  * Click the 'Test Push' button

  * Select the 'Device' where it should be sent

  * You should receive a Push alert on your phone