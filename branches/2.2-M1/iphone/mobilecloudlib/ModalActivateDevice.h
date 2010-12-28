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

@interface ModalActivateDevice : UIViewController<UITableViewDataSource,UITableViewDelegate,UICommandDelegate> 
{
	@private
	id<ModalViewLauncherDelegate> delegate;
}

@property(nonatomic,retain) id<ModalViewLauncherDelegate> delegate;

-(IBAction) cancel:(id) sender;
-(IBAction) submit:(id) sender;
@end
