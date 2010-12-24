//
//  CloudManagerAppDelegate.h
//  CloudManager
//
//  Created by openmobster on 8/26/10.
//  Copyright __MyCompanyName__ 2010. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Kernel.h"

@interface CloudManagerAppDelegate : NSObject <UIApplicationDelegate> 
{
	@private
    UIWindow *window;
	UINavigationController *mainView;
}

@property (nonatomic, retain) IBOutlet UIWindow *window;
@property (nonatomic, retain) IBOutlet UINavigationController *mainView;

@end

