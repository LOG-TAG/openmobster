//
//  CloudService.h
//  mobilecloudlib
//
//  Created by openmobster on 7/4/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Kernel.h"
#import "BootupKernel.h"
#import "UIKernel.h"

@interface CloudService : NSObject 
{
	@private
	Kernel *kernel;
	BootupKernel *bootupKernel;
	UIKernel *uiKernel;
	UIViewController *viewController;
}

@property(nonatomic,retain) Kernel *kernel;
@property(nonatomic,retain) BootupKernel *bootupKernel;
@property(nonatomic,retain) UIKernel *uiKernel;
@property(nonatomic,retain) UIViewController *viewController;

+(CloudService *) getInstance;
+(CloudService *) getInstance:(UIViewController *)viewController;

-(void) startup;

-(void) shutdown;
@end
