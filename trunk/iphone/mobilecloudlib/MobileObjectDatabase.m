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
        result = [self readAll:channel];
    }

    return result;
}
@end
