/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "Process.h"
#import "SystemException.h"
#import "StringUtil.h"


@implementation Process

@synthesize states;
@synthesize activeContext;
@synthesize startState;
@synthesize endState;

+(id) withConfiguration:(Node *)startNode endNode:(Node *) endNode otherNodes:(NSArray *) otherNodes
{
	if(startNode == nil || endNode == nil)
	{
		NSMutableArray *info = [NSMutableArray arrayWithObjects:@"Incomplete configuration:Start and/or End Node is missing!!", nil];
		SystemException *exception = [SystemException withContext:@"sync/workflow/Process" method:@"withConfiguration" parameters:info];
		@throw exception;
	}
	
	Process *process = [[Process alloc] init];
	process.states = [GenericAttributeManager withInit];
	process.activeContext = [Context withInit];
	process.startState = startNode;
	process.endState = endNode;
	process.startState.isRoot = YES;
	
	//Initialize the process nodes
	if(otherNodes != nil)
	{
		for(Node *cour in otherNodes)
		{
			//TODO: add some validation here...nothing urgent since this is all internal
			//to the sync engine..App developer never touches this stuff
			[process.states setAttribute:cour.name :cour];
		}
	}
	
	return [process autorelease];
}

-(BOOL) signal
{
	//Starts the workflow...
	if(currentState == nil)
	{
		NSString* startTransition = startState.activeTransition;
		
		currentState = [self.states getAttribute:startTransition];
		
		//Process the state
		[currentState process:self.activeContext];
		
		return NO;
	}
	
	NSString *nextTransition = currentState.activeTransition;
	if([StringUtil isEmpty:nextTransition])
	{
		NSMutableArray *info = [NSMutableArray arrayWithObjects:@"Abnormal Exit:Missing Transition!!", nil];
		SystemException *exception = [SystemException withContext:@"sync/workflow/Process" method:@"signal" parameters:info];
		@throw exception;
	}
	
	//Check to make sure this is not the end of the workflow
	if([nextTransition isEqualToString:self.endState.name])
	{
		//this is the end of the workflow
		return YES;
	}
	
	//Transition to the next state
	currentState = [self.states getAttribute:nextTransition];
	
	//Execute this state
	[currentState process:self.activeContext];
	
	return NO;
}

-(NSString *) currentNodeName
{
	if(currentState != nil)
	{
		return currentState.name;
	}
	return nil;
}

@end
