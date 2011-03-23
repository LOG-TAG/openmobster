/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "XMLUtil.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation XMLUtil

+(NSString *) cleanupXML:(NSString *)xml
{
	if([StringUtil isEmpty:xml])
	{
		return @"";
	}
	
	NSMutableString *cleanXml = [NSMutableString stringWithString:xml];
	
	NSRange searchRange = NSMakeRange(0, [cleanXml length]);
	[cleanXml replaceOccurrencesOfString:@"&" withString:@"&amp;" options:NSLiteralSearch range:searchRange];
	
	searchRange = NSMakeRange(0, [cleanXml length]);
	[cleanXml replaceOccurrencesOfString:@"<" withString:@"&lt;" options:NSLiteralSearch range:searchRange];
	
	searchRange = NSMakeRange(0, [cleanXml length]);
	[cleanXml replaceOccurrencesOfString:@">" withString:@"&gt;" options:NSLiteralSearch range:searchRange];
	
	searchRange = NSMakeRange(0, [cleanXml length]);
	[cleanXml replaceOccurrencesOfString:@"\"" withString:@"&quot;" options:NSLiteralSearch range:searchRange];

	searchRange = NSMakeRange(0, [cleanXml length]);
	[cleanXml replaceOccurrencesOfString:@"'" withString:@"&apos;" options:NSLiteralSearch range:searchRange];

	return cleanXml;
}

+(NSString *) restoreXML:(NSString *)xml
{
	if([StringUtil isEmpty:xml])
	{
		return @"";
	}
	
	NSMutableString *restoreXml = [NSMutableString stringWithString:xml];
	
	NSRange searchRange = NSMakeRange(0, [restoreXml length]);
	[restoreXml replaceOccurrencesOfString:@"&apos;" withString:@"'" options:NSLiteralSearch range:searchRange];
	
	searchRange = NSMakeRange(0, [restoreXml length]);
	[restoreXml replaceOccurrencesOfString:@"&quot;" withString:@"\"" options:NSLiteralSearch range:searchRange];
	
	searchRange = NSMakeRange(0, [restoreXml length]);
	[restoreXml replaceOccurrencesOfString:@"&gt;" withString:@">" options:NSLiteralSearch range:searchRange];
	
	searchRange = NSMakeRange(0, [restoreXml length]);
	[restoreXml replaceOccurrencesOfString:@"&lt;" withString:@"<" options:NSLiteralSearch range:searchRange];
	
	searchRange = NSMakeRange(0, [restoreXml length]);
	[restoreXml replaceOccurrencesOfString:@"&amp;" withString:@"&" options:NSLiteralSearch range:searchRange];
	
	return restoreXml;
}

+(NSString *) removeCData:(NSString *) xml
{
	NSMutableString *withOut = [NSMutableString stringWithString:xml];
	
	NSRange searchRange = NSMakeRange(0, [withOut length]);
	[withOut replaceOccurrencesOfString:@"<![CDATA[" withString:@"" options:NSLiteralSearch range:searchRange];
	
	searchRange = NSMakeRange(0, [withOut length]);
	[withOut replaceOccurrencesOfString:@"]]>" withString:@"" options:NSLiteralSearch range:searchRange];
	
	return withOut;
}

+(NSString *) addCData:(NSString *) xml
{
	NSMutableString *withCData = [NSMutableString stringWithString:@"<![CDATA["];
	
	[withCData appendString:xml];
	[withCData appendString:@"]]>"];
	
	return withCData;
}

@end
