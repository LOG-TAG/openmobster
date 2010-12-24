//
//  SecurityConfig.h
//  CloudManager
//
//  Created by openmobster on 12/23/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ModalViewLauncherDelegate.h"


@interface SecurityConfig : UIViewController 
{
	@private
	id<ModalViewLauncherDelegate> delegate;
}

@property(nonatomic,retain) id<ModalViewLauncherDelegate> delegate;

-(IBAction) cancel:(id) sender;
@end
