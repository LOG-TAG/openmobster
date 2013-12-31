//
//  SubmitDeviceToken.m
//  mobilecloudlib
//
//  Created by openmobster on 4/28/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import "SubmitDeviceToken.h"
#import "AppService.h"
#import "Request.h"
#import "Response.h"
#import "MobileService.h"


@implementation SubmitDeviceToken

+(id)withInit
{
	SubmitDeviceToken *submit = [[[SubmitDeviceToken alloc] init] autorelease];
	return submit;
}

-(void)submit:(NSString *)deviceToken
{
		AppService *appService = [AppService getInstance];
		NSString *os = @"iphone";
		NSString *appId = [[NSBundle mainBundle] objectForInfoDictionaryKey:@"CFBundleDisplayName"];
		NSArray *myChannels = [appService myChannels];
		NSMutableArray *channelNames = [NSMutableArray array];
		
		if(myChannels != nil && [myChannels count]>0)
		{
			for(Channel *local in myChannels)
			{
				[channelNames addObject:local.name];
			}
		}
		
		//Register this information with the Cloud
		Configuration *conf = [Configuration getInstance];
		if(![conf isActivated])
		{
			return;
		}
		
		//Setup the Request
		Request *request = [Request withInit:@"iphone_push_callback"];
		[request setAttribute:@"os" :os];
		[request setAttribute:@"deviceToken" :deviceToken];
		[request setAttribute:@"appId" :appId];
		[request setListAttribute:@"channels" :[NSArray arrayWithArray:channelNames]];
		
		//Setup MobileService
		MobileService *mobileService = [MobileService withInit];
		[mobileService invoke:request];
}
@end
