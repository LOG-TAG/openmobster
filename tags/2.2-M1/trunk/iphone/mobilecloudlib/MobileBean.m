/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "MobileBean.h"
#import "AppService.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation MobileBean

@synthesize data;
@synthesize isNew;
@synthesize isDirty;
@synthesize readonly;

+(id)withInit:(MobileObject *)data
{
	MobileBean *instance = [[[MobileBean alloc] init] autorelease];
	
	instance.data = data;
	instance.isNew = NO;
	instance.isDirty = NO;
	
	AppService *service = [AppService getInstance];
	NSString *channel = data.service;
	if(![service isWritable:channel])
	{
		instance.readonly = YES;
	}
	else 
	{
		instance.readonly = NO;
	}

	
	return instance;
}

+(MobileBean *)newInstance:(NSString *)channel
{
	MobileBean *instance = [[[MobileBean alloc] init] autorelease];
	
	MobileObject *data = [MobileObject withInit];
	data.createdOnDevice = YES;
	data.service = channel;
	
	AppService *service = [AppService getInstance];
	if(![service isWritable:channel])
	{
		instance.readonly = YES;
	}
	else 
	{
		instance.readonly = NO;
	}
	
	instance.data = data;
	instance.isNew = YES;
	
	return instance;
}

+(MobileBean *)readById:(NSString *)channel :(NSString *)oid
{
	MobileObjectDatabase *mdb = [MobileObjectDatabase getInstance];
	MobileObject *data = [mdb read:channel :oid];
	
	if(data != nil && !data.proxy)
	{
		MobileBean *bean = [MobileBean withInit:data];
		return bean;
	}
	
	return nil;
}

+(NSArray *)readAll:(NSString *)channel
{
	MobileObjectDatabase *mdb = [MobileObjectDatabase getInstance];
	NSArray *allObjects = [mdb readAll:channel];
	if(allObjects != nil && [allObjects count]>0)
	{
		//Filter out the proxy objects
		allObjects = [MobileBean filterProxies:allObjects];
		int size = [allObjects count];
		NSMutableArray *all = [NSMutableArray array];
		for(int i=0; i<size; i++)
		{
			MobileObject *local = (MobileObject *)[allObjects objectAtIndex:i];
			MobileBean *localBean = [MobileBean withInit:local];
			[all addObject:localBean];
		}
		
		return [NSArray arrayWithArray:all];
	}
	
	return nil;
}

+(NSArray *)filterProxies:(NSArray *)mobileObjects
{
	NSMutableArray *filtered = [NSMutableArray array];
	
	if(mobileObjects != nil)
	{
		for(MobileObject *local in mobileObjects)
		{
			if(!local.proxy)
			{
				[filtered addObject:local];
			}
		}
	}
	
	return [NSArray arrayWithArray:filtered];
}

+(BOOL)isBooted:(NSString *)channel
{
	MobileObjectDatabase *mdb = [MobileObjectDatabase getInstance];
	NSArray *allObjects = [mdb readAll:channel];
	
	return (allObjects != nil && [allObjects count]>0);
}


-(NSString *)getChannel
{
	if(![self isInitialized])
	{
		NSMutableArray *params = [NSMutableArray arrayWithObjects:@"MobileBean is uninitialized",nil];
		SystemException *ex = [SystemException withContext:@"MobileBean" method:@"getChannel" parameters:params];
		@throw ex;
	}
	
	NSString *channel = data.service;
	
	return [StringUtil trim:channel];
}

-(NSString *)getId
{
	if(![self isInitialized])
	{
		NSMutableArray *params = [NSMutableArray arrayWithObjects:@"MobileBean is uninitialized",nil];
		SystemException *ex = [SystemException withContext:@"MobileBean" method:@"getId" parameters:params];
		@throw ex;
	}
	
	NSString *oid = data.recordId;
	if(oid != nil)
	{
		oid = [StringUtil trim:oid];
	}
	
	return oid;
}

-(BOOL)isInitialized
{
	return (data != nil);
}

-(NSString *)getCloudId
{
	if(![self isInitialized])
	{
		NSMutableArray *params = [NSMutableArray arrayWithObjects:@"MobileBean is uninitialized",nil];
		SystemException *ex = [SystemException withContext:@"MobileBean" method:@"getCloudId" parameters:params];
		@throw ex;
	}
	
	NSString *oid = data.serverRecordId;
	if(oid != nil)
	{
		oid = [StringUtil trim:oid];
	}
	
	return oid;
}

-(BOOL)isCreatedOnDevice
{
	return data.createdOnDevice;
}

