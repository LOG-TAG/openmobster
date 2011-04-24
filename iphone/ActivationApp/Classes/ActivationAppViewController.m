//
//  ActivationAppViewController.m
//  ActivationApp
//
//  Created by openmobster on 2/28/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import "ActivationAppViewController.h"
#import "UIKernel.h"

@implementation ActivationAppViewController



/*
// The designated initializer. Override to perform setup that is required before the view is loaded.
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil {
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}
*/

/*
// Implement loadView to create a view hierarchy programmatically, without using a nib.
- (void)loadView {
}
*/


/*
// Implement viewDidLoad to do additional setup after loading the view, typically from a nib.
- (void)viewDidLoad {
    [super viewDidLoad];
}
*/


/*
// Override to allow orientations other than the default portrait orientation.
- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation {
    // Return YES for supported orientations
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}
*/

- (void)didReceiveMemoryWarning {
	// Releases the view if it doesn't have a superview.
    [super didReceiveMemoryWarning];
	
	// Release any cached data, images, etc that aren't in use.
}

- (void)viewDidUnload {
	// Release any retained subviews of the main view.
	// e.g. self.myOutlet = nil;
}


- (void)dealloc {
    [super dealloc];
}

 // Implement viewDidLoad to do additional setup after loading the view, typically from a nib.
 - (void)viewDidLoad 
 {
	 [super viewDidLoad];
 }

//-----UITableViewDataSource implementation--------------------------------------------
-(NSInteger) tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section 
{
	return 1;
}

-(UITableViewCell *) tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath 
{
	UITableViewCell *local = [tableView dequeueReusableCellWithIdentifier:@"home"];
	if(local == nil)
	{
		local = [[UITableViewCell alloc] initWithFrame:CGRectZero reuseIdentifier:@"home"];
		local = [local autorelease];
	}
	
	int index = indexPath.row;
	
	switch(index)
	{
		case 0:
			local.textLabel.text = @"Activation";
		break;
			
	}
	
	return local;
}
//-----UITableViewDelegate implementation---------------------------------------------
-(void) tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath 
{
	[tableView deselectRowAtIndexPath:indexPath animated:YES];
	
	int index = indexPath.row;
	
	if(index == 0)
	{
		UIKernel *kernel = [UIKernel getInstance];
		[kernel forceActivation:self];
	}
}
@end
