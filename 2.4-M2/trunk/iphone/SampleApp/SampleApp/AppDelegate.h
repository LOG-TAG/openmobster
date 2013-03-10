//
//  AppDelegate.h
//  SampleApp
//
//  Created by openmobster on 8/31/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>

@class ViewController;

@interface AppDelegate : UIResponder <UIApplicationDelegate>
{
    @private
    UINavigationController *navigationController;
    BOOL pushRegistered;
}

@property (strong, nonatomic) UIWindow *window;

@property (strong, nonatomic) ViewController *viewController;

@property(strong, nonatomic)UINavigationController *navigationController;

@property(nonatomic,assign)BOOL pushRegistered;

-(void)startCloudService;
-(void)stopCloudService;
-(void)sync;
-(void)startActivation;
@end
