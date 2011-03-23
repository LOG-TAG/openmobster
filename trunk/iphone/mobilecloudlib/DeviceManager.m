//
//  DeviceManager.m
//  mobilecloudlib
//
//  Created by openmobster on 3/17/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import "DeviceManager.h"


@implementation DeviceManager

+(DeviceManager *)getInstance
{
	Registry *registry = [Registry getInstance];
	return (DeviceManager *)[registry lookup:[DeviceManager class]];
}

-(void)sendOsCallback
{
	@try
	{
		Configuration *conf = [Configuration getInstance];
		if(![conf isActivated])
		{
			return;
		}
		
		NSString *os = @"iphone";
		NSString *version = [[UIDevice currentDevice] systemVersion];
		
		//Setup the Request
		Request *request = [Request withInit:@"dm_callback"];
		[request setAttribute:@"os" :os];
		[request setAttribute:@"version" :version];
		
		MobileService *mobileService = [MobileService withInit];
		[mobileService invoke:request];
	}
	@catch (SystemException *syse) 
	{
		//NSLog(@"DeviceManagement Exception occured!!!");
		//Do Nothing
		[ErrorHandler handleException:syse];
	}
}
@end
