/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

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
