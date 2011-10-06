//
//  LogicChain.h
//  mobilecloudlib
//
//  Created by openmobster on 8/9/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "LogicExpression.h"

//extern int const AND;
//extern int const OR;

typedef enum linkType {
    AND = 0,
    OR = 1
}LINK_TYPE;

@interface LogicChain : NSObject 
{
   @private
    NSMutableArray *chain;
    int logicLink;
}

@property (nonatomic,assign)NSMutableArray *chain;
@property (nonatomic,assign)int logicLink;

-(id) init;

+(LogicChain *)createANDChain;
+(LogicChain *)createORChain;

-(void) addExpression:(LogicExpression *)expression;

@end
