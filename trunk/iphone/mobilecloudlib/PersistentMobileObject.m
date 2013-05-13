/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "PersistentMobileObject.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation PersistentMobileObject

@dynamic oid;
@dynamic service;
@dynamic fields;
@dynamic arrayMetaData;
@dynamic serverRecordId;
@dynamic proxy;
@dynamic createdOnDevice;
@dynamic locked;
@dynamic dirtyStatus;
@dynamic nameValuePairs;


+(PersistentMobileObject *) newInstance:(NSString *)channel
{
	//Get the Storage Context
	NSManagedObjectContext *managedContext = [[CloudDBManager getInstance] storageContext];
	
	
	//Creates a new instance and returns it
	PersistentMobileObject *pm;
	pm = [NSEntityDescription insertNewObjectForEntityForName:@"PersistentMobileObject" inManagedObjectContext:managedContext];
	pm.oid = [GeneralTools generateUniqueId];
	pm.service = channel;
	[managedContext save:NULL];

	return pm;
}

+(NSArray *) findByChannel:(NSString *) channel
{
	//Get the Storage Context
	NSManagedObjectContext *managedContext = [[CloudDBManager getInstance] storageContext];
	NSEntityDescription *entity = [NSEntityDescription entityForName:@"PersistentMobileObject" 
											  inManagedObjectContext:managedContext];
	
	//Get an instance if its already been provisioned
	NSFetchRequest *request = [[[NSFetchRequest alloc] init] autorelease];
	[request setEntity:entity];
    
    //find by channel
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"(service == %@)",channel];
    [request setPredicate:predicate];
	
	NSArray *all = [managedContext executeFetchRequest:request error:NULL];
	
	return [NSArray arrayWithArray:all];
}

+(PersistentMobileObject *) findByOID:(NSString *) oid
{
	//Get the Storage Context
	NSManagedObjectContext *managedContext = [[CloudDBManager getInstance] storageContext];
	NSEntityDescription *entity = [NSEntityDescription entityForName:@"PersistentMobileObject" 
											  inManagedObjectContext:managedContext];
	
	//Get an instance if its already been provisioned
	NSFetchRequest *request = [[[NSFetchRequest alloc] init] autorelease];
	[request setEntity:entity];
    
    //find by oid
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"(oid == %@)",oid];
    [request setPredicate:predicate];
	
	NSArray *all = [managedContext executeFetchRequest:request error:NULL];
	
	//filter by oid
	if(all != nil && [all count] >0)
	{
		return (PersistentMobileObject *)[all objectAtIndex:0];
	}
	
	return nil;
}

