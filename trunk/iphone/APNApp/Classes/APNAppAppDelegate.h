//
//  APNAppAppDelegate.h
//  APNApp
//
//  Created by openmobster on 4/7/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>

@class APNAppViewController;

@interface APNAppAppDelegate : NSObject <UIApplicationDelegate> {
    UIWindow *window;
    APNAppViewController *viewController;
}

@property (nonatomic, retain) IBOutlet UIWindow *window;
@property (nonatomic, retain) IBOutlet APNAppViewController *viewController;

@end

