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
@interface Field : NSObject 
{
	@private
	NSString *uri;
	NSString *name;
	NSString *value;
}

+(id) withInit;
+(id) withInit:(NSString *)uri name:(NSString *)name value:(NSString *)value;

@property (retain) NSString *uri;
@property (retain) NSString *name;
@property (retain) NSString *value;



@end
