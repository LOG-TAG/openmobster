/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "CommandService.h"
#import "CloudManager.h"

/**
 * 
 * @author openmobster@gmail.com
 */
@interface UIKernel : NSObject 
{
}


+(UIKernel *)getInstance;

-(void)startup;
-(void)shutdown;
-(BOOL)isRunning;


-(void)forceActivation:(UIViewController *)caller;
-(void)launchCloudManager:(UIViewController *)caller;
-(void)launchDeviceActivation:(UIViewController *)caller;

@end
