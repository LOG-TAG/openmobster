//
//  ViewController.h
//  SampleApp
//
//  Created by openmobster on 8/31/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "UICommandDelegate.h"

@interface ViewController : UIViewController<UITableViewDataSource,UITableViewDelegate,UIAlertViewDelegate,UICommandDelegate>
{
    @private
    IBOutlet UITableView *beansTable;
}

@property (nonatomic,strong) IBOutlet UITableView *beansTable;

-(IBAction)launchCloudManager:(id)sender;
-(void)launchSaveView;
-(void)launchCreateBean;

-(void)setUpBeans;
@end
