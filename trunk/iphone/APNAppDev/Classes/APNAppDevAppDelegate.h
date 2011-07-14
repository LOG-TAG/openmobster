//
//  APNAppDevAppDelegate.h
//  APNAppDev
//
//  Created by openmobster on 4/21/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>

@class APNAppDevViewController;

@interface APNAppDevAppDelegate : NSObject <UIApplicationDelegate> {
    UIWindow *window;
    APNAppDevViewController *viewController;
}

@property (nonatomic, retain) IBOutlet UIWindow *window;
@property (nonatomic, retain) IBOutlet APNAppDevViewController *viewController;

@end

