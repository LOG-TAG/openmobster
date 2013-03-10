/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "CommandContext.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation CommandContext

@synthesize caller;

-init
{
	if(self == [super init])
	{
		attrMgr = [[GenericAttributeManager alloc] initWithRetention];
	}
	return self;
}

-(void)dealloc
{
	[attrMgr release];
	[super dealloc];
}

+(id)withInit:(UIViewController *)activeController;
{
	CommandContext *instance = [[CommandContext alloc] init];
	instance = [instance autorelease];
	
	instance.caller = activeController;
	
	return instance;
}

-(void)setAttribute:(NSString *)name :(id)value
{
	[attrMgr setAttribute:name :value];
}

-(id)getAttribute:(NSString *)name
{
	return [attrMgr getAttribute:name];
}

-(void)setTarget:(id)target
{
	[attrMgr setAttribute:@"target" :target];
}

-(id)getTarget
{
	return (NSString *)[attrMgr getAttribute:@"target"];
}

-(void)setError:(NSString *)code :(NSString *)message;
{
	[attrMgr setAttribute:@"error_code" :code];
	[attrMgr setAttribute:@"error_message" :message];
}

-(void)clearErrors
{
    [attrMgr removeAttribute:@"error_code"];
    [attrMgr removeAttribute:@"error_message"];
}

-(NSString *)getErrorCode
{
	return (NSString *)[attrMgr getAttribute:@"error_code"];
}

-(NSString *)getErrorMessage
{
	return (NSString *)[attrMgr getAttribute:@"error_message"];
}

-(BOOL)hasErrors
{
	return ([self getErrorCode] != nil);
}

-(void)activateTimeout
{
	[self setAttribute:@"activate_timeout" :@""];
}

-(void)deactivateTimeout
{
	[attrMgr removeAttribute:@"activate_timeout"];
}

-(BOOL)isTimeoutActivated
{
	return ([self getAttribute:@"activate_timeout"] != nil);
}

-(void)setAppException:(AppException *)appe
{
	[self setAttribute:@"app_exception" :appe];
}

-(AppException *)getAppException
{
	return (AppException *)[self getAttribute:@"app_exception"];
}
@end
