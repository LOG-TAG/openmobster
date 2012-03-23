/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "ChannelBootstrapCommand.h"
#import "MobileBean.h"
#import "SyncService.h"
#import "AppSession.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation ChannelBootstrapCommand

+(id)withInit
{
	ChannelBootstrapCommand *instance = [[ChannelBootstrapCommand alloc] init];
	instance = [instance autorelease];
	return instance;
}

-(void)doAction:(CommandContext *)commandContext
{
	int counter = 5;
	
	while(![MobileBean isBooted:@"webappsync_ticket_channel"])
	{
		[NSThread sleepForTimeInterval:2];
		counter--;
		if(counter < 0)
		{
			NSMutableArray *params = [NSMutableArray arrayWithObjects:@"Channel is not ready. Try again in a few minutes",nil];
			SystemException *ex = [SystemException withContext:@"ChannelBootStrapCommand" method:@"doAction" parameters:params];
			@throw ex;
		}
	}
	
	//bootstrap sync for now until ProxyLoading is implemented
	AppSession *session = [AppSession getInstance];
	NSArray *beans = [MobileBean readAll:@"webappsync_ticket_channel"];
	
	if(beans == nil || [beans count] ==0)
	{
		SyncService *sync = [SyncService getInstance];
		[sync performBootSync:@"webappsync_ticket_channel" :NO];
		beans = [MobileBean readAll:@"webappsync_ticket_channel"];
	}
	[session removeAttribute:@"active-bean"];
	[session setAttribute:@"beans" :beans];
	
	/*for(MobileBean *local in beans)
	 {
	 NSLog(@"****Start*******************************************");
	 NSLog(@"Titel: %@",[local getValue:@"title"]);
	 NSLog(@"Comment: %@",[local getValue:@"comment"]);
	 NSLog(@"***********************************************");
	 }*/
}

@end
