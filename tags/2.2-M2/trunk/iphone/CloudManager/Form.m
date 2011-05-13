//
//  Form.m
//  CloudManager
//
//  Created by openmobster on 2/16/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import "Form.h"


@implementation Form

@synthesize delegate;

/*
 // The designated initializer.  Override if you create the controller programmatically and want to perform customization that is not appropriate for viewDidLoad.
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil {
    if ((self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil])) {
        // Custom initialization
    }
    return self;
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
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
}


- (void)dealloc {
	[delegate release];
    [super dealloc];
}

-(IBAction)done:(id)sender
{
	[delegate dismissModalViewControllerAnimated:YES];
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section
{
	if(section == 0)
	{
		return @"Text Field";
	}
	else if(section == 1)
	{
		return @"Check Box";
	}
	else if(section == 2)
	{
		return @"Select";
	}
	return @"";
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
	return 3;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
	return 2;
}

-(UITableViewCell *) tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath 
{
	int index = indexPath.row;
	int section = indexPath.section;
	
	//Setup cell properties
	UITableViewCell *local = [tableView dequeueReusableCellWithIdentifier:@"form-submission"];
	if(local == nil)
	{
		local = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleValue2 
									   reuseIdentifier:@"form-submission"];
		local = [local autorelease];
		local.accessoryType = UITableViewCellAccessoryNone;
	}
	
	if(section == 0)
	{
		local.textLabel.text = @"TextField";
	}
	else if(section == 1)
	{
		local.textLabel.text = @"CheckBox";
	}
	else if(section == 2)
	{
		local.textLabel.text = @"Select";
	}
	
	
	return local;
}
@end
