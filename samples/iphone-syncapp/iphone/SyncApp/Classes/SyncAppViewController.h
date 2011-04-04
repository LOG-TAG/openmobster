//
//  SyncAppViewController.h
//  SyncApp
//
//  Created by openmobster on 3/31/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "UICommandDelegate.h"

@interface SyncAppViewController : UIViewController<UITableViewDataSource,UITableViewDelegate,UIAlertViewDelegate> 
{
	@private
	BOOL channelIsEmpty;
	UITableView *beanList;
}
@property (nonatomic, retain) IBOutlet UITableView *beanList;
@end

