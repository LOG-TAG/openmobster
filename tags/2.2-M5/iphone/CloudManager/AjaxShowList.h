//
//  AjaxShowList.h
//  CloudManager
//
//  Created by openmobster on 2/14/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "UICommandDelegate.h"


@interface AjaxShowList : UIViewController<UITableViewDataSource,UITableViewDelegate,UICommandDelegate> 
{
	@private
	UIViewController *delegate;
}

@property(nonatomic,retain) UIViewController *delegate;

-(IBAction) done:(id) sender;
@end
