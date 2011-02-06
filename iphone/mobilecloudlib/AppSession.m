/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "AppSession.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation AppSession

-(void)dealloc
{
	[mgr release];
	[super dealloc];
}

+(AppSession *)getInstance
{
	Registry *registry = [Registry getInstance];
	return (AppSession *)[registry lookup:[AppSession class]];
}

-(void)start
{
	mgr = [[GenericAttributeManager alloc] initWithRetention];
}

-(void)setAttribute:(NSString *)name :(id)value
{
	[mgr setAttribute:name :value];
}

-(id)getAttribute:(NSString *)name
{
	return [mgr getAttribute:name];
}

-(void)removeAttribute:(NSString *)name
{
	[mgr removeAttribute:name];
}
@end
