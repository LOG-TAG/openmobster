//
//  SyncAppAppDelegate.h
//  SyncApp
//
//  Created by openmobster on 3/31/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>

@class SyncAppViewController;

@interface SyncAppAppDelegate : NSObject <UIApplicationDelegate> {
    UIWindow *window;
    SyncAppViewController *viewController;
}

@property (nonatomic, retain) IBOutlet UIWindow *window;
@property (nonatomic, retain) IBOutlet SyncAppViewController *viewController;

-(void) startCloudService;
-(void) stopCloudService;

@end

