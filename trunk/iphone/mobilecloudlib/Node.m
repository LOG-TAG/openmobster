/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "Node.h"
#import "SystemException.h"


@implementation Node

@synthesize name;
@synthesize activeTransition;
@synthesize handler;
@synthesize isRoot;

+(id) withInit
{
	return [[[Node alloc] init] autorelease];
}

-(void) process:(Context *) context
{
	if(self.handler == nil && !self.isRoot)
	{
		NSMutableArray *info = [NSMutableArray arrayWithObjects:@"Incomplete configuration:Workflow Node without a Handler", nil];
		SystemException *exception = [SystemException withContext:@"sync/workflow/Node" method:@"process" parameters:info];
		@throw exception;
	}
	
	if([self.handler conformsToProtocol:@protocol(ActionHandler)])
	{
		self.activeTransition = [self.handler execute:context];
	}
	else if([self.handler conformsToProtocol:@protocol(DecisionHandler)])
	{
		self.activeTransition = [self.handler decide:context];
	}
}

@end
