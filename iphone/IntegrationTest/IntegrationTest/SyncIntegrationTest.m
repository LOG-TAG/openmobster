//
//  SyncIntegrationTest.m
//  IntegrationTest
//
//  Created by openmobster on 8/12/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import "SyncIntegrationTest.h"
#import "MobileBean.h"

@implementation SyncIntegrationTest

+(id)withInit
{
    SyncIntegrationTest *instance = [[SyncIntegrationTest alloc] init];
    instance = [instance autorelease];
    return instance;
}

-(void)doAction:(CommandContext *)commandContext
{
    int counter = 5;
	
	while(![MobileBean isBooted:@"showcase_ticket_channel"])
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
    
    NSArray *beans = [MobileBean readAll:@"showcase_ticket_channel"];
	
	if(beans == nil || [beans count] ==0)
	{
		SyncService *sync = [SyncService getInstance];
		[sync performBootSync:@"showcase_ticket_channel" :NO];
		beans = [MobileBean readAll:@"showcase_ticket_channel"];
	}
    
    for(MobileBean *local in beans)
    {
        NSString *title = [local getValue:@"title"];
        NSString *comment = [local getValue:@"comment"];
        NSLog(@"****Start*******************************************");
        NSLog(@"Title: %@",title);
        NSLog(@"Comment: %@",comment);
        NSLog(@"***************************************************");
        
        //Update it now
        NSString *newTitle = [NSString stringWithFormat:@"updated://%@",title];
        NSString *newComment = [NSString stringWithFormat:@"updated://%@",comment];
        [local setValue:@"title" :newTitle];
        [local setValue:@"comment" :newComment];
        [local save];
    }
}
@end
