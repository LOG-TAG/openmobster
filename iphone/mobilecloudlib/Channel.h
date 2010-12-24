/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import <Foundation/Foundation.h>

/**
 * 
 * @author openmobster@gmail.com
 */
@interface Channel : NSObject 
{
	@private
	NSString *name;
	NSString *owner;
}

@property (assign) NSString *name;
@property (assign) NSString *owner;

+(id)withInit:(NSString *)name :(NSString *)owner;
+(id)withInit;

@end
