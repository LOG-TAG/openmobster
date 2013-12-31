/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "MobileObjectDatabase.h"
#import "LogicChain.h"
#import "LogicExpression.h"
#import "Query.h"
#import "MetaData.h"


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
    PersistentMobileObject *stored = [PersistentMobileObject findByOID:recordId];
    if(stored != nil)
    {
        MobileObject *mobileObject = [stored parseMobileObject];
        return mobileObject;
    }
	return nil;
}

-(NSString *)create:(MobileObject *)mobileObject
{
	PersistentMobileObject *newObject = [PersistentMobileObject newInstance:mobileObject.service];
	
	mobileObject.dirtyStatus = [GeneralTools generateUniqueId];
	
	[newObject setState:mobileObject];
    
	
    BOOL success = [newObject saveInstance];
    if(!success)
    {
        NSMutableArray *params = [NSMutableArray arrayWithObjects:@"commit_failure",nil];
        SystemException *ex = [SystemException withContext:@"MobileObjectDatabase" method:@"create" parameters:params];
        @throw ex; 
    }
	
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
        
        
		BOOL success = [stored saveInstance];
        if(!success)
        {
            NSMutableArray *params = [NSMutableArray arrayWithObjects:@"commit_failure",nil];
            SystemException *ex = [SystemException withContext:@"MobileObjectDatabase" method:@"update" parameters:params];
            @throw ex; 
        }
	}
}

-(void)delete:(MobileObject *)mobileObject
{
	PersistentMobileObject *stored = [PersistentMobileObject findByOID:mobileObject.recordId];
	if(stored != nil)
	{
		BOOL success = [PersistentMobileObject delete:stored];
        if(!success)
        {
            NSMutableArray *params = [NSMutableArray arrayWithObjects:@"commit_failure",nil];
            SystemException *ex = [SystemException withContext:@"MobileObjectDatabase" method:@"delete" parameters:params];
            @throw ex; 
        }
	}
}

-(void)deleteAll:(NSString *)channel
{
	BOOL success = [PersistentMobileObject deleteAll:channel];
    if(!success)
    {
        NSMutableArray *params = [NSMutableArray arrayWithObjects:@"commit_failure",nil];
        SystemException *ex = [SystemException withContext:@"MobileObjectDatabase" method:@"deleteAll" parameters:params];
        @throw ex; 
    }
}

-(NSSet *)query:(NSString *)channel:(GenericAttributeManager *)queryAttributes
{
    //Find the Logic Link
    int logicLink = AND; //default value
    NSNumber *logicLinkAttribute = [queryAttributes getAttribute:@"logicLink"];
    if(logicLinkAttribute != nil)
    {
        logicLink = [logicLinkAttribute intValue];
    }
    
    //Setup the expressions
    NSArray *expressions = (NSArray *)[queryAttributes getAttribute:@"expressions"];
    if(expressions == nil || [expressions count] == 0)
    {
        NSMutableArray *params = [NSMutableArray arrayWithObjects:@"Query structure is improper!!",nil];
        SystemException *sys = [SystemException withContext:@"MobileObjectDatabase" method:@"query" parameters:params];
        @throw sys;
    }
    
    //Establish the LogicChain
    LogicChain *chain = nil;
    switch(logicLink)
    {
        case AND:
            chain = [LogicChain createANDChain];					
            break;
            
        case OR:
            chain = [LogicChain createORChain];
            break;
            
        default:
            break;
    }
    if(chain == nil)
    {
        NSMutableArray *params = [NSMutableArray arrayWithObjects:@"Query structure is improper!!",nil];
        SystemException *sys = [SystemException withContext:@"MobileObjectDatabase" method:@"query" parameters:params];
        @throw sys;
    }
    
    //Now get possible matches of records for each expression
    NSMutableSet *result = [NSMutableSet set];
    for(LogicExpression *courExpr in expressions)
    {
        [chain addExpression:courExpr];
        
        //get beans that match this expression
        NSSet *matchedBeans = [self logicExpressionBeans:channel :courExpr];
        if(matchedBeans != nil)
        {
            [result unionSet:matchedBeans];
        }
    }
    
    Query *query = [Query createInstance:chain];
    NSSet *queryResults = [query executeQuery:result];
    
    return queryResults;
}

