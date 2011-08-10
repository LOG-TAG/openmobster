/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "BeanList.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation BeanList

@synthesize listProperty;
@synthesize entries;


+(id)withInit:(NSString *)listProperty
{
	BeanList *instance = [[BeanList alloc] init];
	
	instance.listProperty = listProperty;
	instance.entries = [NSMutableArray array];
	
	instance = [instance autorelease];
	return instance;
}

-(void) dealloc
{
    [listProperty release];
    [entries release];
    
    [super dealloc];
}

-(int)size
{
	return [entries count];
}

-(BeanListEntry *)entryAt:(int) index
{
	return [entries objectAtIndex:index];
}

-(void)addEntry:(BeanListEntry *)entry
{
	entry.listProperty = listProperty;
	[entries addObject:entry];
}

-(NSArray *)entries
{
	return [NSArray arrayWithArray:entries];
}
@end
