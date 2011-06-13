/**
 * Appcelerator Titanium Mobile
 * Copyright (c) 2009-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 */

#import "SyncProxy.h"

#import "TiUtils.h"
#import "SBJsonWriter.h"
#import "TitaniumKernel.h"
#import "MobileBean.h"
#import "BeanList.h"

@implementation SyncProxy

+(SyncProxy *)withInit
{
	SyncProxy *instance = [[SyncProxy alloc] init];
	instance = [instance autorelease];
	
	return instance;
}

-(void) dealloc
{
	[super dealloc];
	if(newBean != nil)
	{
		[newBean release];
	}
}


-(id)ping:(id)input
{
	[TitaniumKernel startApp];
	
	NSMutableArray *oids = [NSMutableArray array];
	for(int i=0; i<10; i++)
	{
		NSString *local = [NSString stringWithFormat:@"oid://%d",i];
		[oids addObject:local];
	}
	
	SBJsonWriter *jsonWriter = [[[SBJsonWriter alloc] init] autorelease];
	NSString *payload = [jsonWriter stringWithObject:oids];
	
	return payload;
}

-(id) readAll:(id)input
{
	[TitaniumKernel startApp];
	
	//Validate the Channel
	NSString *channel = [(NSArray *)input objectAtIndex:0];
	if(channel == nil)
	{
		return nil;
	}
	
	//Read the beans stored in the channel
	NSArray *beans = [MobileBean readAll:channel];
	
	//Get the 'oids' of these beans
	NSMutableArray *oids = [NSMutableArray array];
	if(beans != nil && [beans count]>0)
	{
		for(MobileBean *bean in beans)
		{
			NSString *oid = [bean getId];
			[oids addObject:oid];
		}
	}
	
	//Generate a JSON payload of this array of 'oids'
	SBJsonWriter *jsonWriter = [[[SBJsonWriter alloc] init] autorelease];
	NSString *payload = [jsonWriter stringWithObject:oids];
	
	return payload;	
}

-(id) getValue:(id)input
{
	[TitaniumKernel startApp];
	
	NSArray *parameters = (NSArray *)input;
	
	NSString *channel = [parameters objectAtIndex:0];
	NSString *oid = [parameters objectAtIndex:1];
	NSString *fieldUri = [parameters objectAtIndex:2];
	
	//Validate the input
	if(channel == nil || oid == nil || fieldUri == nil)
	{
		return nil;
	}
	
	MobileBean *bean = [MobileBean readById:channel :oid];
	if(bean == nil)
	{
		//Not Found
		return nil;
	}
	
	NSString *value = [bean getValue:fieldUri];
	
	return value;
}

-(id) arrayLength:(id)input
{
	[TitaniumKernel startApp];
	
	NSArray *parameters = (NSArray *)input;
	
	NSString *channel = [parameters objectAtIndex:0];
	NSString *oid = [parameters objectAtIndex:1];
	NSString *arrayUri = [parameters objectAtIndex:2];
	
	//Validate the input
	if(channel == nil || oid == nil || arrayUri == nil)
	{
		return nil;
	}
	
	MobileBean *bean = [MobileBean readById:channel :oid];
	if(bean == nil)
	{
		//Not Found
		return nil;
	}
	
	BeanList *array = [bean readList:arrayUri];
	if(array == nil)
	{
		return nil;
	}
	
	int arraySize = [array size];
	
	return [NSNumber numberWithInt:arraySize];
}

-(id) deleteBean:(id) input
{
	[TitaniumKernel startApp];
	
	NSArray *parameters = (NSArray *)input;
	
	NSString *channel = [parameters objectAtIndex:0];
	NSString *oid = [parameters objectAtIndex:1];
	
	//Validate the input
	if(channel == nil || oid == nil)
	{
		return nil;
	}
	
	//Get the bean to be deleted
	MobileBean *bean = [MobileBean readById:channel :oid];
	if(bean == nil)
	{
		//Not Found
		return nil;
	}
	
	NSString *beanId = [bean getId];
	
	[bean delete];
	
	return beanId;
}

-(id) setValue:(id)input
{
	[TitaniumKernel startApp];
	
	NSArray *parameters = (NSArray *)input;
	
	NSString *channel = [parameters objectAtIndex:0];
	NSString *oid = [parameters objectAtIndex:1];
	NSString *fieldUri = [parameters objectAtIndex:2];
	NSString *value = [parameters objectAtIndex:3];
	
	//Validate the input
	if(channel == nil || oid == nil || fieldUri == nil || value == nil)
	{
		return nil;
	}
	
	//Get the bean to be updated
	MobileBean *bean = [MobileBean readById:channel :oid];
	if(bean == nil)
	{
		return nil;
	}
	
	[bean setValue:fieldUri :value];
	
	[bean save];
	
	return [bean getId];
}

-(id) newBean:(id)input
{
	[TitaniumKernel startApp];
	
	NSArray *parameters = (NSArray *)input;
	
	NSString *channel = [parameters objectAtIndex:0];
	
	//Validate the input
	if(channel == nil)
	{
		return nil;
	}
	
	newBean = [MobileBean newInstance:channel];
	if(newBean == nil)
	{
		return nil;
	}
	
	[newBean retain];
	
	return nil;
}

-(id) setNewBeanValue:(id)input
{
	[TitaniumKernel startApp];
	
	NSArray *parameters = (NSArray *)input;
	
	NSString *channel = [parameters objectAtIndex:0];
	NSString *fieldUri = [parameters objectAtIndex:1];
	NSString *value = [parameters objectAtIndex:2];
	
	//Validate the input
	if(channel == nil || fieldUri == nil || value == nil)
	{
		return nil;
	}
	
	//Get the bean to be updated
	if(newBean == nil)
	{
		return nil;
	}
	
	[newBean setValue:fieldUri :value];
	
	return nil;
}


-(id) saveNewBean:(id)input
{
	[TitaniumKernel startApp];
	
	if(newBean == nil)
	{
		return nil;
	}
	
	[newBean save];
	
	NSString *id = [newBean getId];
	
	//cleanup
	[newBean release];
	newBean = nil;
	
	return id;
}
@end
