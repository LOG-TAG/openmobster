//
//  IntegrationTestViewController.m
//  IntegrationTest
//
//  Created by openmobster on 8/12/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import "IntegrationTestViewController.h"
#import "CommandService.h"
#import "SyncIntegrationTest.h"


@implementation IntegrationTestViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)dealloc
{
    [super dealloc];
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
			local.textLabel.text = @"Test Sync";
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
        CommandContext *commandContext = [CommandContext withInit:self];
		[commandContext setTarget:[SyncIntegrationTest withInit]];
		
		CommandService *service = [CommandService getInstance];
		[service execute:commandContext];
	}
}
//-------------UICommandDelegate-----------------------------------------------------------
-(void)doViewAfter:(CommandContext *)commandContext
{
    UIAlertView *dialog = [[UIAlertView alloc] initWithTitle:@"Success"
													 message:@"Test Successfully Executed" delegate:nil 
										   cancelButtonTitle:@"OK" otherButtonTitles:nil];
	dialog = [dialog autorelease];
	[dialog show];  
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
    UIAlertView *dialog = [[UIAlertView alloc] initWithTitle:@"Command Error"
													 message:@"Unkown Command Error" delegate:nil 
										   cancelButtonTitle:@"OK" otherButtonTitles:nil];
	dialog = [dialog autorelease];
	[dialog show];
}
@end
