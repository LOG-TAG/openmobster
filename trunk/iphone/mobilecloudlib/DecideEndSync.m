/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "DecideEndSync.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation DecideEndSync

+(id) withInit
{
	return [[[DecideEndSync alloc] init] autorelease];
}

-(NSString *) decide:(Context *)context
{
	WorkflowManager *manager = (WorkflowManager *)[context getAttribute:@"manager"];
	Session *activeSession = [manager activeSession];
	
	//TODO: Map Support
	BOOL clientFinished = [SyncHelper containsFinal:activeSession.clientSyncPackage.messages];
	BOOL serverFinished = [SyncHelper containsFinal:activeSession.serverSyncPackage.messages];
	BOOL errors = [SyncHelper hasErrors:activeSession.currentMessage];
	
	if(clientFinished && serverFinished && !errors)
	{
		return @"start_close";
	}
	
	//otherwise go back to start sync
	return @"start_sync";
}

@end
