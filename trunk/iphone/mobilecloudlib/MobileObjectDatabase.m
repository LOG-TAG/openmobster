/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "MobileObjectDatabase.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation MobileObjectDatabase

+(MobileObjectDatabase *) getInstance
{
	Registry *registry = [Registry getInstance];
	return (MobileObjectDatabase *)[registry lookup:[MobileObjectDatabase class]];
}

-(NSArray *)readAll:(NSString *)channel
{
	NSArray *persistentMobileObjects = [PersistentMobileObject findByChannel:channel];
	NSMutableArray *all = [NSMutableArray array];
	if(all != nil)
	{
		for(PersistentMobileObject *local in persistentMobileObjects)
		{
			[all addObject:[local parseMobileObject]];
		}
	}
	return [NSArray arrayWithArray:all];
}

-(MobileObject *)read:(NSString *)channel:(NSString *)recordId
{
	NSArray *all = [self readAll:channel];
	if(all != nil)
	{
		for(MobileObject *local in all)
		{
			if([local.recordId isEqualToString:recordId])
			{
				return local;
			}
		}
	}
	return nil;
}

-(NSString *)create:(MobileObject *)mobileObject
{
	PersistentMobileObject *newObject = [PersistentMobileObject newInstance:mobileObject.service];
	
	mobileObject.dirtyStatus = [GeneralTools generateUniqueId];
	
	[newObject setState:mobileObject];
	[newObject saveInstance];
	return newObject.oid;
}

-(void)update:(MobileObject *)mobileObject
{
	PersistentMobileObject *stored = [PersistentMobileObject findByOID:mobileObject.recordId];
	if(stored != nil)
	{
		//Check for outdated data (Optimistic Locking)
		NSString *storedStatus = stored.dirtyStatus;
		NSString *activeStatus = mobileObject.dirtyStatus;
		
		if(activeStatus != nil)
		{
			if(![storedStatus isEqualToString:activeStatus])
			{
				NSMutableArray *params = [NSMutableArray arrayWithObjects:@"Optimistic Locking Failure!!",nil];
				SystemException *ex = [SystemException withContext:@"MobileObjectDatabase" method:@"update" parameters:params];
				@throw ex;
			}
		}
		
		mobileObject.dirtyStatus = [GeneralTools generateUniqueId];
		[stored setState:mobileObject];
		[stored saveInstance];
	}
}

-(void)delete:(MobileObject *)mobileObject
{
	PersistentMobileObject *stored = [PersistentMobileObject findByOID:mobileObject.recordId];
	if(stored != nil)
	{
		[PersistentMobileObject delete:stored];
	}
}

-(void)deleteAll:(NSString *)channel
{
	[PersistentMobileObject deleteAll:channel];
}

-(NSArray *)query:(NSString *)channel:(GenericAttributeManager *)queryAttributes
{
	//TODO: implement me
	return nil;
}
@end
