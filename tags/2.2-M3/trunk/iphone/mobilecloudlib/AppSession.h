/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import <Foundation/Foundation.h>
#import "Service.h"
#import "GenericAttributeManager.h"
#import "Registry.h"

/**
 * 
 * @author openmobster@gmail.com
 */
@interface AppSession : Service 
{
	@private
	GenericAttributeManager *mgr;
}

+(AppSession *)getInstance;
-(void)start;

-(void)setAttribute:(NSString *)name :(id)value;
-(id)getAttribute:(NSString *)name;
-(void)removeAttribute:(NSString *)name;
@end
