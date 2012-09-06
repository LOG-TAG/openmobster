/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import <UIKit/UIKit.h>

@interface CloudManagerAppDelegate : NSObject <UIApplicationDelegate> 
{
	@private
    UIWindow *window;
	UINavigationController *mainView;
}

@property (nonatomic, retain) IBOutlet UIWindow *window;
@property (nonatomic, retain) IBOutlet UINavigationController *mainView;

-(void)startCloudService;
-(void)stopCloudService;
-(void)sync;
-(void)startActivation;
-(IBAction)launchCloudManager:(id)sender;
@end

