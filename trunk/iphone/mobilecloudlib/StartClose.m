/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "StartClose.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation StartClose

+(id) withInit
{
	return [[[StartClose alloc] init] autorelease];
}

-(NSString *) execute:(Context *)context
{	
	WorkflowManager *manager = (WorkflowManager *)[context getAttribute:@"manager"];
	Session *activeSession = [manager activeSession];
	
	SyncMessage *incoming = activeSession.currentMessage;
	SyncMessage *outgoing = [SyncMessage withInit];
	int messageId = [incoming.messageId intValue];
	messageId++;
	outgoing.messageId = [NSString stringWithFormat:@"%d",messageId];
	
	int cmdId = 1; //id for the first command in this outgoing message
	
	//Consume the domain data by passing to the sync engine
	[SyncHelper processSyncCommands:context :cmdId :activeSession :outgoing];
	
	//ChangeLog Support
	[SyncHelper cleanupChangeLog:context :activeSession];
	
	//TODO: Map Support
	
	//Make this the final
	outgoing.final = YES;
	
	[context setAttribute:_PAYLOAD :outgoing];
	
	return @"decide_end_close";
}

@end
