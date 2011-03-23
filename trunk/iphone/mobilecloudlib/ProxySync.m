/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "ProxySync.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation ProxySync

+(id)withInit
{
	ProxySync *instance = [[[ProxySync alloc] init] autorelease];
	return instance;
}

-(void)sync:(id)channel
{
	NSLog(@"Executing Proxy sync: %@",channel);
}
@end