-(NSSet *)logicExpressionBeans:(NSString *) channel :(LogicExpression *) expression
{
    NSMutableSet *result = [NSMutableSet set];
    
    NSString *lhs = expression.lhs;
    NSString *rhs = expression.rhs;
    
    //Get the Storage Context
    NSManagedObjectContext *managedContext = [[CloudDBManager getInstance] storageContext];
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"PersistentMobileObject" 
                                              inManagedObjectContext:managedContext];
    
    //Get an instance if its already been provisioned
    NSFetchRequest *request = [[[NSFetchRequest alloc] init] autorelease];
    [request setEntity:entity];
    
    NSString *lhsExpression = [NSString stringWithFormat:@"name=%@",lhs];
    NSString *rhsExpression = [NSString stringWithFormat:@"value=%@",rhs];
    
    NSArray *stored = nil;
    if(expression.op == OP_EQUALS || expression.op == OP_NOT_EQUALS)
    {
        if(expression.op == OP_EQUALS)
        {
            //EqualToQuery
            NSPredicate *predicate = [NSPredicate predicateWithFormat:@"(service == %@) AND (nameValuePairs contains %@) AND (nameValuePairs contains %@)",channel, lhsExpression, rhsExpression];
            [request setPredicate:predicate];
        }
        else if(expression.op == OP_NOT_EQUALS)
        {
            //NotEqualToQuery
            NSPredicate *predicate = [NSPredicate predicateWithFormat:@"(service == %@) AND (nameValuePairs contains %@) AND (NONE nameValuePairs contains %@)",channel, lhsExpression, rhsExpression];  
            [request setPredicate:predicate];
        }
        
        stored = [managedContext executeFetchRequest:request error:NULL];
        
        //filter by predicate
        if(stored != nil)
        {
            for(PersistentMobileObject *local in stored)
            {
                [result addObject:[local parseMobileObject]];
            }
        }
    }
    else
    {
        //In case of LIKE and CONTAINS queries
        NSArray *all = [self readAll:channel];
        for(MobileObject *local in all)
        {
            [result addObject:local];
        }
    }

    return result;
}

-(BOOL) isBooted:(NSString *)channel
{
    //Get the Storage Context
    NSManagedObjectContext *managedContext = [[CloudDBManager getInstance] storageContext];
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"PersistentMobileObject" 
                                              inManagedObjectContext:managedContext];
    
    //Get an instance if its already been provisioned
    NSFetchRequest *request = [[[NSFetchRequest alloc] init] autorelease];
    [request setEntity:entity];
    
    //Set the cursor size
    [request setFetchBatchSize:1];
    
    NSSortDescriptor *descriptor = [[NSSortDescriptor alloc] initWithKey:@"oid" ascending:YES];
    NSArray *descriptors = [[NSArray alloc] initWithObjects:descriptor, nil];
    [request setSortDescriptors:descriptors];
    [descriptor release];
    [descriptors release];
    
    //Set the predicate
    //find by channel
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"(service == %@)",channel];
    [request setPredicate:predicate];
    
    NSFetchedResultsController *cursor =
    [[NSFetchedResultsController alloc] initWithFetchRequest:request
    managedObjectContext:managedContext sectionNameKeyPath:nil
    cacheName:nil];
    cursor = [cursor autorelease];
    
    [cursor performFetch:NULL];
    //NSLog(success ? @"Success: Yes" : @"Success: No");
    
    NSArray *results = cursor.fetchedObjects;
    if(results != nil && [results count]>0)
    {
        return YES;
    }
    
    return NO;
}

