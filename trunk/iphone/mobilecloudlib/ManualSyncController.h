//
//  ManualSyncController.h
//  mobilecloudlib
//
//  Created by openmobster on 8/25/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ManualSyncMenuController.h"

@interface ManualSyncController : UIViewController<UITableViewDataSource,UITableViewDelegate>
{
    @private
    UIViewController *delegate;
    
    ManualSyncMenuController *menu;
    
    NSArray *myChannels;
}

@property (nonatomic,retain) UIViewController *delegate;
@property (nonatomic,retain) ManualSyncMenuController *menu;
@property (nonatomic,retain) NSArray *myChannels;

-(IBAction) cancel:(id) sender;
@end
