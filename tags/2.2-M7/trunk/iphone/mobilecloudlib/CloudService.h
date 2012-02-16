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

/**
 *   CloudService must be started and stopped to access any services of the system.
 *   It controls all the kernels and must be started before any requests can be made to 
 *   the OpenMobster service on the device
 *
 *   @author openmobster@gmail.com
 */
@interface CloudService : NSObject<UIAlertViewDelegate> 
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

/**
 * Gets a service that will be used in a non-gui environment
 *
 * @return the CloudService
 */
+(CloudService *) getInstance;

/**
 * Gets a service that will be used in a GUI environment
 *
 * @param viewController the view controller that will be used to bring up a Modal activation screen if needed
 * @return the CloudService
 */
+(CloudService *) getInstance:(UIViewController *)viewController;

/**
 *  Start the Cloud Service. This starts all the kernels. This must be called before OpenMobster services can
 *  be accessed
 */
-(void) startup;

/**
 * Shuts down the Cloud Service and all its kernels
 */
-(void) shutdown;
@end
