/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "MobileBeanMetaData.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation MobileBeanMetaData

@synthesize service;
@synthesize id;
@synthesize deleted;

+(MobileBeanMetaData *)withInit:(NSString *)service :(NSString *)id
{
	MobileBeanMetaData *instance = [[MobileBeanMetaData alloc] init];
	
	instance = [instance autorelease];
	
	instance->service = service;
	instance->id = id;
	instance.deleted = NO;
	
	return instance;
}
@end
