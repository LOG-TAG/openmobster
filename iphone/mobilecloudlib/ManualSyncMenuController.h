//
//  ManualSyncMenuController.h
//  mobilecloudlib
//
//  Created by openmobster on 8/25/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Channel.h"
#import "UICommandDelegate.h"

@interface ManualSyncMenuController : UIViewController<UITableViewDataSource,UITableViewDelegate,UICommandDelegate>
{
    @private
    Channel *selectedChannel;
}

@property (nonatomic,retain)Channel *selectedChannel;

@end
