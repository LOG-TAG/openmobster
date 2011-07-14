/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "AjaxGetDetailsCommand.h"
#import "Request.h"
#import "Response.h"
#import "MobileService.h"
#import "AppSession.h"
#import "EmailBean.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation AjaxGetDetailsCommand

+(id)withInit
{
	AjaxGetDetailsCommand *instance = [[AjaxGetDetailsCommand alloc] init];
	instance = [instance autorelease];
	return instance;
}

-(void)doAction:(CommandContext *)commandContext
{
	MobileService *ms = [MobileService withInit];
	EmailBean *email = [commandContext getAttribute:@"email"];
	
	//Make the server invocation to get the email details
	Request *request = [Request withInit:@"/asyncserviceapp/getdetails"];
	[request setAttribute:@"oid" :email.oid];
	Response *response = [ms invoke:request];
	
	//Process the cloud response
	email.oid = [response getAttribute:@"oid"];
	email.from = [response getAttribute:@"from"];
	email.to = [response getAttribute:@"to"];
	email.subject = [response getAttribute:@"subject"];
	email.date = [response getAttribute:@"date"];
}
@end
