/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "Request.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation Request

@synthesize service;

-init
{
	if(self == [super init])
	{
		attrMgr = [GenericAttributeManager withInit];
	}
	return self;
}

+(id) withInit:(NSString *)service
{
	Request *request = [[[Request alloc] init] autorelease];
	
	request.service = service;
	
	return request;
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
