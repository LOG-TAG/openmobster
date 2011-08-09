//
//  LogicChain.m
//  mobilecloudlib
//
//  Created by openmobster on 8/9/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import "LogicChain.h"
#import "SystemException.h"

//int const AND = 0;
//int const OR = 1;

@implementation LogicChain

@synthesize chain;
@synthesize logicLink;

-(id) init
{
    if(self = [super init])
	{
		self.chain = [NSMutableArray array];
	}
	
	return self;
}

+(LogicChain *)createANDChain
{
    LogicChain *instance = [[LogicChain alloc] init];
    instance = [instance autorelease];
    
    instance.logicLink = AND;
    
    return instance;
}

+(LogicChain *)createORChain
{
    LogicChain *instance = [[LogicChain alloc] init];
    instance = [instance autorelease];
    
    instance.logicLink = OR;
    
    return instance;
}

-(void) addExpression:(LogicExpression *)expression
{
    if(expression == nil)
    {
        NSMutableArray *params = [NSMutableArray arrayWithObjects:@"LogicExpression must be specified!!",nil];
        SystemException *sys = [SystemException withContext:@"LogicChain" method:@"addExpression" parameters:params];
        @throw sys;
    }
    
    [self.chain addObject:expression];
}

@end
