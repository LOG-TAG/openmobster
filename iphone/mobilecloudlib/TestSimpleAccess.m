/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "TestSimpleAccess.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation TestSimpleAccess

+(id)withInit
{
	return [[[TestSimpleAccess alloc] init] autorelease];
}

-(void) runTest
{
	SyncService *sync = [SyncService getInstance];
	
	//Add testcase
	[self setUp:@"add"];
	[sync performTwoWaySync:channel :NO];
	[self assertRecordPresence:@"unique-1" :@"/TestSimpleAccess/add"];
	[self assertRecordPresence:@"unique-2" :@"/TestSimpleAccess/add"];
	[self assertRecordPresence:@"unique-3" :@"/TestSimpleAccess/add"];
	[self assertRecordPresence:@"unique-4" :@"/TestSimpleAccess/add"];
	
	//Test MobileBean interface on the 'unique-4' bean downloaded from the Cloud
	NSArray *beans = [MobileBean readAll:channel];
	for(MobileBean *bean in beans)
	{
		NSString *beanChannel = [bean getChannel];
		NSString *oid = [bean getId];
	
	
		NSLog(@"****************************************************");
		NSLog(@"Channel: %@",beanChannel);
		NSLog(@"OID: %@",oid);
		NSLog(@"From: %@",[bean getValue:@"from"]);
		NSLog(@"To: %@",[bean getValue:@"to"]);
		NSLog(@"Subject: %@",[bean getValue:@"subject"]);
		NSLog(@"Message: %@",[bean getValue:@"message"]);
		NSLog(@"****************************************************");
	}
	
	[self tearDown];
}

-(NSString *) getInfo
{
	NSString *info = [NSString stringWithFormat:@"%@%@",[[self class] description],@"TwoWaySync"];
	return info;
}
@end
