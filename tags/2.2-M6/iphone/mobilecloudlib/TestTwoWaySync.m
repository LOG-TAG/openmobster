/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "TestTwoWaySync.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation TestTwoWaySync

+(id)withInit
{
	return [[[TestTwoWaySync alloc] init] autorelease];
}

-(void) runTest
{
	SyncService *sync = [SyncService getInstance];
	
	//Add testcase
	[self setUp:@"add"];
	[sync performTwoWaySync:channel :NO];
	[self assertRecordPresence:@"unique-1" :@"/TestTwoWaySync/add"];
	[self assertRecordPresence:@"unique-2" :@"/TestTwoWaySync/add"];
	[self assertRecordPresence:@"unique-3" :@"/TestTwoWaySync/add"];
	[self assertRecordPresence:@"unique-4" :@"/TestTwoWaySync/add"];
	[self tearDown];
	
	//Replace testcase
	[self setUp:@"replace"];
	[sync performTwoWaySync:channel :NO];
	MobileObject *afterUnique1 = [self getRecord:@"unique-1"];
	MobileObject *afterUnique2 = [self getRecord:@"unique-2"];
	[self assertRecordPresence:@"unique-1" :@"/TestTwoWaySync/replace"];
	[self assertRecordPresence:@"unique-2" :@"/TestTwoWaySync/replace"];
	NSString *compareMessage1 = @"<tag apos='apos' quote=\"quote\" ampersand='&'>unique-1/Updated/Server</tag>";
	NSString *compareMessage2 = @"<tag apos='apos' quote=\"quote\" ampersand='&'>unique-2/Updated/Client</tag>";
	NSString *afterUnique1Msg = [afterUnique1 getValue:@"message"];
	NSString *afterUnique2Msg = [afterUnique2 getValue:@"message"];
	NSLog(@"%@",afterUnique1Msg);
	NSLog(@"%@",afterUnique2Msg);
	[self assertTrue:[afterUnique1Msg isEqualToString:compareMessage1] :@"/TestTwoWaySync/replace/updated/unique-1"];
	[self assertTrue:[afterUnique2Msg isEqualToString:compareMessage2] :@"/TestTwoWaySync/replace/updated/unique-2"];
	[self tearDown];
	
	//Delete test case
	[self setUp:@"delete"];
	[sync performTwoWaySync:channel :NO];
	[self assertRecordAbsence:@"unique-1" :@"/TestTwoWaySync/delete"];
	[self assertRecordAbsence:@"unique-2" :@"/TestTwoWaySync/delete"];
	[self tearDown];
	
	//Conflict test case
	[self setUp:@"conflict"];
	[sync performTwoWaySync:channel :NO];
	afterUnique1 = [self getRecord:@"unique-1"];
	afterUnique2 = [self getRecord:@"unique-2"];
	[self assertRecordPresence:@"unique-1" :@"/TestTwoWaySync/conflict"];
	[self assertRecordPresence:@"unique-2" :@"/TestTwoWaySync/conflict"];
	compareMessage1 = @"<tag apos='apos' quote=\"quote\" ampersand='&'>unique-1/Updated/Client</tag>";
	compareMessage2 = @"<tag apos='apos' quote=\"quote\" ampersand='&'>unique-2/Message</tag>";
	afterUnique1Msg = [afterUnique1 getValue:@"message"];
	afterUnique2Msg = [afterUnique2 getValue:@"message"];
	NSLog(@"%@",afterUnique1Msg);
	NSLog(@"%@",afterUnique2Msg);
	[self assertTrue:[afterUnique1Msg isEqualToString:compareMessage1] :@"/TestTwoWaySync/conflict/updated/unique-1"];
	[self assertTrue:[afterUnique2Msg isEqualToString:compareMessage2] :@"/TestTwoWaySync/conflict/updated/unique-2"];
	[self tearDown];
}

-(NSString *) getInfo
{
	return [[self class] description];
}

@end
