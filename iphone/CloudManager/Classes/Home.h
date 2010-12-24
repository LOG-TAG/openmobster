//
//  Home.h
//  CloudManager
//
//  Created by openmobster on 12/22/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ModalViewLauncherDelegate.h"
#import "ModalActivateDevice.h"
#import "SecurityConfig.h"
#import "AsyncSubmit.h"

@interface Home : UIViewController<UITableViewDataSource,UITableViewDelegate,ModalViewLauncherDelegate> 
{
}

-(IBAction) menu:(id) sender;
@end
