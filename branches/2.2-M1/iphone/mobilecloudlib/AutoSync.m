/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "AutoSync.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation AutoSync

+(id)withInit
{
	AutoSync *autosync = (AutoSync *)[[[AutoSync alloc] init] autorelease];
	return autosync;
}

-(void)sync
{
	NSLog(@"Executing AutoSync.....");
	
	//FIXME: implement this
}
@end
