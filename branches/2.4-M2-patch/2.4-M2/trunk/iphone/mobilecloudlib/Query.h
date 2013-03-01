//
//  Query.h
//  mobilecloudlib
//
//  Created by openmobster on 8/9/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "LogicChain.h"
#import "LogicExpression.h"
#import "MobileObject.h"


@interface Query : NSObject 
{
    @private
    LogicChain *logic;
}

@property (nonatomic,assign)LogicChain *logic;

+(Query *)createInstance:(LogicChain *)logic;

-(NSSet *)executeQuery:(NSSet *)mobileObjects;

-(BOOL)isMatched:(MobileObject *) object;

-(BOOL)evalExpression:(NSString *) lhsValue :(NSString *) rhsValue :(int) op;

-(BOOL)evalLogicChain:(int) logicLink :(int) exprMatchCount :(int) totalExprCount;
@end
