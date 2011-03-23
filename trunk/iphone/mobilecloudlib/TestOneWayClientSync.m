/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "TestOneWayClientSync.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation TestOneWayClientSync

+(id)withInit
{
	return [[[TestOneWayClientSync alloc] init] autorelease];
}

-(void) runTest
{
	SyncService *sync = [SyncService getInstance];
	
	//Add usecase
	[self setUp:@"add"];
	[sync performOneWayClientSync:channel :NO];
	[self assertRecordAbsence:@"unique-1" :@"/TestOneWayClientSync/add"];
	[self assertRecordAbsence:@"unique-2" :@"/TestOneWayClientSync/add"];
	[self assertRecordPresence:@"unique-3" :@"/TestOneWayClientSync/add"];
	[self assertRecordPresence:@"unique-4" :@"/TestOneWayClientSync/add"];
	[self tearDown];
	
	//Replace usecase
	[self setUp:@"replace"];
	[sync performOneWayClientSync:channel :NO];
	MobileObject *afterUnique1 = [self getRecord:@"unique-1"];
	MobileObject *afterUnique2 = [self getRecord:@"unique-2"];
	[self assertRecordPresence:@"unique-1" :@"/TestOneWayClientSync/replace"];
	[self assertRecordPresence:@"unique-2" :@"/TestOneWayClientSync/replace"];
	NSString *compareMessage1 = @"<tag apos='apos' quote=\"quote\" ampersand='&'>unique-1/Message</tag>";
	NSString *compareMessage2 = @"<tag apos='apos' quote=\"quote\" ampersand='&'>unique-2/Updated/Client</tag>";
	NSString *afterUnique1Msg = [afterUnique1 getValue:@"message"];
	NSString *afterUnique2Msg = [afterUnique2 getValue:@"message"];
	NSLog(@"%@",afterUnique1Msg);
	NSLog(@"%@",afterUnique2Msg);
	[self assertTrue:[afterUnique1Msg isEqualToString:compareMessage1] :@"/TestOneWayClientSync/replace/updated/unique-1"];
	[self assertTrue:[afterUnique2Msg isEqualToString:compareMessage2] :@"/TestOneWayClientSync/replace/updated/unique-2"];
	[self tearDown];
	
	//Delete usecase
	[self setUp:@"delete"];
	[sync performOneWayClientSync:channel :NO];
	[self assertRecordPresence:@"unique-1" :@"/TestOneWayClientSync/delete"];
	[self assertRecordAbsence:@"unique-2" :@"/TestOneWayClientSync/delete"];
	[self tearDown];
	
	//Conflict usecase
	[self setUp:@"conflict"];
	[sync performOneWayClientSync:channel :NO];
	afterUnique1 = [self getRecord:@"unique-1"];
	afterUnique2 = [self getRecord:@"unique-2"];
	[self assertRecordPresence:@"unique-1" :@"/TestOneWayClientSync/conflict"];
	[self assertRecordPresence:@"unique-2" :@"/TestOneWayClientSync/conflict"];
	compareMessage1 = @"<tag apos='apos' quote=\"quote\" ampersand='&'>unique-1/Updated/Client</tag>";
	compareMessage2 = @"<tag apos='apos' quote=\"quote\" ampersand='&'>unique-2/Message</tag>";
	afterUnique1Msg = [afterUnique1 getValue:@"message"];
	afterUnique2Msg = [afterUnique2 getValue:@"message"];
	NSLog(@"%@",afterUnique1Msg);
	NSLog(@"%@",afterUnique2Msg);
	[self assertTrue:[afterUnique1Msg isEqualToString:compareMessage1] :@"/TestOneWayClientSync/conflict/updated/unique-1"];
	[self assertTrue:[afterUnique2Msg isEqualToString:compareMessage2] :@"/TestOneWayClientSync/conflict/updated/unique-2"];
	[self tearDown];
}

-(NSString *) getInfo
{
	return [[self class] description];
}


@end
