/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "TestCRUD.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation TestCRUD

+(id)withInit
{
	return [[[TestCRUD alloc] init] autorelease];
}

-(void) runTest
{
	//Delete
	[self testDelete];
	
	//Add
	[self testAdd];
	
	//Replace
	[self testReplace];
}

-(NSString *) getInfo
{
	NSString *info = [NSString stringWithFormat:@"%@%@",[[self class] description],@"TwoWaySync"];
	return info;
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
	[unique1 delete];
	
	//Sync with the Cloud
	[sync performTwoWaySync:channel :NO];
	
	
	//unique-1 should not be found
	NSArray *beans = [MobileBean readAll:channel];
	int size = [beans count];
	NSLog(@"Size: %d",size);
	unique1 = [MobileBean readById:channel :@"unique-1"];
	
	[self assertTrue:(size == 3) :@"TestCRUD/delete/size_check"];
	[self assertTrue:(unique1 == nil) :@"TestCRUD/delete/instance_check"];
	
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
	
	[newInstance save];
	oid = [newInstance getId];
	
	[self assertTrue:oid != nil :@"/TestCRUD/testAdd/oid_check"];
	[self assertTrue:[newInstance isInitialized] :@"/TestCRUD/testAdd/init_check"];
	[self assertTrue:[newInstance isCreatedOnDevice] :@"/TestCRUD/testAdd/created_on_device"];
	[self assertTrue:[newInstance getCloudId] == nil :@"/TestCRUD/testAdd/serverid_check"];
	
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
		[local save];
		
		NSString *oldFrom = [oldInstance getValue:@"from"];
		NSString *newFrom = [local getValue:@"from"];
		
		[self assertTrue:![oldFrom isEqualToString:newFrom] :@"/TestDRUD/testReplace/old_new_check"];
		[self assertTrue:[newFrom isEqualToString:@"From://Updated"] :@"/TestDRUD/testReplace/new_check"];
	}
	
	//Test Optimistic Locking
	MobileBean *instance1 = [MobileBean readById:channel :@"unique-1"];
	MobileBean *instance2 = [MobileBean readById:channel :@"unique-1"];
	
	NSString *newValueInstance1 = @"/instance1/from/Updated";
	[instance1 setValue:@"from" :newValueInstance1];
	[instance1 save];
	
	@try
	{
		NSString *newValueInstance2 = @"/instance2/from/Updated";
		[instance2 setValue:@"from" :newValueInstance2];
		[instance2 save];
	}
	@catch (NSException *e)
	{
		[instance2 refresh];
		
		NSString *from = [instance2 getValue:@"from"];
		NSLog(@"From: %@",from);
		
		[self assertTrue:[from isEqualToString:@"/instance1/from/Updated"] :@"/TestDRUD/testReplace/lock_check"];
	}
	
	
	[self tearDown];
}
@end
