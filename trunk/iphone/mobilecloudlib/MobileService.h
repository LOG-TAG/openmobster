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

/** throws SystemException */
-(Response *)invoke:(Request *)request;

//Used internally
-(NSString *)sendRequest:(NSString *)beanRequest;
-(NSString *)serialize:(Request *)request;
-(Response *)parse:(NSString *)response;

@end
