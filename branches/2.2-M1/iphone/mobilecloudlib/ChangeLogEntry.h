/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>
#import "Item.h"
#import "CloudDBManager.h"

/**
 * 
 * @author openmobster@gmail.com
 */
@interface ChangeLogEntry : NSManagedObject 
{
	@private
	Item *item; //not persisted
	NSString *tempNodeId; //not persisted
	NSString *tempOperation; //not persisted
	NSString *tempRecordId;//not persisted
}

@property (nonatomic,retain) NSString *nodeId;
@property (nonatomic,retain) NSString *operation;
@property (nonatomic,retain) NSString *recordId;

@property (assign) Item *item; //not persisted
@property (assign) NSString *tempNodeId; //not persisted
@property (assign) NSString *tempOperation; //not persisted
@property (assign) NSString *tempRecordId; //not persisted

+(id)withInit;


//Persistence related
+(ChangeLogEntry *) getInstance:(NSString *)nodeId :(NSString *)operation :(NSString *)recordId;
+(NSArray *)all;
+(BOOL)delete:(ChangeLogEntry *)entry;
+(BOOL)deleteEntries:(NSArray *)entries;
+(BOOL)deleteAll;
-(BOOL) saveInstance;

//App related
-(NSString *) description;

@end
