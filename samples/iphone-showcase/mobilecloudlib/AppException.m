/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "AppException.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation AppException

-init
{
	self = [self initWithName:@"AppException" reason:@"AppException" userInfo:nil];
	if(self)
	{
		attrMgr = [[GenericAttributeManager alloc] initWithRetention];
	}
	return self;
}

+(id)withInit:(NSString *)type :(NSString *) message
{
	AppException *instance = [[AppException alloc] init];
	instance = [instance autorelease];
	
	if(type != nil)
	{
		[instance setType:type];
	}
	
	if(message != nil)
	{
		[instance setMessage:message];
	}
	
	return instance;
}

-(void)dealloc
{
	[attrMgr release];
	[super dealloc];
}

-(void)setAttribute:(NSString *)name :(id)value
{
	[attrMgr setAttribute:name :value];
}

-(id)getAttribute:(NSString *)name
{
	return [attrMgr getAttribute:name];
}

-(void)setType:(NSString *)type
{
	[self setAttribute:@"type" :type];
}

-(NSString *)getType
{
	return [self getAttribute:@"type"];
}

-(void)setMessage:(NSString *)message
{
	[self setAttribute:@"message" :message];
}

-(NSString *)getMessage
{
	return [self getAttribute:@"message"];
}
@end