-(NSFetchedResultsController *)searchExactMatchAND:(NSString *)channel :(GenericAttributeManager *)criteria
{
    //Get the Storage Context
    NSManagedObjectContext *managedContext = [[CloudDBManager getInstance] storageContext];
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"PersistentMobileObject" 
                                              inManagedObjectContext:managedContext];
    
    //Get an instance if its already been provisioned
    NSFetchRequest *request = [[[NSFetchRequest alloc] init] autorelease];
    [request setEntity:entity];
    
    //Set the cursor size
    [request setFetchBatchSize:1];
    
    NSSortDescriptor *descriptor = [[NSSortDescriptor alloc] initWithKey:@"service" ascending:NO];
    NSArray *descriptors = [[NSArray alloc] initWithObjects:descriptor, nil];
    [request setSortDescriptors:descriptors];
    [descriptor release];
    [descriptors release];
    
    //Do some predicate magic here
    NSMutableArray *channelPredicate = [NSMutableArray array];
    NSMutableArray *criteriaPredicate = [NSMutableArray array];
    
    //Channel Predicate
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"(service == %@)",channel];
    [channelPredicate addObject:predicate];
    
    if(criteria != nil)
    {
        NSArray *names = [criteria getNames];
        for(NSString *name in names)
        {
            NSString *value = [criteria getAttribute:name];
            NSPredicate *nameValuePredicate = [NSPredicate predicateWithFormat:@"(nameValuePairs contains %@) AND (nameValuePairs contains %@)",name,value];
            [criteriaPredicate addObject:nameValuePredicate];
        }
    }
    
    NSPredicate *channelCompound = [NSCompoundPredicate andPredicateWithSubpredicates:[NSArray arrayWithArray:channelPredicate]];
    NSPredicate *criteriaCompound = [NSCompoundPredicate andPredicateWithSubpredicates:[NSArray arrayWithArray:criteriaPredicate]];
    
    NSMutableArray *all = [NSMutableArray array];
    [all addObject:channelCompound];
    [all addObject:criteriaCompound];
    NSArray *allPredicates = [NSArray arrayWithArray:all];
    NSPredicate *finalPredicate = [NSCompoundPredicate andPredicateWithSubpredicates:allPredicates];
    
    [request setPredicate:finalPredicate];
    
    NSFetchedResultsController *cursor =
    [[NSFetchedResultsController alloc] initWithFetchRequest:request
                                        managedObjectContext:managedContext sectionNameKeyPath:nil
                                                   cacheName:nil];
    cursor = [cursor autorelease];
    
    //Now perform fetch
    NSError *error;
    BOOL success = [cursor performFetch:&error];
    if(success)
    {
        return cursor;
    }
    else
    {
        NSLog(@"SearchExactMatchAND Error: %@",[error userInfo]);
    }

    return nil;
}

-(NSFetchedResultsController *)searchExactMatchOR:(NSString *)channel :(GenericAttributeManager *)criteria
{
    //Get the Storage Context
    NSManagedObjectContext *managedContext = [[CloudDBManager getInstance] storageContext];
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"PersistentMobileObject" 
                                              inManagedObjectContext:managedContext];
    
    //Get an instance if its already been provisioned
    NSFetchRequest *request = [[[NSFetchRequest alloc] init] autorelease];
    [request setEntity:entity];
    
    //Set the cursor size
    [request setFetchBatchSize:1];
    
    NSSortDescriptor *descriptor = [[NSSortDescriptor alloc] initWithKey:@"service" ascending:NO];
    NSArray *descriptors = [[NSArray alloc] initWithObjects:descriptor, nil];
    [request setSortDescriptors:descriptors];
    [descriptor release];
    [descriptors release];
    
    //Do some predicate magic here
    NSMutableArray *channelPredicate = [NSMutableArray array];
    NSMutableArray *criteriaPredicate = [NSMutableArray array];
    
    //Channel Predicate
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"(service == %@)",channel];
    [channelPredicate addObject:predicate];
    
    if(criteria != nil)
    {
        NSArray *names = [criteria getNames];
        for(NSString *name in names)
        {
            NSString *value = [criteria getAttribute:name];
            NSPredicate *nameValuePredicate = [NSPredicate predicateWithFormat:@"(nameValuePairs contains %@) AND (nameValuePairs contains %@)",name,value];
            [criteriaPredicate addObject:nameValuePredicate];
        }
    }
    
    NSPredicate *channelCompound = [NSCompoundPredicate andPredicateWithSubpredicates:[NSArray arrayWithArray:channelPredicate]];
    NSPredicate *criteriaCompound = [NSCompoundPredicate orPredicateWithSubpredicates:[NSArray arrayWithArray:criteriaPredicate]];
    
    NSMutableArray *all = [NSMutableArray array];
    [all addObject:channelCompound];
    [all addObject:criteriaCompound];
    NSArray *allPredicates = [NSArray arrayWithArray:all];
    NSPredicate *finalPredicate = [NSCompoundPredicate andPredicateWithSubpredicates:allPredicates];
    
    [request setPredicate:finalPredicate];
    
    NSFetchedResultsController *cursor =
    [[NSFetchedResultsController alloc] initWithFetchRequest:request
                                        managedObjectContext:managedContext sectionNameKeyPath:nil
                                                   cacheName:nil];
    cursor = [cursor autorelease];
    
    //Now perform fetch
    NSError *error;
    BOOL success = [cursor performFetch:&error];
    if(success)
    {
        return cursor;
    }
    else
    {
        NSLog(@"SearchExactMatchOR Error: %@",[error userInfo]);
    }
    
    return nil;  
}

