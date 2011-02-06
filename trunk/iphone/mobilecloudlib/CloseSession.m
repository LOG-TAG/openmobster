/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "CloseSession.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation CloseSession

+(id)withInit
{
	CloseSession *newinstance = [[[CloseSession alloc] init] autorelease];
	return newinstance;
}

-(NSString *) execute:(Context *)context
{
	/*WorkflowManager *manager = (WorkflowManager *)[context getAttribute:@"manager"];
	Session *activeSession = [manager activeSession];
	
	SyncMessage *incoming = activeSession.currentMessage;*/
	
	//FIXME:Push Service integration
	
	//TODO: Map Support
	
	
	return @"end";
}
@end
