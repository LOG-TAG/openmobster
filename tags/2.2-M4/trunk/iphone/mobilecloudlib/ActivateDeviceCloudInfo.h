/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import <UIKit/UIKit.h>
#import "CommandContext.h"
#import "CommandService.h"
#import "UICommandDelegate.h"
#import "ActivateDevice.h"
#import "Configuration.h"
#import "AppException.h"
#import "StringUtil.h"


@interface ActivateDeviceCloudInfo : UIViewController<UITableViewDataSource,UITableViewDelegate,UICommandDelegate> 
{
	@private
	UIViewController *delegate;
	BOOL forceActivation;
	
	//Form fields
	UITextField *cloudIp;
	UITextField *cloudPort;
	CommandContext *commandContext;
}

@property(nonatomic,retain) UIViewController *delegate;
@property(nonatomic,retain) UITextField *cloudIp;
@property(nonatomic,retain) UITextField *cloudPort;
@property(nonatomic,retain) CommandContext *commandContext;
@property(nonatomic,assign) BOOL forceActivation;

-(IBAction) submit:(id) sender;

//use internally only
-(void)dismiss;
@end
