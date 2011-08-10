/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import <Foundation/Foundation.h>
#import "BeanListEntry.h"

/**
 * 
 * @author openmobster@gmail.com
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

-(int)size;
-(NSArray *)entries;
-(BeanListEntry *)entryAt:(int) index;
-(void)addEntry:(BeanListEntry *)entry;

@end
