//
//  BootupKernel.h
//  mobilecloudlib
//
//  Created by openmobster on 5/25/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>


@interface BootupKernel : NSObject 
{

}

+(BootupKernel *)getInstance;

-(void)startup;
-(void)shutdown;
-(BOOL)isRunning;

@end
