//
//  ViewController.m
//  SampleApp
//
//  Created by openmobster on 8/31/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "ViewController.h"
#import "SaveTicket.h"
#import "DeleteTicketCommand.h"

#import "CloudManager.h"
#import "AppSession.h"
#import "MobileBean.h"

@implementation ViewController

@synthesize beansTable;

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Release any cached data, images, etc that aren't in use.
}

#pragma mark - View lifecycle

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
}

-(void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    //reload the data
    [self.beansTable reloadData];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
}

- (void)viewWillDisappear:(BOOL)animated
{
	[super viewWillDisappear:animated];
}

- (void)viewDidDisappear:(BOOL)animated
{
	[super viewDidDisappear:animated];
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    // Return YES for supported orientations
    if ([[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPhone) {
        return (interfaceOrientation != UIInterfaceOrientationPortraitUpsideDown);
    } else {
        return YES;
    }
}
//-------------------------------------------------------------------------------------------------------
-(IBAction)launchCloudManager:(id)sender
{
    //Launch the CloudManager App
    [CloudManager modalCloudManager:self];
}

-(void)setUpBeans
{
    //read the beans and populate the AppSession
    AppSession *session = [AppSession getInstance];
	NSArray *beans = [MobileBean readAll:@"webappsync_ticket_channel"];
    
    if(beans != nil)
    {
        [session setAttribute:@"beans" :beans];
    } 
}
//--------------------------------------------------------------------------------------------------------
-(NSInteger) tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section 
{
    [self setUpBeans];
    
	AppSession *session = [AppSession getInstance];
	NSArray *beans = [session getAttribute:@"beans"];
    
    if(beans == nil)
    {
        return 0;
    }
    
	int count = [beans count];
	return count;
}

-(UITableViewCell *) tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath 
{
    AppSession *session = [AppSession getInstance];
	NSArray *beans = [session getAttribute:@"beans"];
    
	UITableViewCell *local = [tableView dequeueReusableCellWithIdentifier:@"show-beans"];
	if(local == nil)
	{
		local = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"show-beans"];
		local = [local autorelease];
	}
	
	int index = indexPath.row;
    
    MobileBean *bean = [beans objectAtIndex:index];
	
	NSString *display = [bean getValue:@"title"];
	local.textLabel.text = display;
	
	return local;
}

-(void) tableView:(UITableView *)tableView accessoryButtonTappedForRowWithIndexPath:(NSIndexPath *)indexPath 
{
}

-(void) tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath 
{
    AppSession *session = [AppSession getInstance];
	NSArray *beans = [session getAttribute:@"beans"];
    
	[tableView deselectRowAtIndexPath:indexPath animated:YES];
	
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
//--------------------------------------------------------------------------------------------------
- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if(buttonIndex == 1)
    {
        //Update
        [self launchSaveView];
    }
    else if(buttonIndex == 2)
    {
        //Delete
        CommandContext *commandContext = [CommandContext withInit:self];
		[commandContext setTarget:[DeleteTicketCommand withInit]];
        
		CommandService *service = [CommandService getInstance];
		[service execute:commandContext];
    }
}

-(void)launchSaveView
{
	//Go to SaveTicket
	SaveTicket *saveTicket = [[SaveTicket alloc] initWithNibName:@"SaveTicket_iPhone" bundle:nil];
	UINavigationController *navCtrl = self.navigationController;
	//saveTicket.delegate = delegate;
	
	[navCtrl pushViewController:saveTicket animated:YES];
	
	//Add the Save Button to the NavBar
	UIBarButtonItem *saveButton = [[UIBarButtonItem alloc] initWithTitle:@"Save" style:UIBarButtonItemStyleDone target:saveTicket action:@selector(save:)];
	navCtrl.topViewController.navigationItem.rightBarButtonItem = saveButton;
	
	//Cleanup
	[saveTicket release];
	[saveButton release];
}

-(void)launchCreateBean
{
    //Create a New Bean
    AppSession *session = [AppSession getInstance];
    [session removeAttribute:@"active-bean"];
    
    [self launchSaveView]; 
}
//--------------------------------------------------------------------------------------------------
-(void)doViewAfter:(CommandContext *)callback
{
    [self.beansTable reloadData];
}
-(void)doViewError:(CommandContext *)callback
{
}
-(void)doViewAppException:(CommandContext *)callback
{
}
@end
