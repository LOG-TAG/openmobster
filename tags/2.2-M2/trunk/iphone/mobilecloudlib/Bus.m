/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "Bus.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation Bus

-init
{
	if(self == [super init])
	{
		sharedConf = [UIPasteboard pasteboardWithName:@"openmobster_shared_conf" create:YES];
		if(!sharedConf.persistent)
		{
			sharedConf.persistent = YES;
		}
	}
	return self;
}


+(Bus *)getInstance
{
	Registry *registry = [Registry getInstance];
	return (Bus *)[registry lookup:[Bus class]];
}

-(void)synchronizeConf
{
	Configuration *myConf = [Configuration getInstance];
	
	//Read the shared configuration payload from the pasteboard
	NSString *confXml = (NSString *)[sharedConf valueForPasteboardType:@"public.utf8-plain-text"];
	if([StringUtil isEmpty:confXml])
	{
		//write your own conf to the pasteboard
		[self postSharedConf:myConf];
		return;
	}
	
	//A Configuration found on the pasteboard...this will be the latest, replace my own
	//conf with this one...90% of times these are the same
	[self replaceWithSharedConf :confXml];
}

//Used internally
-(void)replaceWithSharedConf:(NSString *)confXml
{
	[self xmlToConf:confXml];
	sharedInstance = nil;
}

-(void)postSharedConf:(Configuration *)conf
{
	NSString *confXml = [self confToXml:conf];
	[sharedConf setValue:confXml forPasteboardType:@"public.utf8-plain-text"];
}

-(NSString *)confToXml:(Configuration *)conf
{
	NSMutableString *buffer = [NSMutableString stringWithString:@""];
	
	[buffer appendFormat:@"<properties class='%@'>\n",[conf class]];
	
	[buffer appendFormat:@"<device-id><![CDATA[%@]]></device-id>\n",conf.deviceId];
	[buffer appendFormat:@"<server-id><![CDATA[%@]]></server-id>\n",conf.serverId];
	[buffer appendFormat:@"<server-ip><![CDATA[%@]]></server-ip>\n",conf.serverIp];
	[buffer appendFormat:@"<plain-server-port><![CDATA[%@]]></plain-server-port>\n",conf.plainServerPort];
	[buffer appendFormat:@"<secure-server-port><![CDATA[%@]]></secure-server-port>\n",conf.secureServerPort];
	[buffer appendFormat:@"<http-port><![CDATA[%@]]></http-port>\n",conf.httpPort];
	[buffer appendFormat:@"<auth-hash><![CDATA[%@]]></auth-hash>\n",conf.authenticationHash];
	[buffer appendFormat:@"<auth-nonce><![CDATA[%@]]></auth-nonce>\n",conf.authenticationNonce];
	[buffer appendFormat:@"<email><![CDATA[%@]]></email>\n",conf.email];
	
	//Handle the NSNumbers
	[buffer appendFormat:@"<active><![CDATA[%@]]></active>\n",[conf.active stringValue]];
	[buffer appendFormat:@"<ssl-active><![CDATA[%@]]></ssl-active>\n",[conf.sslActive stringValue]];
	[buffer appendFormat:@"<max-packet-size><![CDATA[%@]]></max-packet-size>\n",[conf.maxPacketSize stringValue]];
	[buffer appendFormat:@"<poll-interval><![CDATA[%@]]></poll-interval>\n",[conf.pollInterval stringValue]];
	[buffer appendFormat:@"<push-mode><![CDATA[%@]]></push-mode>\n",[conf.pushMode stringValue]];
	
	//Handle the system channels
	if(conf.channels != nil && [conf.channels count] >0)
	{
		[buffer appendString:@"<system-channels>\n"];
		
		NSArray *allKeys = [conf.channels allKeys];
		for(NSString *systemChannel in allKeys)
		{
			NSString *owner = (NSString *)[conf.channels objectForKey:systemChannel];
			[buffer appendString:@"<channel>\n"];
			[buffer appendFormat:@"<name><![CDATA[%@]]></name>\n",systemChannel];
			[buffer appendFormat:@"<owner><![CDATA[%@]]></owner>\n",owner];
			[buffer appendString:@"</channel>\n"];
		}
		
		[buffer appendString:@"</system-channels>\n"];
	}	
	
	[buffer appendString:@"</properties>"];
	
	return [NSString stringWithString:buffer];
}

-(void)xmlToConf:(NSString *)xml
{
	sharedInstance = [Configuration getInstance];
	NSData *xmlData = [xml dataUsingEncoding:NSUTF8StringEncoding];
	NSXMLParser *parser = [[[NSXMLParser alloc] initWithData:xmlData] autorelease];
	
	//Set the Deletegate to self
	[parser setDelegate:self];
	
	//Start parsing
	[parser parse];
	dataBuffer = nil;
	fullPath = nil;
	sharedChannel = nil;
	
	[sharedInstance saveInstance];
}
//----------------------------------------------------------------------------------------
-(void)parserDidStartDocument:(NSXMLParser *)parser
{
	fullPath = [NSMutableString stringWithString:@""];
	dataBuffer = [NSMutableString stringWithString:@""];
}

