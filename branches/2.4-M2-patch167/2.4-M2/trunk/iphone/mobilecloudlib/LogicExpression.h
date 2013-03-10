//
//  LogicExpression.h
//  mobilecloudlib
//
//  Created by openmobster on 8/8/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>

//extern int const OP_EQUALS;
//extern int const OP_NOT_EQUALS;
//extern int const OP_LIKE;
//extern int const OP_CONTAINS;

typedef enum operationType {
    OP_EQUALS = 0,
    OP_NOT_EQUALS = 1,
    OP_LIKE = 2,
    OP_CONTAINS = 3
}OPERATION_TYPE;

@interface LogicExpression : NSObject 
{
   @private
    NSString *lhs;
    NSString *rhs;
    int op;
}

@property (nonatomic,assign)NSString *lhs;
@property (nonatomic,assign)NSString *rhs;
@property (nonatomic,assign)int op;

+(LogicExpression *)withInit:(NSString *) lhs :(NSString *) rhs :(int) op;

@end
