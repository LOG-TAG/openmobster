//
//  ShowList.m
//  CloudManager
//
//  Created by openmobster on 2/1/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import "ShowList.h"
#import "MobileBean.h"
#import "AppService.h"
#import "AppSession.h"

@implementation ShowList

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

-(IBAction) newticket:(id) sender
{
	AppSession *session = [AppSession getInstance];
	[session removeAttribute:@"active-bean"];
	
	[self loadSaveView:sender];
}

-(void)loadSaveView:(id) sender
{
	//Go to SaveTicket
	SaveTicket *saveTicket = [[SaveTicket alloc] initWithNibName:@"SaveTicket" bundle:nil];
	UINavigationController *navCtrl = self.navigationController;
	saveTicket.delegate = delegate;
	
	[navCtrl pushViewController:saveTicket animated:YES];
	
	//Add the Save Button to the NavBar
	UIBarButtonItem *saveButton = [[UIBarButtonItem alloc] initWithTitle:@"Save" style:UIBarButtonItemStyleDone target:saveTicket action:@selector(save:)];
	navCtrl.topViewController.navigationItem.rightBarButtonItem = saveButton;
	
	//Cleanup
	[saveTicket release];
	[saveButton release];
}


-(NSInteger) tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section 
{
	AppSession *session = [AppSession getInstance];
	NSArray *beans = [session getAttribute:@"beans"];
	int count = [beans count];
	return count;
}

-(UITableViewCell *) tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath 
{	
	AppSession *session = [AppSession getInstance];
	NSArray *beans = [session getAttribute:@"beans"];
	
	UITableViewCell *local = [tableView dequeueReusableCellWithIdentifier:@"showlist"];
	if(local == nil)
	{
		local = [[UITableViewCell alloc] initWithFrame:CGRectZero reuseIdentifier:@"showlist"];
		local = [local autorelease];
	}
	
	int index = indexPath.row;
	
	MobileBean *bean = [beans objectAtIndex:index];
	
	NSString *display = [bean getValue:@"title"];
	local.textLabel.text = display;
	
	return local;
}

-(void) tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath 
{
	AppSession *session = [AppSession getInstance];
	NSArray *beans = [session getAttribute:@"beans"];
	
	/*
	 int count = [beans count];
	 NSLog(@"*****************************************");
	NSLog(@"Bean Count: %d",count);
	
	
	for(int i=0; i<5; i++)
	{
		MobileBean *bean = [beans objectAtIndex:i];
		NSLog(@"Bean: %@",bean);
		NSLog(@"Data: %@",bean.data);
		NSLog(@"Title: %@",[bean getValue:@"title"]);
	}
	NSLog(@"*****************************************");*/
	
	[tableView deselectRowAtIndexPath:indexPath animated:YES];
	[tableView scrollToRowAtIndexPath:indexPath atScrollPosition:UITableViewScrollPositionMiddle animated:YES];
	
	int index = indexPath.row;
	MobileBean *bean = [beans objectAtIndex:index];
	[session setAttribute:@"active-bean" :bean];
	
	//Show the ticket in a dialog box
	NSString *title = [bean getValue:@"title"];
	NSString *comment = [bean getValue:@"comment"];
	
	UIAlertView *dialog = [[UIAlertView alloc] initWithTitle:title
													 message:comment 
													delegate:self cancelButtonTitle:@"OK" 
										   otherButtonTitles:@"Update",@"Delete",nil];
	
	
	dialog = [dialog autorelease];
	[dialog show];
}

- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
	if(buttonIndex == 1)
	{
		[self loadSaveView:self];
	}
	else if(buttonIndex == 2)
	{
		CommandContext *commandContext = [CommandContext withInit:self];
		[commandContext setTarget:[DeleteTicketCommand withInit]];
	 
		CommandService *service = [CommandService getInstance];
		[service execute:commandContext];
	}
}

-(void) tableView:(UITableView *)tableView accessoryButtonTappedForRowWithIndexPath:(NSIndexPath *)indexPath 
{
}
//------UICommandDelegate impl-----------------------------------------------------
-(void)doViewAfter:(CommandContext *)commandContext
{
	//Refresh the UITableView
	[beanList reloadData];
}

-(void)doViewError:(CommandContext *)commandContext
{
	UIAlertView *dialog = [[UIAlertView alloc] initWithTitle:@"Error"
						  message:@"Unkown System Error" delegate:nil 
						  cancelButtonTitle:@"OK" otherButtonTitles:nil];
	dialog = [dialog autorelease];
	[dialog show];
}

-(void)doViewAppException:(CommandContext *)commandContext
{
	UIAlertView *dialog = [[UIAlertView alloc] initWithTitle:@"Error"
													 message:@"Unkown System Error" delegate:nil 
										   cancelButtonTitle:@"OK" otherButtonTitles:nil];
	dialog = [dialog autorelease];
	[dialog show];
}

@end