+(BOOL) delete:(PersistentMobileObject *)mobileObject
{
	NSManagedObjectContext *managedContext = [[CloudDBManager getInstance] storageContext];
	[managedContext deleteObject:mobileObject];
	
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

+(BOOL) deleteAll:(NSString *)channel
{
	NSArray *all = [PersistentMobileObject findByChannel:channel];
	
	if(all != nil)
	{
		NSManagedObjectContext *managedContext = [[CloudDBManager getInstance] storageContext];
		
		for(PersistentMobileObject *local in all)
		{
			[managedContext deleteObject:local];
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
	
	return YES;
}

-(BOOL) saveInstance
{
	//Get the Storage Context
	NSManagedObjectContext *managedContext = [[CloudDBManager getInstance] storageContext];
	
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

-(void) addField:(Field *) field
{
	if(self.fields == nil)
	{
		self.fields = [NSMutableArray array];
	}
	
	
	NSMutableDictionary *dictionary = [NSMutableDictionary dictionary];
	[dictionary setValue:field.uri forKey:@"uri"];
	[dictionary setValue:field.name forKey:@"name"];
	[dictionary setValue:field.value forKey:@"value"];
	[(NSMutableArray *)self.fields addObject:dictionary];
    
    NSString *nameValue = [NSString stringWithFormat:@"name=%@,value=%@",field.name,field.value];
    
    if(self.nameValuePairs == nil)
    {
        self.nameValuePairs = @"";
    }
    
    NSMutableString *buffer = [NSMutableString stringWithString:self.nameValuePairs];
    [buffer appendFormat:@"%@%@",nameValue,@"|"];
    
    self.nameValuePairs = [NSString stringWithString:buffer];
}

-(NSArray *)parseFields
{
	NSMutableArray *local = [NSMutableArray array];
	
	if(self.fields != nil)
	{
		for(NSDictionary *cour in self.fields)
		{
			NSString *uri = (NSString *)[cour objectForKey:@"uri"];
			NSString *name = (NSString *)[cour objectForKey:@"name"];
			NSString *value = (NSString *)[cour objectForKey:@"value"];
			
			Field *field = [Field withInit:uri name:name value:value];
			[local addObject:field];
        }
	}
	
	return [NSArray arrayWithArray:local];
}

-(void) addArrayMetaData:(ArrayMetaData *)arrayMetaData
{
	if(self.arrayMetaData == nil)
	{
		self.arrayMetaData = [NSMutableArray array];
	}
	
	NSMutableDictionary *dictionary = [NSMutableDictionary dictionary];
	[dictionary setValue:arrayMetaData.arrayUri forKey:@"arrayUri"];
	[dictionary setValue:arrayMetaData.arrayLength forKey:@"arrayLength"];
	[dictionary setValue:arrayMetaData.arrayClass forKey:@"arrayClass"];
	[(NSMutableArray *)self.arrayMetaData addObject:dictionary];
}

-(NSArray *)parseArrayMetaData
{
	NSMutableArray *local = [NSMutableArray array];
	
	if(self.arrayMetaData != nil)
	{
		for(NSDictionary *cour in self.arrayMetaData)
		{
			NSString *arrayUri = (NSString *)[cour objectForKey:@"arrayUri"];
			NSString *arrayLength = (NSString *)[cour objectForKey:@"arrayLength"];
			NSString *arrayClass = (NSString *)[cour objectForKey:@"arrayClass"];
			
			ArrayMetaData *arrayMetaData = [ArrayMetaData withInit:arrayUri arrayLength:arrayLength arrayClass:arrayClass];
			[local addObject:arrayMetaData];
		}
	}
	
	return [NSArray arrayWithArray:local];
}

-(MobileObject *)parseMobileObject
{
	MobileObject *mobileObject = [MobileObject withInit];
	
	mobileObject.service = self.service;
	mobileObject.recordId = self.oid;
	mobileObject.serverRecordId = self.serverRecordId;
	
	if([self.proxy boolValue])
	{
		mobileObject.proxy = YES;
	}
	else 
	{
		mobileObject.proxy = NO;
	}

	if([self.createdOnDevice boolValue])
	{
		mobileObject.createdOnDevice = YES;
	}
	else 
	{
		mobileObject.createdOnDevice = NO;
	}
	
	if([self.locked boolValue])
	{
		mobileObject.locked = YES;
	}
	else 
	{
		mobileObject.locked = NO;
	}
	
	mobileObject.dirtyStatus = self.dirtyStatus;
	mobileObject.fields = [NSMutableArray arrayWithArray:[self parseFields]];
	mobileObject.arrayMetaData = [NSMutableArray arrayWithArray:[self parseArrayMetaData]];
	
	return mobileObject;
}

-(void)setState:(MobileObject *)mobileObject
{
	self.service = mobileObject.service;
	
	if(![StringUtil isEmpty:mobileObject.recordId])
	{
		self.oid = mobileObject.recordId;
	}
	self.serverRecordId = mobileObject.serverRecordId;
	
	if(mobileObject.proxy)
	{
		self.proxy = [NSNumber numberWithBool:YES];
	}
	else 
	{
		self.proxy = [NSNumber numberWithBool:NO];
	}
		
	if(mobileObject.createdOnDevice)
	{
		self.createdOnDevice = [NSNumber numberWithBool:YES];
	}
	else 
	{
		self.createdOnDevice = [NSNumber numberWithBool:NO];
	}

	if(mobileObject.locked)
	{
		self.locked = [NSNumber numberWithBool:YES];
	}
	else 
	{
		self.locked = [NSNumber numberWithBool:NO];
	}
	
	self.dirtyStatus = mobileObject.dirtyStatus;
	
	NSArray *fields = mobileObject.fields;
	if(fields != nil)
	{
		[(NSMutableArray *)self.fields removeAllObjects];
		for(Field *field in fields)
		{
			[self addField:field];
		}
	}
	
	NSArray *arrayMetaData = mobileObject.arrayMetaData;
	if(arrayMetaData != nil)
	{
		[(NSMutableArray *)self.arrayMetaData removeAllObjects];
		for(ArrayMetaData *local in arrayMetaData)
		{
			[self addArrayMetaData:local];
		}
	}
}
@end