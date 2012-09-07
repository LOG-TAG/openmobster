//
//  SaveTicketCommand.m
//  SampleApp
//
//  Created by openmobster on 9/7/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "SaveTicketCommand.h"

#import "AppSession.h"
#import "MobileBean.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation SaveTicketCommand

+(id)withInit
{
	SaveTicketCommand *instance = [[SaveTicketCommand alloc] init];
	instance = [instance autorelease];
	return instance;
}

-(void)doAction:(CommandContext *)commandContext
{
	NSString *title = [commandContext getAttribute:@"title"];
	NSString *comment = [commandContext getAttribute:@"comment"];
	
	AppSession *session = [AppSession getInstance];
	MobileBean *activeBean = [session getAttribute:@"active-bean"];
	if(activeBean != nil)
	{
		//Update
		NSString *oid = [activeBean getId];
		NSString *channel = [activeBean getChannel];
		MobileBean *update = [MobileBean readById:channel :oid];
		[update setValue:@"title" :title];
		[update setValue:@"comment" :comment];
		[update save];
	}
	else 
	{
		//Create
		MobileBean *newBean = [MobileBean newInstance:@"webappsync_ticket_channel"];
		[newBean setValue:@"title" :title];
		[newBean setValue:@"comment" :comment];
		[newBean save];
	}
	
	//Session Cleanup
	[session removeAttribute:@"active-bean"];
	
	//Refresh
	NSArray *beans = [MobileBean readAll:@"webappsync_ticket_channel"];
	[session setAttribute:@"beans" :beans];
	
	/*for(MobileBean *local in beans)
     {
     NSLog(@"******Save*****************************************");
     NSLog(@"Titel: %@",[local getValue:@"title"]);
     NSLog(@"Comment: %@",[local getValue:@"comment"]);
     NSLog(@"***********************************************");
     }*/
    
}
@end