-(NSFetchedResultsController *)readByName:(NSString *)channel :(NSString *)name :(BOOL) ascending
{
    //Get the Storage Context
    NSManagedObjectContext *managedContext = [[CloudDBManager getInstance] storageContext];
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"MetaData" 
                                              inManagedObjectContext:managedContext];
    
    //Get an instance if its already been provisioned
    NSFetchRequest *request = [[[NSFetchRequest alloc] init] autorelease];
    [request setEntity:entity];
    
    //Set the cursor size
    [request setFetchBatchSize:1];
    
    NSSortDescriptor *descriptor = [[NSSortDescriptor alloc] initWithKey:@"value" ascending:ascending];
    NSArray *descriptors = [[NSArray alloc] initWithObjects:descriptor, nil];
    [request setSortDescriptors:descriptors];
    [descriptor release];
    [descriptors release];
    
    NSPredicate *channelPredicate = [NSPredicate predicateWithFormat:@"(parent.service == %@)",channel];
    NSPredicate *namePredicate = [NSPredicate predicateWithFormat:@"(name == %@)",name];
    NSArray *predicates = [NSArray arrayWithObjects:channelPredicate,namePredicate,nil];
    
    NSPredicate *finalPredicate = [NSCompoundPredicate andPredicateWithSubpredicates:predicates];
    
    [request setPredicate:finalPredicate];
    
    NSFetchedResultsController *cursor =
    [[NSFetchedResultsController alloc] initWithFetchRequest:request
                                        managedObjectContext:managedContext sectionNameKeyPath:nil
                                                   cacheName:nil];
    cursor = [cursor autorelease];
    
    //Now perform fetch
    NSError *error;
    BOOL success = [cursor performFetch:&error];
    if(success)
    {
        return cursor;
    }
    else
    {
        NSLog(@"readByNameAndSort Error: %@",[error userInfo]);
    }

    
    return nil;
}

-(NSFetchedResultsController *)readByName:(NSString *)channel :(NSString *)name
{
    //Get the Storage Context
    NSManagedObjectContext *managedContext = [[CloudDBManager getInstance] storageContext];
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"MetaData" 
                                              inManagedObjectContext:managedContext];
    
    //Get an instance if its already been provisioned
    NSFetchRequest *request = [[[NSFetchRequest alloc] init] autorelease];
    [request setEntity:entity];
    
    //Set the cursor size
    [request setFetchBatchSize:1];
    
    NSSortDescriptor *descriptor = [[NSSortDescriptor alloc] initWithKey:@"name" ascending:YES];
    NSArray *descriptors = [[NSArray alloc] initWithObjects:descriptor, nil];
    [request setSortDescriptors:descriptors];
    [descriptor release];
    [descriptors release];
    
    NSPredicate *channelPredicate = [NSPredicate predicateWithFormat:@"(parent.service == %@)",channel];
    NSPredicate *namePredicate = [NSPredicate predicateWithFormat:@"(name == %@)",name];
    NSArray *predicates = [NSArray arrayWithObjects:channelPredicate,namePredicate,nil];
    
    NSPredicate *finalPredicate = [NSCompoundPredicate andPredicateWithSubpredicates:predicates];
    
    [request setPredicate:finalPredicate];
    
    NSFetchedResultsController *cursor =
    [[NSFetchedResultsController alloc] initWithFetchRequest:request
                                        managedObjectContext:managedContext sectionNameKeyPath:nil
                                                   cacheName:nil];
    cursor = [cursor autorelease];
    
    //Now perform fetch
    NSError *error;
    BOOL success = [cursor performFetch:&error];
    if(success)
    {
        return cursor;
    }
    else
    {
        NSLog(@"readByName Error: %@",[error userInfo]);
    }
    
    
    return nil;
}

