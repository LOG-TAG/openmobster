/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "TestOneWayServerSync.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation TestOneWayServerSync

+(id)withInit
{
	return [[[TestOneWayServerSync alloc] init] autorelease];
}

-(void) runTest
{
	SyncService *sync = [SyncService getInstance];
	
	//Add usecase
	[self setUp:@"add"];
	[sync performOneWayServerSync:channel :NO];
	[self assertRecordPresence:@"unique-1" :@"/TestOneWayServerSync/add"];
	[self assertRecordPresence:@"unique-2" :@"/TestOneWayServerSync/add"];
	[self assertRecordPresence:@"unique-3" :@"/TestOneWayServerSync/add"];
	[self assertRecordPresence:@"unique-4" :@"/TestOneWayServerSync/add"];
	[self tearDown];
	
	//Replace usecase
	[self setUp:@"replace"];
	[sync performOneWayServerSync:channel :NO];
	MobileObject *afterUnique1 = [self getRecord:@"unique-1"];
	MobileObject *afterUnique2 = [self getRecord:@"unique-2"];
	[self assertRecordPresence:@"unique-1" :@"/TestOneWayServerSync/replace"];
	[self assertRecordPresence:@"unique-2" :@"/TestOneWayServerSync/replace"];
	NSString *compareMessage1 = @"<tag apos='apos' quote=\"quote\" ampersand='&'>unique-1/Updated/Server</tag>";
	NSString *compareMessage2 = @"<tag apos='apos' quote=\"quote\" ampersand='&'>unique-2/Updated/Client</tag>";
	NSString *afterUnique1Msg = [afterUnique1 getValue:@"message"];
	NSString *afterUnique2Msg = [afterUnique2 getValue:@"message"];
	NSLog(@"%@",afterUnique1Msg);
	NSLog(@"%@",afterUnique2Msg);
	[self assertTrue:[afterUnique1Msg isEqualToString:compareMessage1] :@"/TestOneWayServerSync/replace/updated/unique-1"];
	[self assertTrue:[afterUnique2Msg isEqualToString:compareMessage2] :@"/TestOneWayServerSync/replace/updated/unique-2"];	
	[self tearDown];
	
	//Delete usecase
	[self setUp:@"delete"];
	[sync performOneWayServerSync:channel :NO];
	[self assertRecordAbsence:@"unique-1" :@"/TestOneWayServerSync/delete"];
	[self assertRecordAbsence:@"unique-2" :@"/TestOneWayServerSync/delete"];
	[self tearDown];
	
	//Conflict usecase
	[self setUp:@"conflict"];
	[sync performOneWayServerSync:channel :NO];
	afterUnique1 = [self getRecord:@"unique-1"];
	afterUnique2 = [self getRecord:@"unique-2"];
	[self assertRecordPresence:@"unique-1" :@"/TestOneWayServerSync/conflict"];
	[self assertRecordPresence:@"unique-2" :@"/TestOneWayServerSync/conflict"];
	compareMessage1 = @"<tag apos='apos' quote=\"quote\" ampersand='&'>unique-1/Updated/Server</tag>";
	compareMessage2 = @"<tag apos='apos' quote=\"quote\" ampersand='&'>unique-2/Message</tag>";
	afterUnique1Msg = [afterUnique1 getValue:@"message"];
	afterUnique2Msg = [afterUnique2 getValue:@"message"];
	NSLog(@"%@",afterUnique1Msg);
	NSLog(@"%@",afterUnique2Msg);
	[self assertTrue:[afterUnique1Msg isEqualToString:compareMessage1] :@"/TestOneWayServerSync/conflict/updated/unique-1"];
	[self assertTrue:[afterUnique2Msg isEqualToString:compareMessage2] :@"/TestOneWayServerSync/conflict/updated/unique-2"];
	[self tearDown];
}

-(NSString *) getInfo
{
	return [[self class] description];
}


@end
