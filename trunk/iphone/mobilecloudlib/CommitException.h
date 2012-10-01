//
//  CommitException.h
//  mobilecloudlib
//
//  Created by openmobster on 10/1/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>


@interface CommitException : NSException 
{
    
}

-initWithException:(NSException *)exception;
+withException:(NSException *)exception;

@end
