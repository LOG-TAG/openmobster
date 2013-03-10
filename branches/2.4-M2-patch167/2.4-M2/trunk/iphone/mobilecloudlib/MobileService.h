/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import <Foundation/Foundation.h>
#import "Request.h"
#import "Response.h"
#import "StringUtil.h"
#import "CloudPayload.h"
#import "Configuration.h"
#import "NetworkConnector.h"
#import "NetSession.h"
#import "SystemException.h"
#import "ErrorHandler.h"

/**
 * Mobile Service facilitates making invocations from the device to the server side Mobile Service Bean components
 *
 * @author openmobster@gmail.com
 */
@interface MobileService : NSObject<NSXMLParserDelegate> 
{
	@private 
	Response *parsedResponse;
	
	//parsing helpers
	NSMutableString *fullPath;
	NSMutableString *dataBuffer;
	NSString *name;
	NSString *value;
}

+(id)withInit;

/**
 * Invokes the remote Mobile Service component
 * 
 * @param request invocation request
 * @return invocation response
 * @throws SystemException
 */
-(Response *)invoke:(Request *)request;

//Used internally
/**
 * This is for internal use only
 */
-(NSString *)sendRequest:(NSString *)beanRequest;

/**
 * This is for internal use only
 */
-(NSString *)serialize:(Request *)request;

/**
 * This is for internal use only
 */
-(Response *)parse:(NSString *)response;

@end
