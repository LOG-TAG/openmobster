/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "TestReadOnlyChannel.h"
#import "AppService.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation TestReadOnlyChannel

+(id)withInit
{
	return [[[TestReadOnlyChannel alloc] init] autorelease];
}

-(NSString *) getInfo
{
	NSString *info = [NSString stringWithFormat:@"%@%@",[[self class] description],@"TwoWaySync"];
	return info;
}

-(void) runTest
{
	//Make the channel readonly
	AppService *service = [AppService getInstance];
	[service.writableChannels removeAllObjects];
	
	[self testReplace];
	[self testAdd];
	[self testDelete];
	
	[self tearDown];
}

-(void)testReplace
{
	SyncService *sync = [SyncService getInstance];
	
	[self setUp:@"add"];
	[sync performTwoWaySync:channel :NO];
	[self assertRecordPresence:@"unique-1" :@"/TestSimpleAccess/add"];
	[self assertRecordPresence:@"unique-2" :@"/TestSimpleAccess/add"];
	[self assertRecordPresence:@"unique-3" :@"/TestSimpleAccess/add"];
	[self assertRecordPresence:@"unique-4" :@"/TestSimpleAccess/add"];
	
	NSArray *beans = [MobileBean readAll:channel];
	
	for(MobileBean *local in beans)
	{
		MobileBean *oldInstance = [MobileBean readById:channel :[local getId]];
		
		[local setValue:@"from" :@"From://Updated"];
		BOOL readonlyTested = NO;
		@try
		{
			[local save];
		}
		@catch (SystemException *syse) 
		{
			readonlyTested = YES;
		}
		[self assertTrue:readonlyTested :@"/ReadOnlyTest/Save"];
		
		
		
		@try
		{
			[oldInstance save];
		}
		@catch (SystemException *syse) 
		{
			readonlyTested = YES;
		}
		[self assertTrue:readonlyTested :@"/ReadOnlyTest/Save"];
	}
	
	
	[self tearDown];
}

-(void)testAdd
{
	SyncService *sync = [SyncService getInstance];
	
	[self setUp:@"add"];
	[sync performTwoWaySync:channel :NO];
	[self assertRecordPresence:@"unique-1" :@"/TestSimpleAccess/add"];
	[self assertRecordPresence:@"unique-2" :@"/TestSimpleAccess/add"];
	[self assertRecordPresence:@"unique-3" :@"/TestSimpleAccess/add"];
	[self assertRecordPresence:@"unique-4" :@"/TestSimpleAccess/add"];
	
	MobileBean *newInstance = [MobileBean newInstance:channel];
	[newInstance setValue:@"from" :@"/new/from"];
	[newInstance setValue:@"to" :@"/new/to"];
	NSString *oid = [newInstance getId];
	[self assertTrue:oid == nil :@"/TestCRUD/testAdd/oid_check"];
	[self assertTrue:[newInstance isInitialized] :@"/TestCRUD/testAdd/init_check"];
	[self assertTrue:[newInstance isCreatedOnDevice] :@"/TestCRUD/testAdd/created_on_device"];
	[self assertTrue:[newInstance getCloudId] == nil :@"/TestCRUD/testAdd/serverid_check"];
	
	BOOL readonlyTested = NO;
	@try
	{
		[newInstance save];
	}
	@catch (SystemException *syse) 
	{
		readonlyTested = YES;
	}
	[self assertTrue:readonlyTested :@"/ReadOnlyTest/NewInstance"];
	
	[self tearDown];
}

-(void)testDelete
{
	SyncService *sync = [SyncService getInstance];
	
	[self setUp:@"add"];
	[sync performTwoWaySync:channel :NO];
	[self assertRecordPresence:@"unique-1" :@"/TestSimpleAccess/add"];
	[self assertRecordPresence:@"unique-2" :@"/TestSimpleAccess/add"];
	[self assertRecordPresence:@"unique-3" :@"/TestSimpleAccess/add"];
	[self assertRecordPresence:@"unique-4" :@"/TestSimpleAccess/add"];
	
	MobileBean *unique1 = [MobileBean readById:channel :@"unique-1"];
	
	BOOL readonlyTested = NO;
	@try
	{
		[unique1 delete];
	}
	@catch (SystemException *syse) 
	{
		readonlyTested = YES;
	}
	[self assertTrue:readonlyTested :@"/ReadOnlyTest/DeleteInstance"];
	
	[self tearDown];
}

@end
