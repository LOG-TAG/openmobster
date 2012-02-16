/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "Response.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation Response

-init
{
	if(self == [super init])
	{
		attrMgr = [GenericAttributeManager withInit];
	}
	return self;
}

+(id) withInit
{
	Response *response = [[[Response alloc] init] autorelease];
	return response;
}

-(void) setAttribute:(NSString *)name :(NSString *)value
{
	[attrMgr setAttribute:name :value];
}

-(NSString *) getAttribute:(NSString *)name
{
	return (NSString *)[attrMgr getAttribute:name];
}

-(NSArray *) getNames
{
	return [attrMgr getNames];
}

-(NSArray *) getValues
{
	return [attrMgr getValues];
}

-(void) removeAttribute:(NSString *)name
{
	[attrMgr removeAttribute:name];
}

-(NSString *)getStatusCode
{
	return [self getAttribute:@"status"];
}

-(void) setStatusCode:(NSString *)statusCode
{
	[self setAttribute:@"status" :statusCode];
}

-(NSString *)getStatusMsg
{
	return [self getAttribute:@"statusMsg"];
}

-(void) setStatusMsg:(NSString *)statusMsg
{
	[self setAttribute:@"statusMsg" :statusMsg];
}

-(void) setListAttribute:(NSString *)name :(NSArray *)list
{
	if(list != nil)
	{
		int size = [list count];
		NSString *sizeStr = [NSString stringWithFormat:@"%d",size];
		[self setAttribute:name :sizeStr];
		
		int i=0;
		for(NSString *local in list)
		{
			NSString *attrName = [NSString stringWithFormat:@"%@[%d%]",name,i];
			i++;
			[self setAttribute:attrName :local];
		}
	}
}

-(NSArray *) getListAttribute:(NSString *)name
{
	NSMutableArray *attribute = [NSMutableArray array];
	
	NSString *metadata = [self getAttribute:name];
	if(![StringUtil isEmpty:metadata])
	{
		int listSize = [metadata intValue];
		for(int i=0; i<listSize; i++)
		{
			NSString *local = [NSString stringWithFormat:@"%@[%d]",name,i];
			NSString *value = [self getAttribute:local];
			[attribute addObject:value];
		}
	}
	
	return [NSArray arrayWithArray:attribute];
}
@end
