//
//  ModalActivateDevice.h
//  CloudManager
//
//  Created by openmobster on 12/22/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ModalViewLauncherDelegate.h"
#import "AsyncSubmit.h"

@interface ModalActivateDevice : UIViewController<UITableViewDataSource,UITableViewDelegate> 
{
	@private
	id<ModalViewLauncherDelegate> delegate;
}

@property(nonatomic,retain) id<ModalViewLauncherDelegate> delegate;

-(IBAction) cancel:(id) sender;
-(IBAction) submit:(id) sender;

-(void)asyncCallback;
@end
