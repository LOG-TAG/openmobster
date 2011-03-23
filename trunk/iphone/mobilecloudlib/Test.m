/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "Test.h"

/**
 * 
 * @author openmobster@gmail.com
 */
@implementation Test

@synthesize suite;

-(void) setUp
{
}

-(void) tearDown
{
}

-(void) runTest
{
}

-(NSString *) getInfo
{
	return nil;
}

-(void)assertTrue:(BOOL)actualValue :(NSString *)context
{
	if(!actualValue)
	{
		NSString *errorMessage = [NSString stringWithFormat:@"AssertionError: Value Must Be True: %@",context];
		[self.suite reportError:errorMessage];
	}
}
@end
