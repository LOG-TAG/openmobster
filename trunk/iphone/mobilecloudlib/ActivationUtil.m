/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "ActivationUtil.h"


/**
 * 
 * @author openmobster@gmail.com
 */

NSString *const deviceIdentifier = @"IMEI:8675309"; //Do Not Modify

@implementation ActivationUtil

+(id) withInit
{
	return [[[ActivationUtil alloc] init] autorelease];
}

/** throws SystemException */
-(void)activateDevice:(TestSuite *)testSuite
{
	NSString *cloudServerIp = testSuite.cloudServer;
	NSString *email = testSuite.email;
	NSString *password = testSuite.password;
	
	[self bootup:deviceIdentifier :cloudServerIp :@"1502"];
	
	//Setup the Cloud Request
	Request *request = [Request withInit:@"provisioning"];
	[request setAttribute:@"email" :email];
	[request setAttribute:@"password" :password];
	[request setAttribute:@"identifier" :deviceIdentifier];
	
	MobileService *mobileService = [MobileService withInit];
	Response *response = [mobileService invoke:request];
	
	NSString *error = [response getAttribute:@"idm-error"];
	if(error != NULL)
	{
		//error scenario
		NSMutableArray *parameters = [NSMutableArray arrayWithObjects:error,nil];
		SystemException *se = [SystemException withContext:@"ActivationUtil" method:@"activateDevice" parameters:parameters];
		@throw se;
	}
	
	//success scenario
	[self processProvisioningSuccess:email :response];
	[self handlePostActivation];
}

//Used internally
-(void)processProvisioningSuccess:(NSString *)email :(Response *)response
{
	Configuration *config = [Configuration getInstance];
	
	NSString *authHash = [response getAttribute:@"authenticationHash"];
	config.email = email;
	config.authenticationHash = authHash;
	config.active = [NSNumber numberWithBool:YES];
	
	[config saveInstance];
}

-(void)bootup:(NSString *)deviceIdentifier :(NSString *)serverIp :(NSString *)port
{
	Configuration *config = [Configuration getInstance];
	
	config.sslActive = [NSNumber numberWithBool:NO];
	
	config.deviceId = deviceIdentifier;
	config.serverIp = serverIp;
	if(![StringUtil isEmpty:port])
	{
		config.plainServerPort = port;
	}
	config.active = [NSNumber numberWithBool:NO];
	config.authenticationHash = NULL;
	config.authenticationNonce = NULL;
	
	[config saveInstance];
	
	Request *request = [Request withInit:@"provisioning"];
	[request setAttribute:@"action" :@"metadata"];
	MobileService *mo = [MobileService withInit];
	Response *response = [mo invoke:request];
	
	//Read the Server Response
	NSString *serverId = [response getAttribute:@"serverId"];
	NSString *plainServerPort = [response getAttribute:@"plainServerPort"];
	NSString *secureServerPort = [response getAttribute:@"secureServerPort"];
	NSString *sslActive = [response getAttribute:@"isSSLActive"];
	NSString *maxPacketSize = [response getAttribute:@"maxPacketSize"];
	NSString *httpPort = [response getAttribute:@"httpPort"];
	
	//Setup the configuration
	config.serverId = serverId;
	config.plainServerPort = plainServerPort;
	if(![StringUtil isEmpty:secureServerPort])
	{
		config.secureServerPort = secureServerPort;
	}
	
	BOOL isSSLActive = [sslActive boolValue];
	if(isSSLActive)
	{
		config.sslActive = [NSNumber numberWithBool:YES];
	}
	else 
	{
		config.sslActive = [NSNumber numberWithBool:NO];
	}
	
	config.maxPacketSize = [NSNumber numberWithInt:[maxPacketSize intValue]];
	config.httpPort = httpPort;
	
	[config saveInstance];
}

-(void)handlePostActivation
{
	//TODO: Start the Push Daemon
	
	//TODO: Boot up the App sync channels
}
@end
