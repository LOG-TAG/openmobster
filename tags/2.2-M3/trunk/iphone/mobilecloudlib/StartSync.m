/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "StartSync.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation StartSync

+(id) withInit
{
	return [[[StartSync alloc] init] autorelease];
}

-(NSString *) execute:(Context *)context
{
	WorkflowManager *manager = (WorkflowManager *)[context getAttribute:@"manager"];
	Session *activeSession = [manager activeSession];

	//TODO: very low priority (Map Supprt)
	
	//TODO: very very low priority (LongObject Support..only for low bandwidth networks from
	//stone age of mobile
	
	//Setup the on-device changelog
	[SyncHelper cleanupChangeLog:context :activeSession];
	
	//Decide where to transition
	NSString *syncType = activeSession.syncType;
	SyncMessage *message = nil;
	
	if([syncType isEqualToString:_BOOT_SYNC])
	{
		//Boot Sync
		message = [SyncHelper bootSync:context :activeSession];
	}
	else if([syncType isEqualToString:_STREAM])
	{
		//Stream Sync
		message = [SyncHelper streamSync:context :activeSession];
	}
	else 
	{
		//Standard Sync
		message = [SyncHelper normalSync:context :activeSession];
	}

	//Update the Workflow State
	[context setAttribute:_PAYLOAD :message];
	
	return @"decide_end_sync";
}
@end
