//
//  CloudManagerApp.m
//  mobilecloudlib
//
//  Created by openmobster on 12/29/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

#import "CloudManagerApp.h"


@implementation CloudManagerApp

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

- (void)didReceiveMemoryWarning 
{
    // Releases the view if it doesn't have a superview.
    [super didReceiveMemoryWarning];
    
    // Release any cached data, images, etc that aren't in use.
}

- (void)viewDidUnload 
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
}

-(void)dealloc 
{
    [super dealloc];
	[delegate release];
}

-(IBAction) done:(id) sender
{
	[delegate dismissModalViewControllerAnimated:YES];
}

-(NSInteger) tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section 
{
	return 3;
}

-(UITableViewCell *) tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath 
{
	UITableViewCell *local = [tableView dequeueReusableCellWithIdentifier:@"control-panel"];
	if(local == nil)
	{
		local = [[UITableViewCell alloc] initWithFrame:CGRectZero reuseIdentifier:@"control-panel"];
		local = [local autorelease];
	}
	
	int index = indexPath.row;
	
	switch(index)
	{
		case 0:
			local.textLabel.text = @"Activate Device";
			break;
			
		case 1:
			local.textLabel.text = @"Security";
			break;
			
		case 2:
			local.textLabel.text = @"Cloud Status";
			break;
	}
	
	return local;
}

-(void) tableView:(UITableView *)tableView accessoryButtonTappedForRowWithIndexPath:(NSIndexPath *)indexPath 
{
}

-(void) tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath 
{
	[tableView deselectRowAtIndexPath:indexPath animated:YES];
	
	int index = indexPath.row;
	
	
	if(index == 0)
	{
		//Activate Device
		[CloudManager modalActivateDevice:self];
	}
	else if(index == 1)
	{
		//Security
		[CloudManager modalSecurityConfig:self];
	}	
	else if(index == 2)
	{
		//Cloud Status
		UIAlertView *dialog = [[UIAlertView alloc] initWithTitle:@"Status" message:@"Cloud Status!!!" delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
		dialog = [dialog autorelease];
		[dialog show];
	}
}
@end