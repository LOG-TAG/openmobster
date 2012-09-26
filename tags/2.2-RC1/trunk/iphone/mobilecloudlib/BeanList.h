/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import <Foundation/Foundation.h>
#import "BeanListEntry.h"

/*!
    BeanList is the representation an indexed property such as an array, list, etc
 @author openmobster@gmail.com
 */
@interface BeanList : NSObject 
{
	@private
	NSMutableArray *entries;
	NSString *listProperty;
}

@property(nonatomic,retain)NSString *listProperty;
@property(nonatomic,retain)NSMutableArray *entries;

+(id)withInit:(NSString *)listProperty;

/**
 * Provides the size of the List represented by this property
 * 
 * @return the size of this list property
 */
-(int)size;

/**
 * Provides an enumeration of entries in this list
 * 
 * @return entries of this list (BeanListEntry instances)
 */
-(NSArray *)entries;

/**
 * Provides the entry located at the specified index
 * 
 * @param index index of the entry
 * @return the entry
 */
-(BeanListEntry *)entryAt:(int) index;

/**
 * Add an entry to the list
 * 
 * @param entry the entry to be added
 */
-(void)addEntry:(BeanListEntry *)entry;
@end
