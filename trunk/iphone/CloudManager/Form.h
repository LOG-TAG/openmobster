//
//  Form.h
//  CloudManager
//
//  Created by openmobster on 2/16/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>


@interface Form : UIViewController<UITableViewDataSource,UITableViewDelegate> 
{	
	@private
	UIViewController *delegate;
}

@property(nonatomic,retain)UIViewController *delegate;

-(IBAction)done:(id)sender;

@end
