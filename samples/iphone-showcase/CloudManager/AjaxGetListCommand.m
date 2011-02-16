/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "AjaxGetListCommand.h"
#import "Request.h"
#import "Response.h"
#import "MobileService.h"
#import "AppSession.h"
#import "EmailBean.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation AjaxGetListCommand

+(id)withInit
{
	AjaxGetListCommand *command = [[AjaxGetListCommand alloc] init];
	[command autorelease];
	return command;
}

-(void)doAction:(CommandContext *)commandContext
{
	//Make an RPC invocation
	Request *request = [Request withInit:@"/asyncserviceapp/getlist"];
	MobileService *mobileService = [MobileService withInit];
	Response *response = [mobileService invoke:request];
	
	//Process the results
	NSArray *list = [response getListAttribute:@"subjects"];
	
	//Create a list of email beans
	NSMutableArray *emails = [NSMutableArray array];
	for(NSString *local in list)
	{
		EmailBean *email = [EmailBean withInit];
		NSArray *tokens = [local componentsSeparatedByString:@":"];
		
		//process id token
		NSString *idToken = [tokens objectAtIndex:0];
		NSArray *idTokens = [idToken componentsSeparatedByString:@"="];
		NSString *id = [idTokens objectAtIndex:1];
		email.oid = id;
		
		
		//process the subject token
		NSString *subjectToken = [tokens objectAtIndex:1];
		NSArray *subjectTokens = [subjectToken componentsSeparatedByString:@"="];
		NSString *subject = [subjectTokens objectAtIndex:1];
		email.subject = subject;
		
		[emails addObject:email];
	}
	
	
	//Prepare the context 
	[commandContext setAttribute:@"emails" :emails];
}

@end
