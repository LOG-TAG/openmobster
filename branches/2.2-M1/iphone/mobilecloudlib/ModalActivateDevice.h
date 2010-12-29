/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import <UIKit/UIKit.h>
#import "ModalViewLauncherDelegate.h"
#import "CommandContext.h"
#import "CommandService.h"
#import "UICommandDelegate.h"
#import "ActivateDevice.h"
#import "Configuration.h"

@interface ModalActivateDevice : UIViewController<UITableViewDataSource,UITableViewDelegate,UICommandDelegate> 
{
	@private
	UIViewController *delegate;
	BOOL forceActivation;
}

@property(nonatomic,retain) UIViewController *delegate;

-(void)forceActivation;

-(IBAction) cancel:(id) sender;
-(IBAction) submit:(id) sender;

//use internally only
-(void)dismiss;
@end
