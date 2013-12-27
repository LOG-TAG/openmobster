/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "MobileService.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation MobileService

+(id)withInit
{
	MobileService *mo = [[[MobileService alloc] init] autorelease];
	return mo;
}


/** throws SystemException */
-(Response *)invoke:(Request *)request
{
	@try 
	{
		Response *beanResponse = nil;
		
		//Create a Bus invocation
		NSString *beanRequest = [self serialize:request];
		
		//Process the response
		NSString *beanResponseStr = [self sendRequest:beanRequest];
		
		if(![StringUtil isEmpty:beanResponseStr])
		{
			//Parse the response returned from the server
			beanResponse = [self parse:beanResponseStr];
		}
		else 
		{
			NSMutableArray *parameters = [NSMutableArray arrayWithObjects:@"network_error",nil];
			SystemException *se = [SystemException withContext:@"MobileService" method:@"invoke" parameters:parameters];
			@throw se;
		}
		
		return beanResponse;
	}
	@catch (SystemException *e) 
	{
		[ErrorHandler handleException:e];
		@throw e;
	}
}

-(NSString *)sendRequest:(NSString *)beanRequest
{
	NetSession *session = NULL;
	@try
	{
		Configuration *conf = [Configuration getInstance];
		BOOL secure = [conf.sslActive boolValue];
		BOOL isActive = [conf.active boolValue];
		NetworkConnector *connector = [NetworkConnector getInstance];
		session = [connector openSession:secure];
		
		NSString *handshake = NULL;
		if(isActive)
		{
			NSString *deviceId = conf.deviceId;
			NSString *authHash = conf.authenticationHash;
			
			handshake = [NSString stringWithFormat:_SERVICE_PAYLOAD_ACTIVE,deviceId,authHash];
		}
		else 
		{
			handshake = _SERVICE_PAYLOAD_INACTIVE;
		}
		
		
		NSString *response = [session performHandshake:handshake];
		if([StringUtil indexOf:response :@"status=200"] != -1)
		{
			return [session sendPayload:beanRequest];
		}
		
		return nil;
	}
	@finally 
	{
		if(session != NULL)
		{
			[session close];
		}
	}
}

-(NSString *)serialize:(Request *)request
{
	NSMutableString *buffer = [NSMutableString stringWithString:@""];
	
	[buffer appendString:@"<map>\n"];
	
	[buffer appendString:@"<entry>\n"];
	[buffer appendString:@"<string>servicename</string>\n"];
	[buffer appendFormat:@"<string>%@</string>\n",request.service];
	[buffer appendString:@"</entry>\n"];
	
	NSArray *names = [request getNames];
	if(names != nil)
	{
		for(NSString *local in names)
		{
			NSString *localValue = [request getAttribute:local];
			
			[buffer appendString:@"<entry>\n"];
			[buffer appendFormat:@"<string><![CDATA[%@]]></string>\n",local];
			[buffer appendFormat:@"<string><![CDATA[%@]]></string>\n",localValue];
			[buffer appendString:@"</entry>\n"];
		}
	}
	
	[buffer appendString:@"</map>\n"];
	
	return [NSString stringWithString:buffer];
}

-(Response *)parse:(NSString *)response
{
	NSData *xmlData = [response dataUsingEncoding:NSUTF8StringEncoding];
	NSXMLParser *parser = [[[NSXMLParser alloc] initWithData:xmlData] autorelease];
	
	//Set the Deletegate to self
	[parser setDelegate:self];
	
	//Start parsing
	[parser parse];
	
	return parsedResponse;
}

//------NSXMLParserDelegate implementation-------------------------------------------
-(void)parserDidStartDocument:(NSXMLParser *)parser
{
	parsedResponse = [Response withInit];
	fullPath = [NSMutableString stringWithString:@""];
	dataBuffer = [NSMutableString stringWithString:@""];
}

- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict
{
	[fullPath appendFormat:@"/%@",[StringUtil trim:elementName]];
	dataBuffer = [NSMutableString stringWithString:@""];
	
	if([fullPath isEqualToString:@"/map/entry"])
	{
		name = NULL;
		value = NULL;
	}
}

-(void)parser:(NSXMLParser *)parser didEndElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName
{
	if([fullPath isEqualToString:@"/map/entry/string"])
	{
		if(name == NULL)
		{
			name = [NSString stringWithString:dataBuffer];
		}
		else 
		{
			value = [NSString stringWithString:dataBuffer];
		}
	}
	else if([fullPath isEqualToString:@"/map/entry"])
	{
		[parsedResponse setAttribute:name :value];
	}
	
	//Reset
	int lastIndex = [StringUtil lastIndexOf:fullPath :@"/"];
	int length = [fullPath length];
	int numberOfCharactersToDelete = length - lastIndex;
	NSRange deleteRange = NSMakeRange(lastIndex, numberOfCharactersToDelete);
	[fullPath deleteCharactersInRange:deleteRange];
}

-(void)parser:(NSXMLParser *)parser foundCharacters:(NSString *)string
{
	if(![StringUtil isEmpty:string])
	{
		[dataBuffer appendString:string];
	}
}

-(void)parser:(NSXMLParser *)parser foundCDATA:(NSData *)CDATABlock
{
	if(CDATABlock != nil)
	{
		NSString *string = [[NSString alloc] initWithData:CDATABlock encoding:NSUTF8StringEncoding];
		string = [string autorelease];
		if(![StringUtil isEmpty:string])
		{
			[dataBuffer appendString:string];
		}		
	}
}
@end