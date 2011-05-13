/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "ActivateDevice.h"
#import "Bus.h"
#import "DeviceManager.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation ActivateDevice

+(id)withInit
{
	ActivateDevice *instance = [[ActivateDevice alloc] init];
	instance = [instance autorelease];
	return instance;
}

-(void)doAction:(CommandContext *)commandContext
{
	NSString *login = (NSString *)[commandContext getAttribute:@"login"];
	NSString *password = (NSString *)[commandContext getAttribute:@"password"];
	NSString *cloudIp = (NSString *)[commandContext getAttribute:@"cloudIp"];
	NSString *cloudPort = (NSString *)[commandContext getAttribute:@"cloudPort"];
	NSString *deviceIdentifier = [GeneralTools getDeviceIdentifier];
	
	//Activate the device
	[self activateDevice:deviceIdentifier :cloudIp :cloudPort];
	
	//Activate the account
	//Setup the Cloud Request
	Request *request = [Request withInit:@"provisioning"];
	[request setAttribute:@"email" :login];
	[request setAttribute:@"password" :password];
	[request setAttribute:@"identifier" :deviceIdentifier];
	
	MobileService *mobileService = [MobileService withInit];
	Response *response = [mobileService invoke:request];
	
	NSString *errorKey = [response getAttribute:@"idm-error"];
	if(errorKey == nil)
	{
		//success
		[self processProvisioningSuccess:login :response];
	}
	else 
	{
		//handle error
		SystemException *syse = [SystemException withContext:@"ActivateDevice" method:@"doAction" parameters:[NSMutableArray arrayWithObjects:errorKey,deviceIdentifier,cloudIp,login,nil]];
		[ErrorHandler handleException:syse];
		
		//TODO: Make this properly fetch an Error Message corresponding to an Error Key
		AppException *appe = [AppException withInit:@"Device Activation" :errorKey];
		@throw appe;
	}	
}

-(void)activateDevice:(NSString *)deviceIdentifier: (NSString *)cloudIp :(NSString *)cloudPort
{
	Configuration *config = [Configuration getInstance];
	
	config.sslActive = [NSNumber numberWithBool:NO];
	
	config.deviceId = deviceIdentifier;
	config.serverIp = cloudIp;
	if(![StringUtil isEmpty:cloudPort])
	{
		config.plainServerPort = cloudPort;
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

-(void)processProvisioningSuccess:(NSString *)email :(Response *)response;
{
	Configuration *conf = [Configuration getInstance];
	
	NSString *authHash = [response getAttribute:@"authenticationHash"];
	conf.email = email;
	conf.authenticationHash = authHash;
	conf.authenticationNonce = authHash;
	conf.active = [NSNumber numberWithBool:YES];
	
	[conf saveInstance];
	Bus *bus = [Bus getInstance];
	[bus postSharedConf:conf];
	
	//Register Device Meta Data with the Cloud
	DeviceManager *dm = [DeviceManager getInstance];
	[dm sendOsCallback];
}
@end
