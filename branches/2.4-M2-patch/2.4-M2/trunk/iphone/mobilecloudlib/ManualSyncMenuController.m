//
//  ManualSyncMenuController.m
//  mobilecloudlib
//
//  Created by openmobster on 8/25/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "ManualSyncMenuController.h"
#import "CommandContext.h"
#import "CommandService.h"
#import "ManualSync.h"

@implementation ManualSyncMenuController

@synthesize selectedChannel;

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
    [selectedChannel release];
    [super dealloc];
}

//--------UITableView Protocols implementation---------------------------------------------------------------------
-(NSInteger) tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section 
{
	return 2;
}

-(UITableViewCell *) tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath 
{
	UITableViewCell *local = [tableView dequeueReusableCellWithIdentifier:@"manual-sync-menu-controller"];
	if(local == nil)
	{
		local = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"manual-sync-menu-controller"];
		local = [local autorelease];
	}
	
	int index = indexPath.row;
	
	switch(index)
	{
		case 0:
			local.textLabel.text = @"Reset Channel";
            break;
            
        case 1:
			local.textLabel.text = @"Sync Channel";
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
    
    NSString *channel = self.selectedChannel.name;
    
    ManualSync *manualSync = [ManualSync withInit];
    CommandContext *commandContext = [CommandContext withInit:self];
    [commandContext setAttribute:@"channel" :channel];
    [commandContext setTarget:manualSync];
    CommandService *service = [CommandService getInstance];
    switch(index)
	{
		case 0:
            //reset channel
            [commandContext setAttribute:@"sync-type" :@"reset"];
            [service execute:commandContext];
        break;
            
        case 1:
            //sync the channel
            [commandContext setAttribute:@"sync-type" :@"two-way"];
            [service execute:commandContext];
        break;
	}
}
//----------Implementing the UICommandDelegate protocol--------------------------------------------------
-(void)doViewAfter:(CommandContext *)callback
{
    NSString *code = @"Channel Sync";
	NSString *message = @"Channel successfully synchronized";
	UIAlertView *dialog = [[UIAlertView alloc] initWithTitle:code message:message delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
	dialog = [dialog autorelease];
	
	[dialog show];
}
-(void)doViewError:(CommandContext *)callback
{
    NSString *code = [callback getErrorCode];
	NSString *message = [callback getErrorMessage];
	UIAlertView *dialog = [[UIAlertView alloc] initWithTitle:code message:message delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
	dialog = [dialog autorelease];
	
	[dialog show];
}
-(void)doViewAppException:(CommandContext *)callback
{
    AppException *appe = [callback getAppException];
	
	NSString *code = [appe getType];
	NSString *message = [appe getMessage];
    
    
	UIAlertView *dialog = [[UIAlertView alloc] initWithTitle:code message:message delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
	dialog = [dialog autorelease];
	
	[dialog show]; 
}
@end
