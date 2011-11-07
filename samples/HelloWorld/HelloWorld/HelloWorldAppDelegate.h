//
//  HelloWorldAppDelegate.h
//  HelloWorld
//
//  Created by openmobster on 11/7/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface HelloWorldAppDelegate : NSObject <UIApplicationDelegate> 
{
    @private
    
    //The view objects
    UILabel *mobileos;
    UILabel *desktopos;
    
    //The model objects
    NSMutableArray *mobile;
    NSMutableArray *desktop;
    int mobilePtr;
    int desktopPtr;
}

//The Labels are wired up to instances in the controller here
@property (nonatomic,retain) IBOutlet UIWindow *window;
@property (nonatomic,retain) IBOutlet UILabel *mobileos;
@property (nonatomic,retain) IBOutlet UILabel *desktopos;

//These hold the data to be used in the App
@property (nonatomic,retain) NSMutableArray *mobile;
@property (nonatomic,retain) NSMutableArray *desktop;

//Buttons are wired to these methods
-(IBAction)nextMobileOs:(id)sender;
-(IBAction)nextDesktopOs:(id)sender;

@end
