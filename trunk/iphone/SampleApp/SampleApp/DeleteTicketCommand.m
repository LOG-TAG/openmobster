//
//  DeleteTicketCommand.m
//  SampleApp
//
//  Created by openmobster on 9/7/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "DeleteTicketCommand.h"

#import "SystemException.h"
#import "AppSession.h"
#import "MobileBean.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation DeleteTicketCommand

+(id)withInit
{
	DeleteTicketCommand *instance = [[DeleteTicketCommand alloc] init];
	instance = [instance autorelease];
	return instance;
}

-(void)doAction:(CommandContext *)commandContext
{
	AppSession *session = [AppSession getInstance];
	MobileBean *activeBean = [session getAttribute:@"active-bean"];
	
	//Actual Delete from the local database/sync with the Cloud
	NSString *oid = [activeBean getId];
	NSString *channel = [activeBean getChannel];
	MobileBean *deleteMe = [MobileBean readById:channel :oid];
	[deleteMe delete];
	
	//Session Cleanup
	[session removeAttribute:@"active-bean"];
	
	//Refresh
	NSArray *beans = [MobileBean readAll:@"webappsync_ticket_channel"];
	[session setAttribute:@"beans" :beans];
}

@end

