//
//  SaveTicket.h
//  SampleApp
//
//  Created by openmobster on 9/6/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "UICommandDelegate.h"

@interface SaveTicket : UIViewController<UITableViewDataSource,UITableViewDelegate,UICommandDelegate>
{
    @private
	//Form fields
	UITextField *ticketTitle;
	UITextField *ticketComments;
}

@property(nonatomic,retain) UITextField *ticketTitle;
@property(nonatomic,retain) UITextField *ticketComments;

-(IBAction) save:(id) sender;
@end
