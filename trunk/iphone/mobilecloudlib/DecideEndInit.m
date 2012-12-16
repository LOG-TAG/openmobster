/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "DecideEndInit.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation DecideEndInit

+(id) withInit
{
	return [[[DecideEndInit alloc] init] autorelease];
}

-(NSString *) decide:(Context *)context
{
	WorkflowManager *manager = (WorkflowManager *)[context getAttribute:@"manager"];
	Session *activeSession = [manager activeSession];
	
	[activeSession.serverInitPackage addMessage:activeSession.currentMessage];
	
	if(![SyncHelper authorized:activeSession] || 
	   [SyncHelper hasErrors:activeSession.currentMessage]
	)
	{
		return @"end";
	}
	
	BOOL clientFinished = [SyncHelper containsFinal:activeSession.clientInitPackage.messages];
	BOOL serverFinished = [SyncHelper containsFinal:activeSession.serverInitPackage.messages];
	BOOL initPhaseSucceeded = [SyncHelper initSuccess:activeSession];
	if(clientFinished && serverFinished && initPhaseSucceeded)
	{
		//Process the next nonce
		[SyncHelper processNextNonce:activeSession];
		
		//Progress to the 'Sync Phase'
		return @"start_sync";
	}

	
	return @"end";
}

@end
