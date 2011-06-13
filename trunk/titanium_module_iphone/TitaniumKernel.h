//
//  TitaniumKernel.h
//  titanium_module_iphone
//
//  Created by openmobster on 5/31/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>


@interface TitaniumKernel : NSObject 
{
	@private
	BOOL isRunning;
}

+(TitaniumKernel *) withInit;

+(TitaniumKernel *) getInstance;

+(void) startTx;

+(void) startApp;

-(void) start;

-(void) stop;

@end
