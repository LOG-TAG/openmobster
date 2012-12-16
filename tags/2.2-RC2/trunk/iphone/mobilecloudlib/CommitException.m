//
//  CommitException.m
//  mobilecloudlib
//
//  Created by openmobster on 10/1/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import "CommitException.h"
#import "SystemException.h"


@implementation CommitException

-initWithException:(NSException *)exception
{
    NSString *reason = exception.reason;
    
    if([exception isKindOfClass:[SystemException class]])
    {
        SystemException *systemException = (SystemException *)exception;
        reason = [systemException getMessage];
    }
	
    
    self = [self initWithName:@"CommitException" reason:reason userInfo:nil];
    
    
	return self; 
}

+(id) withException:(NSException *)exception;
{
    CommitException *instance = [[CommitException alloc] initWithException:exception];
    return [instance autorelease];
}

@end
