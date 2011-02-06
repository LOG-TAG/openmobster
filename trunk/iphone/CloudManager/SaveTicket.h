//
//  SaveTicket.h
//  CloudManager
//
//  Created by openmobster on 2/1/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "UICommandDelegate.h"
#import "SaveTicketCommand.h"
#import "CommandService.h"


@interface SaveTicket : UIViewController<UITableViewDataSource,UITableViewDelegate,UICommandDelegate>
{
	@private
	UIViewController *delegate;
	
	//Form fields
	UITextField *ticketTitle;
	UITextField *ticketComments;
}

@property(nonatomic,retain) UIViewController *delegate;
@property(nonatomic,retain) UITextField *ticketTitle;
@property(nonatomic,retain) UITextField *ticketComments;

-(IBAction) save:(id) sender;

@end
