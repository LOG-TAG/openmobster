//
//  ManualSyncController.m
//  mobilecloudlib
//
//  Created by openmobster on 8/25/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "ManualSyncController.h"
#import "AppService.h"
#import "Channel.h"

@implementation ManualSyncController

@synthesize delegate;
@synthesize menu;
@synthesize myChannels;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)didReceiveMemoryWarning
{
    // Releases the view if it doesn't have a superview.
    [super didReceiveMemoryWarning];
    
    // Release any cached data, images, etc that aren't in use.
}

#pragma mark - View lifecycle

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    self.menu = [[ManualSyncMenuController alloc] initWithNibName:@"ManualSyncMenuController" bundle:nil];
    
    //get my sync channels
    AppService *appService = [AppService getInstance];
    self.myChannels = [appService myChannels];
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    // Return YES for supported orientations
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

-(void)dealloc 
{
	[delegate release];
    [menu release];
    [myChannels release];
    [super dealloc];
}

-(IBAction) cancel:(id) sender
{
   [delegate dismissModalViewControllerAnimated:YES]; 
}

//--------UITableView Protocols implementation---------------------------------------------------------------------
-(NSInteger) tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section 
{
    int size = [self.myChannels count];
	return size;
}

-(UITableViewCell *) tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath 
{
	UITableViewCell *local = [tableView dequeueReusableCellWithIdentifier:@"manual-sync-controller"];
	if(local == nil)
	{
		local = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"manual-sync-controller"];
		local = [local autorelease];
	}
	
	int index = indexPath.row;
	
    Channel *channel = (Channel *)[self.myChannels objectAtIndex:index];
    local.textLabel.text = channel.name;
	
	return local;
}

-(void) tableView:(UITableView *)tableView accessoryButtonTappedForRowWithIndexPath:(NSIndexPath *)indexPath 
{
}

-(void) tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath 
{
	[tableView deselectRowAtIndexPath:indexPath animated:YES];
	
	int index = indexPath.row;
    
    Channel *selectedChannel = (Channel *)[self.myChannels objectAtIndex:index];
    
    //set this on the menu
    self.menu.selectedChannel = selectedChannel;
    
    UINavigationController *navCtrl = self.navigationController;
    [navCtrl pushViewController:self.menu animated:YES];
}

@end
