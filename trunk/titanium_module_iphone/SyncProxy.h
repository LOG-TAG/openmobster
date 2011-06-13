/**
 * Appcelerator Titanium Mobile
 * Copyright (c) 2009-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 */
#import "TiProxy.h"
#import "MobileBean.h"

@interface SyncProxy : TiProxy 
{
	@private
	MobileBean *newBean;
}

+(SyncProxy *)withInit;

-(id) ping:(id)input;

-(id) readAll:(id)channel;

-(id) getValue:(id)input;

-(id) arrayLength:(id)input;

-(id) deleteBean:(id) input;

-(id) setValue:(id)input;

-(id) newBean:(id)input;

-(id) setNewBeanValue:(id)input;

-(id) saveNewBean:(id)input;

@end