-(void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict
{
	[fullPath appendFormat:@"/%@",[StringUtil trim:elementName]];
	dataBuffer = [NSMutableString stringWithString:@""];
	
	if([fullPath hasSuffix:@"/system-channels/channel"])
	{
		sharedChannel = [Channel withInit];
	}
}

-(void)parser:(NSXMLParser *)parser didEndElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName
{
	NSString *local = [NSString stringWithString:dataBuffer];
	
	//NSStrings
	if([fullPath hasSuffix:@"/device-id"])
	{
		if(![local isEqualToString:@"(null)"])
		{
			sharedInstance.deviceId = local;
		}
		else 
		{
			sharedInstance.deviceId = @"";
		}
	}
	else if([fullPath hasSuffix:@"/server-id"])
	{
		if(![local isEqualToString:@"(null)"])
		{
			sharedInstance.serverId = local;
		}
		else 
		{
			sharedInstance.serverId = @"";
		}
	}
	else if([fullPath hasSuffix:@"/server-ip"])
	{
		if(![local isEqualToString:@"(null)"])
		{
			sharedInstance.serverIp = local;
		}
		else 
		{
			sharedInstance.serverIp = @"";
		}
	}
	else if([fullPath hasSuffix:@"/plain-server-port"])
	{
		if(![local isEqualToString:@"(null)"])
		{
			sharedInstance.plainServerPort = local;
		}
		else 
		{
			sharedInstance.plainServerPort = @"";
		}
	}
	else if([fullPath hasSuffix:@"/secure-server-port"])
	{
		if(![local isEqualToString:@"(null)"])
		{
			sharedInstance.secureServerPort = local;
		}
		else 
		{
			sharedInstance.secureServerPort = @"";
		}
	}
	else if([fullPath hasSuffix:@"/http-port"])
	{
		if(![local isEqualToString:@"(null)"])
		{
			sharedInstance.httpPort = local;
		}
		else 
		{
			sharedInstance.httpPort = @"";
		}
	}
	else if([fullPath hasSuffix:@"/auth-hash"])
	{
		if(![local isEqualToString:@"(null)"])
		{
			sharedInstance.authenticationHash = local;
		}
		else 
		{
			sharedInstance.authenticationHash = @"";
		}
	}
	else if([fullPath hasSuffix:@"/auth-nonce"])
	{
		if(![local isEqualToString:@"(null)"])
		{
			sharedInstance.authenticationNonce = local;
		}
		else 
		{
			sharedInstance.authenticationNonce = @"";
		}
	}
	else if([fullPath hasSuffix:@"/email"])
	{
		if(![local isEqualToString:@"(null)"])
		{
			sharedInstance.email = local;
		}
		else 
		{
			sharedInstance.email = @"";
		}
	}
	
	//NSNumbers
	else if([fullPath hasSuffix:@"/active"])
	{
		if(![local isEqualToString:@"(null)"])
		{
			sharedInstance.active = [NSNumber numberWithBool:[local boolValue]];
		}
		else 
		{
			sharedInstance.active = [NSNumber numberWithBool:NO];
		}
	}
	else if([fullPath hasSuffix:@"/ssl-active"])
	{
		if(![local isEqualToString:@"(null)"])
		{
			sharedInstance.sslActive = [NSNumber numberWithBool:[local boolValue]];
		}
		else 
		{
			sharedInstance.sslActive = [NSNumber numberWithBool:NO];
		}
	}
	else if([fullPath hasSuffix:@"/max-packet-size"])
	{
		if(![local isEqualToString:@"(null)"])
		{
			sharedInstance.maxPacketSize = [NSNumber numberWithInt:[local intValue]];
		}
		else 
		{
			sharedInstance.maxPacketSize = [NSNumber numberWithInt:0];
		}
	}
	else if([fullPath hasSuffix:@"/poll-interval"])
	{
		if(![local isEqualToString:@"(null)"])
		{
			sharedInstance.pollInterval = [NSNumber numberWithInt:[local intValue]];
		}
		else 
		{
			sharedInstance.pollInterval = [NSNumber numberWithInt:0];
		}
	}
	else if([fullPath hasSuffix:@"/push-mode"])
	{
		if(![local isEqualToString:@"(null)"])
		{
			sharedInstance.pushMode = [NSNumber numberWithInt:[local intValue]];
		}
		else 
		{
			sharedInstance.pushMode = [NSNumber numberWithInt:0];
		}
	}
	
	//Collections
	else if([fullPath hasSuffix:@"/system-channels/channel/name"])
	{
		sharedChannel.name = local;
	}
	else if([fullPath hasSuffix:@"/system-channels/channel/owner"])
	{
		sharedChannel.owner = local;
	}
	//Close out a channel instance
	else if([fullPath hasSuffix:@"/system-channels/channel"])
	{
		[sharedInstance establishOwnership:sharedChannel :NO];
		sharedChannel = nil; //clear out
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