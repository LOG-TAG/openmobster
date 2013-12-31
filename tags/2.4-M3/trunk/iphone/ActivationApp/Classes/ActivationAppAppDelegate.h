//
//  ActivationAppAppDelegate.h
//  ActivationApp
//
//  Created by openmobster on 2/28/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Kernel.h"
#import "UIKernel.h"
#import "AppService.h"

@class ActivationAppViewController;

@interface ActivationAppAppDelegate : NSObject <UIApplicationDelegate> {
    UIWindow *window;
    ActivationAppViewController *viewController;
}

@property (nonatomic, retain) IBOutlet UIWindow *window;
@property (nonatomic, retain) IBOutlet ActivationAppViewController *viewController;

-(void)startCloudService;
-(void)stopCloudService;

@end

