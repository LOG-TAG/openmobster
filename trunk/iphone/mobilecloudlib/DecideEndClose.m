/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "DecideEndClose.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation DecideEndClose

+(id)withInit
{
	DecideEndClose *newinstance = [[[DecideEndClose alloc] init] autorelease];
	return newinstance;
}

-(NSString *) decide:(Context *)context
{
	WorkflowManager *manager = (WorkflowManager *)[context getAttribute:@"manager"];
	Session *activeSession = [manager activeSession];
	
	//TODO: Map Support
	BOOL clientFinished = [SyncHelper containsFinal:activeSession.clientClosePackage.messages];
	BOOL serverFinished = [SyncHelper containsFinal:activeSession.serverClosePackage.messages];
	BOOL errors = [SyncHelper hasErrors:activeSession.currentMessage];
	
	if(clientFinished && serverFinished && !errors)
	{
		return @"close_session";
	}
	
	//otherwise go back to start sync
	return @"start_close";	
}

@end
