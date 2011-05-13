/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "AppConfig.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation AppConfig

@synthesize channelRegistry;

-init
{
	if(self == [super init])
	{
		channelRegistry = [[NSMutableArray alloc] init];
	}
	return self;
}

-(void)dealloc
{
	[channelRegistry release];
	[local release];
	[super dealloc];
}

+(AppConfig *)getInstance
{
	Registry *registry = [Registry getInstance];
	return (AppConfig *)[registry lookup:[AppConfig class]];
}

-(void)start
{	
	//Find the path to Resources/openmobster-app.xml
	NSString *path = [[[NSBundle mainBundle] resourcePath] stringByAppendingPathComponent:@"openmobster-app.xml"];
	NSURL *confUrl = [NSURL fileURLWithPath:path];
	
	NSXMLParser *parser = [[[NSXMLParser alloc] initWithContentsOfURL:confUrl] autorelease];
	
	//Set the Deletegate to self
	[parser setDelegate:self];
	
	//Start parsing
	[parser parse];
}

-(NSArray *)getChannels
{
	return channelRegistry;
}
//-------NSXMLParser delegate implementation--------------------------------------------
-(void)parserDidStartDocument:(NSXMLParser *)parser
{
}

-(void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict
{	
	if([elementName isEqualToString:@"channel"])
	{
		local = [[Channel alloc] init];
	
		NSString *access = [attributeDict objectForKey:@"access"];
		if([access isEqualToString:@"write"])
		{
			local.writable = YES;
		}
	}
}

-(void)parser:(NSXMLParser *)parser didEndElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName
{
	if([elementName isEqualToString:@"channel"])
	{
		[channelRegistry addObject:local];
	}
}

-(void)parser:(NSXMLParser *)parser foundCharacters:(NSString *)string
{
	if(![StringUtil isEmpty:string])
	{
		local.name = string;
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
			local.name = string;
		}		
	}
}
@end
