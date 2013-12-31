//
//  MetaData.m
//  mobilecloudlib
//
//  Created by openmobster on 9/23/13.
//  Copyright (c) 2013 __MyCompanyName__. All rights reserved.
//

#import "MetaData.h"

@implementation MetaData

@dynamic parent;
@dynamic name;
@dynamic value;

/*
-(BOOL) isEqual:(id)obj
{
    BOOL result = NO;
    
    if([obj isKindOfClass:[MetaData class]])
    {
        MetaData *rhs = (MetaData *)obj;
        if([self.parent.oid isEqualToString:rhs.parent.oid] && [self.name isEqualToString:rhs.name])
        {
            result = YES;
        }
    }
    
    return result;
}

-(NSUInteger) hash
{
    NSString *hashStr = [NSString stringWithFormat:@"%@|%@",self.parent.oid,self.name];
    return [hashStr hash];
}
*/
@end
