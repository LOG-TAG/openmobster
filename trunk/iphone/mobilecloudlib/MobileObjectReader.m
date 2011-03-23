/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "MobileObjectReader.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation MobileObjectReader

+(id) withInit
{
	return [[[MobileObjectReader alloc] init] autorelease];
}

-(MobileObject *)parse:(NSString *)xml
{
	NSData *xmlData = [xml dataUsingEncoding:NSUTF8StringEncoding];
	NSXMLParser *parser = [[[NSXMLParser alloc] initWithData:xmlData] autorelease];
	
	//Set the Deletegate to self
	[parser setDelegate:self];
	
	//Start parsing
	[parser parse];
	
	MobileObject *mobileObject = [MobileObject withInit];
	mobileObject.recordId = recordId;
	mobileObject.serverRecordId = serverRecordId;
	mobileObject.proxy = proxy;
	mobileObject.fields = fields;
	mobileObject.arrayMetaData = arrayMetaData;
	
	return mobileObject;
}
//----------------------------------------------------------------------------------
-(void)parserDidStartDocument:(NSXMLParser *)parser
{
	fields = [NSMutableArray array];
	arrayMetaData = [NSMutableArray array];
	fullPath = [NSMutableString string];
	dataBuffer = [NSMutableString string];
	currentField = [Field withInit];
	currentMetaData = [ArrayMetaData withInit];
	currentElement = nil;
	recordId = nil;
	serverRecordId = nil;
	proxy = NO;
}

- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict
{
	[fullPath appendFormat:@"/%@",[StringUtil trim:elementName]];
	currentElement = [StringUtil trim:elementName];
}

-(void)parser:(NSXMLParser *)parser didEndElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName
{
	[self closeDataBuffer];
	
	if([currentElement isEqualToString:@"proxy"])
	{
		proxy = YES;
	}
	
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

-(void)closeDataBuffer
{
	if([StringUtil isEmpty:fullPath])
	{
		return;
	}
	
	if([fullPath hasSuffix:@"/recordId"])
	{
		recordId = [StringUtil trim:dataBuffer];
	}
	else if([fullPath hasSuffix:@"/serverRecordId"])
	{
		serverRecordId = [StringUtil trim:dataBuffer];
	}				
	else if([fullPath hasSuffix:@"/field/uri"])
	{
		currentField.uri = [StringUtil trim:dataBuffer];
	}
	else if([fullPath hasSuffix:@"/field/name"])
	{
		currentField.name = [StringUtil trim:dataBuffer];
	}
	else if([fullPath hasSuffix:@"/field/value"])
	{
		currentField.value = [StringUtil trim:dataBuffer];
		[fields addObject:currentField];
		
		currentField = [Field withInit];
	}
	else if([fullPath hasSuffix:@"/array-metadata/uri"])
	{
		currentMetaData.arrayUri = [StringUtil trim:dataBuffer];					
	}				
	else if([fullPath hasSuffix:@"/array-metadata/array-length"])
	{
		currentMetaData.arrayLength = [StringUtil trim:dataBuffer];										
	}
	else if([fullPath hasSuffix:@"/array-metadata/array-class"])
	{
		currentMetaData.arrayClass = [StringUtil trim:dataBuffer];
		
		[arrayMetaData addObject:currentMetaData];
		currentMetaData = [ArrayMetaData withInit];
	}
	
	dataBuffer = [NSMutableString string];
}
@end