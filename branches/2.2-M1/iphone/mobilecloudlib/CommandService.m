/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "CommandService.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation CommandService

+(CommandService *)getInstance
{
	//FIXME: re-implement via UIKernel integration
	CommandService *instance = [[CommandService alloc] init];
	instance = [instance autorelease];
	return instance;
}

-(void)execute:(CommandContext *)commandContext
{
	ExecuteOnEDT *interaction = [ExecuteOnEDT withInit:commandContext];
	[interaction performSelectorOnMainThread:@selector(run) withObject:nil waitUntilDone:NO];
}
@end
