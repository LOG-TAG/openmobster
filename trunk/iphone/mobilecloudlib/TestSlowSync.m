/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "TestSlowSync.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation TestSlowSync

+(id)withInit
{
	return [[[TestSlowSync alloc] init] autorelease];
}

-(void) runTest
{
	SyncService *sync = [SyncService getInstance];
	
	//Add testcase
	[self setUp:@"add"];
	[sync performSlowSync:channel :NO];
	[self assertRecordPresence:@"unique-1" :@"/TestSlowSync/add"];
	[self assertRecordPresence:@"unique-2" :@"/TestSlowSync/add"];
	[self assertRecordPresence:@"unique-3" :@"/TestSlowSync/add"];
	[self assertRecordPresence:@"unique-4" :@"/TestSlowSync/add"];
	[self tearDown];
	
	//Replace testcase
	[self setUp:@"replace"];
	[sync performSlowSync:channel :NO];
	MobileObject *afterUnique1 = [self getRecord:@"unique-1"];
	MobileObject *afterUnique2 = [self getRecord:@"unique-2"];
	[self assertRecordPresence:@"unique-1" :@"/TestSlowSync/replace"];
	[self assertRecordPresence:@"unique-2" :@"/TestSlowSync/replace"];
	NSString *compareMessage1 = @"<tag apos='apos' quote=\"quote\" ampersand='&'>unique-1/Message</tag>";
	NSString *compareMessage2 = @"<tag apos='apos' quote=\"quote\" ampersand='&'>unique-2/Updated/Client</tag>";
	NSString *afterUnique1Msg = [afterUnique1 getValue:@"message"];
	NSString *afterUnique2Msg = [afterUnique2 getValue:@"message"];
	NSLog(@"%@",afterUnique1Msg);
	NSLog(@"%@",afterUnique2Msg);
	[self assertTrue:[afterUnique1Msg isEqualToString:compareMessage1] :@"/TestSlowSync/replace/updated/unique-1"];
	[self assertTrue:[afterUnique2Msg isEqualToString:compareMessage2] :@"/TestSlowSync/replace/updated/unique-2"];
	[self tearDown];
	
	//Delete testcase
	[self setUp:@"delete"];
	[sync performSlowSync:channel :NO];
	[self assertRecordPresence:@"unique-1" :@"/TestSlowSync/delete"];
	[self assertRecordPresence:@"unique-2" :@"/TestSlowSync/delete"];
	[self tearDown];
	
	//Conflict testcase
	[self setUp:@"conflict"];
	[sync performSlowSync:channel :NO];
	afterUnique1 = [self getRecord:@"unique-1"];
	afterUnique2 = [self getRecord:@"unique-2"];
	[self assertRecordPresence:@"unique-1" :@"/TestSlowSync/conflict"];
	[self assertRecordPresence:@"unique-2" :@"/TestSlowSync/conflict"];
	compareMessage1 = @"<tag apos='apos' quote=\"quote\" ampersand='&'>unique-1/Updated/Client</tag>";
	compareMessage2 = @"<tag apos='apos' quote=\"quote\" ampersand='&'>unique-2/Message</tag>";
	afterUnique1Msg = [afterUnique1 getValue:@"message"];
	afterUnique2Msg = [afterUnique2 getValue:@"message"];
	NSLog(@"%@",afterUnique1Msg);
	NSLog(@"%@",afterUnique2Msg);
	[self assertTrue:[afterUnique1Msg isEqualToString:compareMessage1] :@"/TestSlowSync/conflict/updated/unique-1"];
	[self assertTrue:[afterUnique2Msg isEqualToString:compareMessage2] :@"/TestSlowSync/conflict/updated/unique-2"];
	[self tearDown];
}

-(NSString *) getInfo
{
	return [[self class] description];
}
@end
