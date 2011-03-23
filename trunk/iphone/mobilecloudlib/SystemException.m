//
//  SystemException.m
//  iphone
//
//  Created by openmobster on 7/16/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

#import "SystemException.h"


@implementation SystemException

-initWithContext:(NSString *)classNameIn method:(NSString *)methodIn parameters:(NSMutableArray *)parametersIn
{
	self = [self initWithName:@"SystemException" reason:@"SystemException" userInfo:nil];
	
	if(self)
	{
		className = classNameIn;
		method = methodIn;
		parameters = parametersIn;
	}
	
	return self;
}

+(id) withContext:(NSString *)classNameIn method:(NSString *)methodIn parameters:(NSMutableArray *)parametersIn
{
	SystemException* exception = 
	[[SystemException alloc] initWithContext:classNameIn method:methodIn parameters:parametersIn];
	
	return [exception autorelease];
}


-(NSString *) getMessage
{
	NSMutableString *message = [NSMutableString stringWithCapacity:0];
	
	NSString* classNameMessage = [NSString stringWithFormat:@"Class=%@\n",className];
	NSString* methodMessage = [NSString stringWithFormat:@"Method=%@\n",method];
	
	[message appendString: classNameMessage];
	[message appendString: methodMessage];
	
	if(parameters != nil && [parameters count] > 0)
	{
		for(int i=0,length=[parameters count]; i<length; i++)
		{
			NSString *cour = [parameters objectAtIndex: i]; 
			NSString *index = [[NSNumber numberWithInt:i] stringValue];
			
			NSString* paramMessage = [NSString stringWithFormat: @"Param(%@)=%@\n",index,cour];
			
			[message appendString: paramMessage];
		}
	}
	
	return message;
}

@end