-(BOOL)isProxy
{
	if(![self isInitialized])
	{
		NSMutableArray *params = [NSMutableArray arrayWithObjects:@"MobileBean is uninitialized",nil];
		SystemException *ex = [SystemException withContext:@"MobileBean" method:@"isProxy" parameters:params];
		@throw ex;
	}
	return data.proxy;
}

-(NSString *)getValue:(NSString *)fieldUri
{
	if(![self isInitialized])
	{
		NSMutableArray *params = [NSMutableArray arrayWithObjects:@"MobileBean is uninitialized",nil];
		SystemException *ex = [SystemException withContext:@"MobileBean" method:@"getValue" parameters:params];
		@throw ex;
	}
	
	if(data.proxy)
	{
		NSMutableArray *params = [NSMutableArray arrayWithObjects:@"MobileBean is in proxy state",nil];
		SystemException *ex = [SystemException withContext:@"MobileBean" method:@"getValue" parameters:params];
		@throw ex;
	}
	
	NSString *value = [data getValue:fieldUri];
	return [StringUtil trim:value];
}

-(void)setValue:(NSString *)fieldUri :(NSString *)value
{
	if(![self isInitialized])
	{
		NSMutableArray *params = [NSMutableArray arrayWithObjects:@"MobileBean is uninitialized",nil];
		SystemException *ex = [SystemException withContext:@"MobileBean" method:@"setValue" parameters:params];
		@throw ex;
	}
	
	if(data.proxy)
	{
		NSMutableArray *params = [NSMutableArray arrayWithObjects:@"MobileBean is in proxy state",nil];
		SystemException *ex = [SystemException withContext:@"MobileBean" method:@"setValue" parameters:params];
		@throw ex;
	}
	
	[data setValue:fieldUri value:value];
	isDirty = YES;
}

-(BeanList *)readList:(NSString *)listProperty
{
	BeanList *beanList = [BeanList withInit:listProperty];
	
	if(![self isInitialized])
	{
		NSMutableArray *params = [NSMutableArray arrayWithObjects:@"MobileBean is uninitialized",nil];
		SystemException *ex = [SystemException withContext:@"MobileBean" method:@"readList" parameters:params];
		@throw ex;
	}
	
	if(data.proxy)
	{
		NSMutableArray *params = [NSMutableArray arrayWithObjects:@"MobileBean is in proxy state",nil];
		SystemException *ex = [SystemException withContext:@"MobileBean" method:@"readList" parameters:params];
		@throw ex;
	}
	
	int arrayLength = [data getArrayLength:listProperty];
	for(int i=0; i<arrayLength; i++)
	{
		NSDictionary *arrayElement = [data getArrayElement:listProperty :i];
		
		BeanListEntry *entry = [BeanListEntry withInit:i :arrayElement :listProperty];
		
		[beanList addEntry:entry];
	}
	
	return beanList;
}

-(void)saveList:(BeanList *)list
{
	if(![self isInitialized])
	{
		NSMutableArray *params = [NSMutableArray arrayWithObjects:@"MobileBean is uninitialized",nil];
		SystemException *ex = [SystemException withContext:@"MobileBean" method:@"saveList" parameters:params];
		@throw ex;
	}
	
	if(data.proxy)
	{
		NSMutableArray *params = [NSMutableArray arrayWithObjects:@"MobileBean is in proxy state",nil];
		SystemException *ex = [SystemException withContext:@"MobileBean" method:@"saveList" parameters:params];
		@throw ex;
	}
	
	NSString *listProperty = list.listProperty;
	[data clearArray:listProperty];
	
	int arrayLength = [list size];
	for(int i=0; i<arrayLength; i++)
	{
		BeanListEntry *local = [list entryAt:i];
		NSDictionary *properties = [local getProperties];
		[data addToArray:listProperty :properties]; 
	}
	
	isDirty = YES;
}

-(void)clearList:(NSString *)listProperty
{
	if(![self isInitialized])
	{
		NSMutableArray *params = [NSMutableArray arrayWithObjects:@"MobileBean is uninitialized",nil];
		SystemException *ex = [SystemException withContext:@"MobileBean" method:@"clearList" parameters:params];
		@throw ex;
	}
	
	if(data.proxy)
	{
		NSMutableArray *params = [NSMutableArray arrayWithObjects:@"MobileBean is in proxy state",nil];
		SystemException *ex = [SystemException withContext:@"MobileBean" method:@"clearList" parameters:params];
		@throw ex;
	}
	
	[data clearArray:listProperty];
	isDirty = YES;
}

