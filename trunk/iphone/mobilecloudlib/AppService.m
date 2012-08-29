/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "AppService.h"
#import "Bus.h"
#import "SyncUtility.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation AppService

@synthesize writableChannels;
@synthesize allChannels;

-init
{
	if(self == [super init])
	{
		allChannels = [[NSMutableArray alloc] init];
		writableChannels = [[NSMutableArray alloc] init];
	}
	
	return self;
}

-(void)dealloc
{
	[allChannels release];
	[writableChannels release];
	
	[super dealloc];
}

+(AppService *) getInstance
{
	Registry *registry = [Registry getInstance];
	return (AppService *)[registry lookup:[AppService class]];
}

-(void)start
{
	//Synchronize the local Conf with System Conf
	Bus *bus = [Bus getInstance];
	[bus synchronizeConf];
	
	Configuration *conf = [Configuration getInstance];
	NSString *owner = [[NSBundle mainBundle] objectForInfoDictionaryKey:@"CFBundleDisplayName"];
	AppConfig *appConfig = [AppConfig getInstance];
	
	//Load up registered channels
	NSArray *channelRegistry = [appConfig getChannels];
	BOOL broadcastConf = NO;
	if(channelRegistry != nil)
	{
		for(Channel *local in channelRegistry)
		{
			local.owner = owner;
				
			if(local.writable)
			{
                //Making all channels writable
				//see if this can be made owner system-wide
				/*BOOL ownership = [conf establishOwnership:local :NO];
				if(ownership)
				{
					[writableChannels addObject:local];
					broadcastConf = YES;
				}*/
                //Making all channels writable
                [writableChannels addObject:local];
			}
			[allChannels addObject:local];
		}
	}
	
	if(broadcastConf)
	{
		[bus postSharedConf:conf];
	}
	
	//Start the Sync Daemons
	//NSLog(@"ISActivated: %d",[conf isActivated]);
	if([conf isActivated])
	{
	//	NSLog(@"Starting Sync Daemons...........");
		//SyncService *sync = [SyncService getInstance];
		//[sync startDaemons];
        //perform a sync operation to get the data up-to-date
        SyncUtility *syncUtility = [SyncUtility withInit];
        [syncUtility syncAll];
	}
	
	//MyChannles
	//NSArray *myChannels = [self allChannels];
	//if(myChannels != nil && [myChannels count] >0)
	//{
	//	for(Channel *local in myChannels)
	//	{
	//		NSLog(@"Channel: %@",local.name);
	//	}
	//}
	//else 
	//{
	//	NSLog(@"Empty Channels......");
	//}
}

-(NSArray *)myChannels
{
	return allChannels;
}

-(BOOL)isWritable:(NSString *)channel
{
	for(Channel *local in writableChannels)
	{
		NSString *name = local.name;
		if([name isEqualToString:channel])
		{
			return YES;
		}
	}
	return NO;
}

-(NSArray *)writableChannels
{
	return writableChannels;
}

-(NSArray *)readonlyChannels
{
	NSMutableArray *readonly = [NSMutableArray array];
	
	for(Channel *local in allChannels)
	{
		BOOL isWritable = [self isWritable:local.name];
		if(!isWritable)
		{
			[readonly addObject:local];
		}
	}
	
	return [NSArray arrayWithArray:readonly];
}
@end
