//
//  LogicExpression.m
//  mobilecloudlib
//
//  Created by openmobster on 8/8/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import "LogicExpression.h"
#import "SystemException.h"

//int const OP_EQUALS = 0;
//int const OP_NOT_EQUALS = 1;
//int const OP_LIKE = 2;
//int const OP_CONTAINS = 3;

@implementation LogicExpression

@synthesize lhs;
@synthesize rhs;
@synthesize op;

+(LogicExpression *)withInit:(NSString *) lhs :(NSString *) rhs :(int) op
{
    if(lhs == nil)
    {
        NSMutableArray *params = [NSMutableArray arrayWithObjects:@"LHS portion of the expression must be specified!!",nil];
        SystemException *sys = [SystemException withContext:@"LogicExpression" method:@"withInit" parameters:params];
        @throw sys;
    }
    if(rhs == nil)
    {
        NSMutableArray *params = [NSMutableArray arrayWithObjects:@"RHS portion of the expression must be specified!!",nil];
        SystemException *sys = [SystemException withContext:@"LogicExpression" method:@"withInit" parameters:params];
        @throw sys;
    }
    if(op != OP_EQUALS && op != OP_NOT_EQUALS && op != OP_LIKE && op != OP_CONTAINS)
    {
        NSMutableArray *params = [NSMutableArray arrayWithObjects:@"Specified Operation is unsupported!!",nil];
        SystemException *sys = [SystemException withContext:@"LogicExpression" method:@"withInit" parameters:params];
        @throw sys;  
    }
    
    LogicExpression *instance = [[LogicExpression alloc] init];
    instance = [instance autorelease];
    
    instance.lhs = lhs;
    instance.rhs = rhs;
    instance.op = op;
    
    return instance;
}

@end
