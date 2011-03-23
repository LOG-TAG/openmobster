//
//  AjaxShowDetails.m
//  CloudManager
//
//  Created by openmobster on 2/15/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import "AjaxShowDetails.h"
#import "AppSession.h"
#import "EmailBean.h"


@implementation AjaxShowDetails

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


- (void)dealloc 
{
	[delegate release];
    [super dealloc];
}

-(IBAction) done:(id) sender
{
	[delegate dismissModalViewControllerAnimated:YES];
}


- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section
{
	return @"Email";
}

-(NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
	return 1;
}

-(NSInteger) tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section 
{
	return 4;
}

-(UITableViewCell *) tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath 
{
	int index = indexPath.row;
	
	//Setup cell properties
	UITableViewCell *local = [tableView dequeueReusableCellWithIdentifier:@"ajaxshowdetails"];
	if(local == nil)
	{
		if(index == 2)
		{
			local = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle 
					reuseIdentifier:@"ajaxshowdetails"];
		}
		else 
		{
			local = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleValue2 
										   reuseIdentifier:@"ajaxshowdetails"];
		}

		local = [local autorelease];
		local.accessoryType = UITableViewCellAccessoryNone;
	}
	
	
	//Show the email
	AppSession *session = [AppSession getInstance];
	EmailBean *email = [session getAttribute:@"email"];
	switch(index)
	{
		case 0:
			local.textLabel.text = @"From :";
			local.detailTextLabel.text = email.from;
		break;
			
		case 1:
			local.textLabel.text = @"To:";
			local.detailTextLabel.text = email.to;
		break;
		
		case 2:
			local.textLabel.text = @"Subject:";
			local.detailTextLabel.text = email.subject;
		break;
			
		case 3:
			local.textLabel.text = @"Date:";
			local.detailTextLabel.text = email.date;
		break;
	}
	
	return local;
}
@end
