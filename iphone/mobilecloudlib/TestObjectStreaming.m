/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "TestObjectStreaming.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation TestObjectStreaming

+(id)withInit
{
	return [[[TestObjectStreaming alloc] init] autorelease];
}

-(void) runTest
{
	SyncService *sync = [SyncService getInstance];
	
	[self setUp:@"add"];
	[sync performTwoWaySync:channel :NO];
	[self assertRecordPresence:@"unique-1" :@"/TestObjectStreaming/add"];
	[self assertRecordPresence:@"unique-2" :@"/TestObjectStreaming/add"];
	[self assertRecordPresence:@"unique-3" :@"/TestObjectStreaming/add"];
	[self assertRecordPresence:@"unique-4" :@"/TestObjectStreaming/add"];
	
	MobileObject *unique1 = [self getRecord:@"unique-1"];
	NSString *attachment = [unique1 getValue:@"attachment"];
	[self assertTrue:attachment == nil :@"/TestObjectStreaming/AttachmentMustNotBeDownloaded"];
	
	
	[sync performStreamSync:channel :NO :@"unique-1"];
	unique1 = [self getRecord:@"unique-1"];
	attachment = [unique1 getValue:@"attachment"];
	[self assertTrue:attachment != nil :@"/TestObjectStreaming/AttachmentMustBeDownloaded"];
	
	[self tearDown];
}

-(NSString *) getInfo
{
	return [[self class] description];
}
@end
