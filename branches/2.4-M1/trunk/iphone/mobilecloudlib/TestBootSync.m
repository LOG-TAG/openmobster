/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "TestBootSync.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation TestBootSync

+(id)withInit
{
	return [[[TestBootSync alloc] init] autorelease];
}

-(void) runTest
{
	[self setUp:@"add"];
	SyncService *sync = [SyncService getInstance];
	[sync performTwoWaySync:channel :NO];
	[self assertRecordPresence:@"unique-1" :@"/TestTwoWaySync/add"];
	[self assertRecordPresence:@"unique-2" :@"/TestTwoWaySync/add"];
	[self assertRecordPresence:@"unique-3" :@"/TestTwoWaySync/add"];
	[self assertRecordPresence:@"unique-4" :@"/TestTwoWaySync/add"];
	
	[sync performBootSync:channel :NO];
	[self assertRecordPresence:@"unique-1" :@"/TestBootSync/MustBeFound"];
	[self assertRecordPresence:@"unique-2" :@"/TestBootSync/MustBeFound"];
	[self assertRecordPresence:@"unique-3" :@"/TestBootSync/MustBeFound"];
	[self assertRecordPresence:@"unique-4" :@"/TestBootSync/MustBeFound"];
	[self assertTrue:!([self getRecord:@"unique-1"].proxy) :@"/TestBootSync/MustBeFullyLoaded"];
	[self assertTrue:([self getRecord:@"unique-2"].proxy) :@"/TestBootSync/MustBeProxy"];
	[self assertTrue:([self getRecord:@"unique-3"].proxy) :@"/TestBootSync/MustBeProxy"];
	[self assertTrue:([self getRecord:@"unique-4"].proxy) :@"/TestBootSync/MustBeProxy"];
}

-(NSString *) getInfo
{
	return [[self class] description];
}
@end
