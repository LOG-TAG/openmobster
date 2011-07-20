/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "ChangeLogEntry.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation ChangeLogEntry

@dynamic nodeId;
@dynamic operation;
@dynamic recordId;

@synthesize item; //not persisted
@synthesize tempNodeId; //not persisted
@synthesize tempOperation; //not persisted
@synthesize tempRecordId; //not persisted

+(id)withInit
{
	ChangeLogEntry *instance = [[ChangeLogEntry alloc] init];
	return [instance autorelease];
}

+(ChangeLogEntry *) getInstance:(NSString *)nodeId :(NSString *)operation :(NSString *)recordId
{
	if(nodeId == nil)
	{
		@throw NSInvalidArgumentException;
	}
	if(operation == nil)
	{
		@throw NSInvalidArgumentException;
	}
	if(recordId == nil)
	{
		@throw NSInvalidArgumentException;
	}
	
	//Get the Storage Context
	NSManagedObjectContext *managedContext = [CloudDBManager getInstance].storageContext;
	NSEntityDescription *entity = [NSEntityDescription entityForName:@"ChangeLogEntry" inManagedObjectContext:managedContext];
	
	//Get an instance if its already been provisioned
	NSFetchRequest *request = [[[NSFetchRequest alloc] init] autorelease];
	[request setEntity:entity];
	
	NSArray *all = [managedContext executeFetchRequest:request error:NULL];
	
	if(all != nil && [all count] > 0)
	{
		for(ChangeLogEntry *local in all)
		{
			if([local.nodeId isEqualToString:nodeId] &&
			   [local.operation isEqualToString:operation] &&
			   [local.recordId isEqualToString:recordId]
			   )
			{
				return local;
			}
		}
	}
	
	//Creates a new instance and returns it
	ChangeLogEntry *entry = [NSEntityDescription insertNewObjectForEntityForName:@"ChangeLogEntry" inManagedObjectContext:managedContext];
	entry.nodeId = nodeId;
	entry.operation = operation;
	entry.recordId = recordId;
	[managedContext save:NULL];
	
	return entry;
}

+(NSArray *)all
{
	//Get the Storage Context
	NSManagedObjectContext *managedContext = [CloudDBManager getInstance].storageContext;
	NSEntityDescription *entity = [NSEntityDescription entityForName:@"ChangeLogEntry" inManagedObjectContext:managedContext];
	
	//Get an instance if its already been provisioned
	NSFetchRequest *request = [[[NSFetchRequest alloc] init] autorelease];
	[request setEntity:entity];
	
	NSArray *all = [managedContext executeFetchRequest:request error:NULL];
	
	return all;
}

-(BOOL) saveInstance
{
	//Get the Storage Context
	NSManagedObjectContext *managedContext = [CloudDBManager getInstance].storageContext;
	
	NSError* error;
	BOOL success = [managedContext save:&error];
	
	//Used for debugging
	/*if(!success)
	 {
	 NSLog(@"Error during save!!!");
	 NSLog(@"SaveError: %@, %@", error, [error userInfo]);
	 }
	 else 
	 {
	 NSLog(@"Save was a success!!!");
	 }*/
	
	
	return success;
}

+(BOOL)delete:(ChangeLogEntry *)entry
{
	NSManagedObjectContext *managedContext = [CloudDBManager getInstance].storageContext;
	[managedContext deleteObject:entry];
	
	NSError* error;
	BOOL success = [managedContext save:&error];
	
	//Used for debugging
	/*if(!success)
	 {
	 NSLog(@"Error during save!!!");
	 NSLog(@"SaveError: %@, %@", error, [error userInfo]);
	 }
	 else 
	 {
	 NSLog(@"Save was a success!!!");
	 }*/
	
	return success;
}

+(BOOL)deleteEntries:(NSArray *)entries
{
	if(entries == nil || [entries count] == 0)
	{
		return YES;
	}
	
	NSManagedObjectContext *managedContext = [CloudDBManager getInstance].storageContext;
	
	if(entries != nil)
	{
		for(ChangeLogEntry *local in entries)
		{
			[managedContext deleteObject:local];
		}
	}
	
	NSError* error;
	BOOL success = [managedContext save:&error];
	
	//Used for debugging
	/*if(!success)
	 {
	 NSLog(@"Error during save!!!");
	 NSLog(@"SaveError: %@, %@", error, [error userInfo]);
	 }
	 else 
	 {
	 NSLog(@"Save was a success!!!");
	 }*/
	
	return success;
}

+(BOOL)deleteAll
{
	NSArray *all = [ChangeLogEntry all];
	
	return [ChangeLogEntry deleteEntries:all];
}

-(NSString *) description
{
	NSMutableString *toString = [NSMutableString stringWithString:@""];
	
	[toString appendFormat:@"Service: %@\n",self.nodeId];
	[toString appendFormat:@"Operation: %@\n",self.operation];
	[toString appendFormat:@"RecordId: %@\n",self.recordId];
	
	return toString;
}
@end
