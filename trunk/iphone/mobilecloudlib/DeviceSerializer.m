/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "DeviceSerializer.h"

static DeviceSerializer *singleton = nil;

/**
 * @author openmobster@gmail.com
 */
@implementation DeviceSerializer

+(DeviceSerializer *) getInstance
{
	if(singleton)
	{
		return singleton;
	}
	
	@synchronized([DeviceSerializer class])
	{
		if(singleton == nil)
		{
			singleton = [[DeviceSerializer alloc] init];
		}
	}
	
	return singleton;
}

+(void)stop
{
	@synchronized([DeviceSerializer class])
	{
		if(singleton != nil)
		{
			[singleton release];
			singleton = nil;
		}
	}
}

-(NSString *) serialize:(MobileObject *)mobileObject
{
	NSMutableString *buffer = [NSMutableString string];
	
	if(!mobileObject.createdOnDevice)
	{
		[buffer appendString:@"<mobileObject>\n"];
	}
	else 
	{
		[buffer appendString:@"<mobileObject createdOnDevice='true'>\n"];
	}
	
	[buffer appendFormat:@"<recordId>%@</recordId>\n",
	 [XMLUtil cleanupXML:mobileObject.recordId]];
	
	[buffer appendFormat:@"<serverRecordId>%@</serverRecordId>\n",
	 [XMLUtil cleanupXML:mobileObject.serverRecordId]];
	
	[buffer appendString:@"<object>\n"];
	
	//Serialize the Fields
	NSArray *fields = mobileObject.fields;
	if(fields != nil && [fields count] >0)
	{
		[buffer appendString:@"<fields>\n"];
		for(Field *local in fields)
		{
			[buffer appendString:@"<field>\n"];
			
			[buffer appendFormat:@"<uri>%@</uri>\n",local.uri];
			[buffer appendFormat:@"<name>%@</name>\n",local.name];
			[buffer appendFormat:@"<value>%@</value>\n",[XMLUtil cleanupXML:local.value]];
			
			[buffer appendString:@"</field>\n"];
		}
		[buffer appendString:@"</fields>\n"];
	}
	
	//Serialize the ArrayMetaData
	NSArray *arrayMetaData = mobileObject.arrayMetaData;
	if(arrayMetaData != nil && [arrayMetaData count] >0)
	{
		[buffer appendString:@"<metadata>\n"];
		for(ArrayMetaData *local in arrayMetaData)
		{
			[buffer appendString:@"<array-metadata>\n"];
			
			[buffer appendFormat:@"<uri>%@</uri>\n",local.arrayUri];
			[buffer appendFormat:@"<array-length>%@</array-length>\n",local.arrayLength];
			
			NSString *arrayClassName = local.arrayClass;
			if([StringUtil isEmpty:arrayClassName])
			{
				arrayClassName = @"";
			}
			[buffer appendFormat:@"<array-class>%@</array-class>\n",arrayClassName];
			
			[buffer appendString:@"</array-metadata>\n"];
		}
		[buffer appendString:@"</metadata>\n"];
	}
	
	[buffer appendString:@"</object>\n"];
    
	[buffer appendString:@"</mobileObject>\n"];
	return buffer;
}

-(MobileObject *)deserialize:(NSString *)xml
{
	MobileObjectReader *reader = [MobileObjectReader withInit];
	return [reader parse:xml];
}
@end
