//
//  ShowList.h
//  CloudManager
//
//  Created by openmobster on 2/1/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "SaveTicket.h"
#import "UICommandDelegate.h"
#import "CommandService.h"
#import "CommandContext.h"
#import "DeleteTicketCommand.h"
#import "MobileBean.h"


@interface ShowList : UIViewController<UITableViewDataSource,UITableViewDelegate,UIAlertViewDelegate,UICommandDelegate> 
{
	@private
	UIViewController *delegate;
	UITableView *beanList;
}

@property(nonatomic,retain) UIViewController *delegate;
@property (nonatomic, retain) IBOutlet UITableView *beanList;

-(IBAction) done:(id) sender;
-(IBAction) newticket:(id) sender;
-(void)loadSaveView:(id) sender;

@end
