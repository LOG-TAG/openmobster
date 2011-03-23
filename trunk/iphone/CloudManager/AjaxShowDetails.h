//
//  AjaxShowDetails.h
//  CloudManager
//
//  Created by openmobster on 2/15/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>


@interface AjaxShowDetails : UIViewController<UITableViewDataSource,UITableViewDelegate> 
{
	@private
	UIViewController *delegate;
}

@property (nonatomic,retain)UIViewController *delegate;

-(IBAction) done:(id) sender;
@end
