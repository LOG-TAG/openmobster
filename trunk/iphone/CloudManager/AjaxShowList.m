//
//  AjaxShowList.m
//  CloudManager
//
//  Created by openmobster on 2/14/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import "AjaxShowList.h"
#import "AppSession.h"
#import "EmailBean.h"
#import "CommandService.h"
#import "CommandContext.h"
#import "AjaxGetDetailsCommand.h"
#import "AjaxShowDetails.h"


@implementation AjaxShowList

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


- (void)dealloc 
{
	[delegate release];
    [super dealloc];
}

-(IBAction) done:(id) sender
{
	[delegate dismissModalViewControllerAnimated:YES];
}

//UITableView protocol implementation-----------------------------------------------------
-(NSInteger) tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section 
{
	//Number of Emails == Number of Rows to Display
	AppSession *session = [AppSession getInstance];
	NSArray *emails = [session getAttribute:@"emails"];
	int count = [emails count];
	return count;
}

-(UITableViewCell *) tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath 
{
	//Get the emails to be displayed
	AppSession *session = [AppSession getInstance];
	NSArray *emails = [session getAttribute:@"emails"];
	
	UITableViewCell *local = [tableView dequeueReusableCellWithIdentifier:@"ajaxshowlist"];
	if(local == nil)
	{
		local = [[UITableViewCell alloc] initWithFrame:CGRectZero reuseIdentifier:@"ajaxshowlist"];
		local = [local autorelease];
	}
	
	int index = indexPath.row;
	
	//Find the email to be displayed
	EmailBean *email = [emails objectAtIndex:index];
	
	//Since its a non-detail look, only show the subject
	local.textLabel.text = email.subject;
	
	return local;
}

-(void) tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath 
{
	 //Find the email that was selected
	 AppSession *session = [AppSession getInstance];
	 NSArray *emails = [session getAttribute:@"emails"];
	
	//Select the item  
	[tableView deselectRowAtIndexPath:indexPath animated:YES];
	[tableView scrollToRowAtIndexPath:indexPath atScrollPosition:UITableViewScrollPositionMiddle animated:YES];
	
	//Select the Email bean that was selected
	int index = indexPath.row;
	EmailBean *bean = [emails objectAtIndex:index];
	
	//Setup the CommandContext for the action that gets more details
	//about the email from the server
	CommandContext *commandContext = [CommandContext withInit:self];
	[commandContext setTarget:[AjaxGetDetailsCommand withInit]];
	[commandContext setAttribute:@"email" :bean];
	
	CommandService *service = [CommandService getInstance];
	
	//start the service invocation
	[service execute:commandContext];
}
//------------UICommandDelegate implementation-------------------------------------------
-(void)doViewAfter:(CommandContext *)commandContext
{
	//Go to AjaxShowDetails screen
	//Setup the AppSession with the fully populated email instance
	EmailBean *email = [commandContext getAttribute:@"email"];
	AppSession *appSession = [AppSession getInstance];
	[appSession setAttribute:@"email" :email];
	
	//SHow details as a modal screen
	AjaxShowDetails *modal = [[AjaxShowDetails alloc] initWithNibName:@"AjaxShowDetails" bundle:nil];
	modal.delegate = delegate;
	
	UINavigationController *navCtrl = [[UINavigationController alloc] initWithRootViewController:modal];
	[modal release];
	
	//Add the Title
	navCtrl.navigationBar.topItem.title = @"Ajax";
	
	//Add the Cancel button to the navbar
	UIBarButtonItem *done = [[UIBarButtonItem alloc] initWithTitle:@"Done" 
															 style:UIBarButtonItemStyleDone 
															target:modal 
															action:@selector(done:)];
	navCtrl.topViewController.navigationItem.leftBarButtonItem = done;
	[done release];
	
	
	[self presentModalViewController:navCtrl animated:YES];
	[navCtrl release];
}

-(void)doViewError:(CommandContext *)commandContext
{
}

-(void)doViewAppException:(CommandContext *)commandContext
{
}
@end
