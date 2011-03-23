/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import <Foundation/Foundation.h>
#import "GenericAttributeManager.h"
#import "Context.h"
#import "Node.h"


@interface Process : NSObject 
{
	@private
	GenericAttributeManager *states;
	Context *activeContext;
	
	Node *startState;
	Node *endState;
	Node *currentState;
}

@property (assign) GenericAttributeManager *states;
@property (assign) Context *activeContext;
@property (assign) Node *startState;
@property (assign) Node *endState;

+(id) withConfiguration:(Node *)startNode endNode:(Node *) endNode otherNodes:(NSArray *) otherNodes;

-(BOOL) signal;
-(NSString *) currentNodeName;

@end
