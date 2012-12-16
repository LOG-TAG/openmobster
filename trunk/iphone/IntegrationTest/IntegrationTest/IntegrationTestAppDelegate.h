//
//  IntegrationTestAppDelegate.h
//  IntegrationTest
//
//  Created by openmobster on 8/12/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "IntegrationTestViewController.h"
#import "CloudService.h"

@interface IntegrationTestAppDelegate : NSObject <UIApplicationDelegate> 
{
    @private
    IntegrationTestViewController *viewController;
}

@property (nonatomic, retain) IBOutlet UIWindow *window;
@property (nonatomic,retain) IBOutlet IntegrationTestViewController *viewController;

-(void)startCloudService;
-(void)stopCloudService;
-(void)sync;
-(void)startActivation;
@end
