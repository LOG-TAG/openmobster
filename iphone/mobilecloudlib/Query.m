//
//  Query.m
//  mobilecloudlib
//
//  Created by openmobster on 8/9/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import "Query.h"
#import "SystemException.h"
#import "LogicExpression.h"
#import "LogicChain.h"
#import "StringUtil.h"


@implementation Query

@synthesize logic;

+(Query *)createInstance:(LogicChain *)logic
{
    if(logic == nil)
    {
        NSMutableArray *params = [NSMutableArray arrayWithObjects:@"LogicChain must be specified!!",nil];
        SystemException *sys = [SystemException withContext:@"Query" method:@"createInstance" parameters:params];
        @throw sys;
    }
    
    Query *instance = [[Query alloc] init];
    instance = [instance autorelease];
    
    instance.logic = logic;
    
    return instance;
}

-(NSSet *)executeQuery:(NSSet *)mobileObjects
{
    NSMutableSet *result = [NSMutableSet set];
    
    if(mobileObjects != nil && [mobileObjects count] >0)
    {
        for(MobileObject *local in mobileObjects)
        {
            if([self isMatched:local])
            {
                [result addObject:local];
            }
        }
    }
    
    return result;
}

-(BOOL)isMatched:(MobileObject *) object
{
    NSArray *expressions = self.logic.chain;
    
    int size = [expressions count];
    int exprMatchCount = 0;
    
    for(LogicExpression *expression in expressions)
    {
        NSString *lhsValue = [object getValue:expression.lhs];
        NSString *rhsValue = expression.rhs;
        
        //Evaluate the expression
        if([self evalExpression:lhsValue :rhsValue :expression.op])
        {
            exprMatchCount++;
        }
    }
    
    return [self evalLogicChain:self.logic.logicLink :exprMatchCount :size];
}

-(BOOL)evalExpression:(NSString *) lhsValue :(NSString *) rhsValue :(int) op
{
    switch(op)
    {
        case OP_EQUALS:
            if(lhsValue != nil && [lhsValue isEqualToString:rhsValue])
            {
                return YES;
            }
			break;
			
        case OP_NOT_EQUALS:
            if(lhsValue != nil && ![lhsValue isEqualToString:rhsValue])
            {
                return YES;
            }
			break;
			
        case OP_LIKE:
            if(lhsValue != nil && [lhsValue hasPrefix:rhsValue])
            {
                return YES;
            }
			break;
			
        case OP_CONTAINS:
            if(lhsValue != nil && [StringUtil indexOf:lhsValue :rhsValue] != -1)
            {
                return YES;
            }
			break;
			
        default:				
			break;
    }
    return NO;
}

-(BOOL)evalLogicChain:(int) logicLink :(int) exprMatchCount :(int) totalExprCount
{
    switch (logicLink) 
    {
        case AND:
            if(exprMatchCount == totalExprCount)
            {
                return YES;
            }	
            break;
            
        case OR:
            if(exprMatchCount > 0)
            {
                return true;
            }
            break;
            
        default:
            break;
    }
    
    return NO;
}

@end
