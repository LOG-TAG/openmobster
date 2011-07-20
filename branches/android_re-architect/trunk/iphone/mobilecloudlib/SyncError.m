/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "SyncError.h"

NSString *const _RESET_SYNC_STATE = @"1";

/**
 * 
 * @author openmobster@gmail.com
 */
@implementation SyncError

@dynamic code;
@dynamic source;
@dynamic target;

+(SyncError *) getInstance:(NSString *)code :(NSString *)source :(NSString *)target
{
	if(code == nil)
	{
		@throw NSInvalidArgumentException;
	}
	if(source == nil)
	{
		@throw NSInvalidArgumentException;
	}
	if(target == nil)
	{
		@throw NSInvalidArgumentException;
	}
	
	//Get the Storage Context
	NSManagedObjectContext *managedContext = [CloudDBManager getInstance].storageContext;
	NSEntityDescription *entity = [NSEntityDescription entityForName:@"SyncError" inManagedObjectContext:managedContext];
	
	//Get an instance if its already been provisioned
	NSFetchRequest *request = [[[NSFetchRequest alloc] init] autorelease];
	[request setEntity:entity];
	
	NSArray *all = [managedContext executeFetchRequest:request error:NULL];
	
	if(all != nil && [all count] > 0)
	{
		for(SyncError *local in all)
		{
			if([local.code isEqualToString:code] &&
			   [local.source isEqualToString:source] &&
			   [local.target isEqualToString:target]
			   )
			{
				return local;
			}
		}
	}
	
	//Creates a new instance and returns it
	SyncError *error = [NSEntityDescription insertNewObjectForEntityForName:@"SyncError" inManagedObjectContext:managedContext];
	error.code = code;
	error.source = source;
	error.target = target;
	[managedContext save:NULL];
	
	return error;
}

+(NSArray *) all
{
	//Get the Storage Context
	NSManagedObjectContext *managedContext = [CloudDBManager getInstance].storageContext;
	NSEntityDescription *entity = [NSEntityDescription entityForName:@"SyncError" inManagedObjectContext:managedContext];
	
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

+(BOOL)delete:(SyncError *)syncError
{
	NSManagedObjectContext *managedContext = [CloudDBManager getInstance].storageContext;
	[managedContext deleteObject:syncError];
	
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
	NSManagedObjectContext *managedContext = [CloudDBManager getInstance].storageContext;
	NSArray *all = [SyncError all];
	if(all != nil)
	{
		for(SyncError *local in all)
		{
			[managedContext deleteObject:local];
		}
	}
	
	NSError *error;
	return [managedContext save:&error];
}
@end