-(NSFetchedResultsController *)readProxyCursor:(NSString *)channel
{
    //Get the Storage Context
    NSManagedObjectContext *managedContext = [[CloudDBManager getInstance] storageContext];
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"PersistentMobileObject" 
                                              inManagedObjectContext:managedContext];
    
    //Get an instance if its already been provisioned
    NSFetchRequest *request = [[[NSFetchRequest alloc] init] autorelease];
    [request setEntity:entity];
    
    //Set the cursor size
    [request setFetchBatchSize:1];
    
    NSSortDescriptor *descriptor = [[NSSortDescriptor alloc] initWithKey:@"service" ascending:NO];
    NSArray *descriptors = [[NSArray alloc] initWithObjects:descriptor, nil];
    [request setSortDescriptors:descriptors];
    [descriptor release];
    [descriptors release];
    
    //Channel Predicate
    NSPredicate *channelPredicate = [NSPredicate predicateWithFormat:@"(service == %@)",channel];
    NSPredicate *proxyPredicate = [NSPredicate predicateWithFormat:@"(proxy == %@)",[NSNumber numberWithBool:YES]];
    
    NSArray *predicates = [NSArray arrayWithObjects:channelPredicate,proxyPredicate,nil];
    NSPredicate *finalPredicate = [NSCompoundPredicate andPredicateWithSubpredicates:predicates];
    [request setPredicate:finalPredicate];
    
    NSFetchedResultsController *cursor =
    [[NSFetchedResultsController alloc] initWithFetchRequest:request
                                        managedObjectContext:managedContext sectionNameKeyPath:nil
                                                   cacheName:nil];
    cursor = [cursor autorelease];
    
    //Now perform fetch
    NSError *error;
    BOOL success = [cursor performFetch:&error];
    if(success)
    {
        return cursor;
    }
    else
    {
        NSLog(@"readProxyCursor Error: %@",[error userInfo]);
    }
    
    return nil;
}

-(NSFetchedResultsController *)readByNameValuePair:(NSString *)channel :(NSString *)name :(NSString *)value
{
    //Get the Storage Context
    NSManagedObjectContext *managedContext = [[CloudDBManager getInstance] storageContext];
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"MetaData" 
                                              inManagedObjectContext:managedContext];
    
    //Get an instance if its already been provisioned
    NSFetchRequest *request = [[[NSFetchRequest alloc] init] autorelease];
    [request setEntity:entity];
    
    //Set the cursor size
    [request setFetchBatchSize:1];
    
    NSSortDescriptor *descriptor = [[NSSortDescriptor alloc] initWithKey:@"name" ascending:YES];
    NSArray *descriptors = [[NSArray alloc] initWithObjects:descriptor, nil];
    [request setSortDescriptors:descriptors];
    [descriptor release];
    [descriptors release];
    
    NSPredicate *channelPredicate = [NSPredicate predicateWithFormat:@"(parent.service == %@)",channel];
    NSPredicate *namePredicate = [NSPredicate predicateWithFormat:@"(name == %@)",name];
    NSPredicate *valuePredicate = [NSPredicate predicateWithFormat:@"(value == %@)",value];
    NSArray *predicates = [NSArray arrayWithObjects:channelPredicate,namePredicate,valuePredicate,nil];
    
    NSPredicate *finalPredicate = [NSCompoundPredicate andPredicateWithSubpredicates:predicates];
    
    [request setPredicate:finalPredicate];
    
    NSFetchedResultsController *cursor =
    [[NSFetchedResultsController alloc] initWithFetchRequest:request
                                        managedObjectContext:managedContext sectionNameKeyPath:nil
                                                   cacheName:nil];
    cursor = [cursor autorelease];
    
    //Now perform fetch
    NSError *error;
    BOOL success = [cursor performFetch:&error];
    if(success)
    {
        return cursor;
    }
    else
    {
        NSLog(@"readByNameValuePair Error: %@",[error userInfo]);
    }
    
    
    return nil; 
}
@end