-(void)addBean:(NSString *)listProperty :(BeanListEntry *)bean
{
	if(![self isInitialized])
	{
		NSMutableArray *params = [NSMutableArray arrayWithObjects:@"MobileBean is uninitialized",nil];
		SystemException *ex = [SystemException withContext:@"MobileBean" method:@"addBean" parameters:params];
		@throw ex;
	}
	
	if(data.proxy)
	{
		NSMutableArray *params = [NSMutableArray arrayWithObjects:@"MobileBean is in proxy state",nil];
		SystemException *ex = [SystemException withContext:@"MobileBean" method:@"addBean" parameters:params];
		@throw ex;
	}
	
	[data addToArray:listProperty :[bean getProperties]];
	isDirty = YES;
}

-(void)removeBean:(NSString *)listProperty :(int) elementAt
{
	if(![self isInitialized])
	{
		NSMutableArray *params = [NSMutableArray arrayWithObjects:@"MobileBean is uninitialized",nil];
		SystemException *ex = [SystemException withContext:@"MobileBean" method:@"removeBean" parameters:params];
		@throw ex;
	}
	
	if(data.proxy)
	{
		NSMutableArray *params = [NSMutableArray arrayWithObjects:@"MobileBean is in proxy state",nil];
		SystemException *ex = [SystemException withContext:@"MobileBean" method:@"removeBean" parameters:params];
		@throw ex;
	}
	
	[data removeArrayElement:listProperty :elementAt];
	isDirty = YES;
}

-(void)clearAll
{
	@synchronized([MobileBean class])
	{
		data = nil;
		isDirty = NO;
		isNew = NO;
	}
}

-(void)clearMetaData
{
	@synchronized([MobileBean class])
	{
		isDirty = NO;
		isNew = NO;
	}
}

-(void)delete
{
	@synchronized([MobileBean class])
	{
		if(isNew)
		{
			NSMutableArray *params = [NSMutableArray arrayWithObjects:@"Instance is transient",nil];
			SystemException *ex = [SystemException withContext:@"MobileBean" method:@"delete" parameters:params];
			@throw ex;
		}
		
		if(readonly)
		{
			NSMutableArray *params = [NSMutableArray arrayWithObjects:@"Channel is ReadOnly",nil];
			SystemException *ex = [SystemException withContext:@"MobileBean" method:@"delete" parameters:params];
			@throw ex;
		}
	
		MobileObjectDatabase *mdb = [MobileObjectDatabase getInstance];
		NSString *channel = [self getChannel];
		NSString *oid = [self getId];
	
		[mdb delete:data];
	
		[self clearAll];
		
		//Sync integration
		@try 
		{
			SyncService *sync = [SyncService getInstance];
			[sync updateChangeLog:channel :_Delete :oid];
		}
		@catch (NSException * e) 
		{
			[ErrorHandler handleException:e];	
		}
	}
}

-(void)save
{
	@synchronized([MobileBean class])
	{
		if(readonly)
		{
			NSMutableArray *params = [NSMutableArray arrayWithObjects:@"Channel is ReadOnly",nil];
			SystemException *ex = [SystemException withContext:@"MobileBean" method:@"save" parameters:params];
			@throw ex;
		}
		
		MobileObjectDatabase *deviceDB = [MobileObjectDatabase getInstance];
		
		//bean created on the device
		if(isNew)
		{
			NSString *newOid = [deviceDB create:data];
			NSString *channel = data.service;
			data = [deviceDB read:channel : newOid];
			
			isNew = NO;
			
			[self refresh];
			
			//Sync integration
			@try 
			{
				SyncService *sync = [SyncService getInstance];
				[sync updateChangeLog:channel :_Add :[self getId]];
			}
			@catch (NSException * e) 
			{
				[ErrorHandler handleException:e];	
			}
			
			return;
		}
		
		//bean modified on the device
		if(isDirty)
		{
			[deviceDB update:data];
			[self clearMetaData];
			
			//Sync integration
			@try 
			{
				SyncService *sync = [SyncService getInstance];
				[sync updateChangeLog:[self getChannel] :_Replace :[self getId]];
			}
			@catch (NSException * e) 
			{
				[ErrorHandler handleException:e];	
			}
		}
	}
}

-(void)refresh
{
	if(isNew)
	{
		NSMutableArray *params = [NSMutableArray arrayWithObjects:@"Instance is transient",nil];
		SystemException *ex = [SystemException withContext:@"MobileBean" method:@"refresh" parameters:params];
		@throw ex;
	}
	
	MobileObjectDatabase *deviceDB = [MobileObjectDatabase getInstance];
	
	data = [deviceDB read:[self getChannel] :[self getId]];
	
	[self clearMetaData];
}
@end
