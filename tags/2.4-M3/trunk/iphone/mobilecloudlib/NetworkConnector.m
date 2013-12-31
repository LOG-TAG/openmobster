/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "NetworkConnector.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation NetworkConnector

+(NetworkConnector *) getInstance
{
	Registry *registry = [Registry getInstance];
	return (NetworkConnector *)[registry lookup:[NetworkConnector class]];
}

-(NetSession *) openSession:(BOOL)secure
{
	Configuration *configuration = [Configuration getInstance];
	
	NSString *serverIp = configuration.serverIp;
	
	if([StringUtil isEmpty:serverIp])
	{
		NSMutableArray *parameters = [NSMutableArray arrayWithObjects:@"device_inactive",nil];
		SystemException *se = [SystemException withContext:@"NetworkConnector" method:@"openSession" parameters:parameters];
		@throw se;
	}
	
	NSNumber *sslActive = configuration.sslActive;
	NetSession *session;
	if(secure && [sslActive boolValue])
	{
		int port = [configuration.secureServerPort intValue];
		session = [NetSession withInit:YES :serverIp :port];
	}
	else 
	{
		int port = [configuration.plainServerPort intValue];
		session = [NetSession withInit:NO :serverIp :port];
	}

	
	return session;
}
@end
