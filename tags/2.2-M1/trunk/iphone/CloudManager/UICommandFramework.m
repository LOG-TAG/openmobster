//
//  UICommandFramework.m
//  CloudManager
//
//  Created by openmobster on 2/19/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import "UICommandFramework.h"
#import "CommandContext.h"
#import "CommandService.h"
#import "AjaxCommand.h"
#import "BusyCommand.h"
#import "FastCommand.h"


@implementation UICommandFramework

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

//---------UITableViewDataSource and UITableViewDelegate--------------------------------------
-(NSInteger) tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section 
{
	return 3;
}

-(UITableViewCell *) tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath 
{
	UITableViewCell *local = [tableView dequeueReusableCellWithIdentifier:@"command-framework"];
	if(local == nil)
	{
		local = [[UITableViewCell alloc] initWithFrame:CGRectZero reuseIdentifier:@"command-framework"];
		local = [local autorelease];
	}
	
	int index = indexPath.row;
	
	if(index == 0)
	{
		local.textLabel.text = @"Ajax Command";
	}
	else if(index == 1)
	{
		local.textLabel.text = @"Busy Command";
	}
	else if(index == 2)
	{
		local.textLabel.text = @"Fast Command";
	}
	
	return local;
}

-(void) tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath 
{
	//Select the item  
	[tableView deselectRowAtIndexPath:indexPath animated:YES];
	[tableView scrollToRowAtIndexPath:indexPath atScrollPosition:UITableViewScrollPositionMiddle animated:YES];
	
	//Select the Email bean that was selected
	int index = indexPath.row;
	
	if(index == 0)
	{
		CommandContext *commandContext = [CommandContext withInit:self];
		[commandContext setTarget:[AjaxCommand withInit]];
		CommandService *service = [CommandService getInstance];
		[commandContext setAttribute:@"type" :@"ajax"];
		[service execute:commandContext];
	}
	else if(index == 1)
	{
		CommandContext *commandContext = [CommandContext withInit:self];
		[commandContext setTarget:[BusyCommand withInit]];
		CommandService *service = [CommandService getInstance];
		[commandContext setAttribute:@"type" :@"busy"];
		[service execute:commandContext];
	}
	else if(index == 2)
	{
		CommandContext *commandContext = [CommandContext withInit:self];
		[commandContext setTarget:[FastCommand withInit]];
		CommandService *service = [CommandService getInstance];
		[commandContext setAttribute:@"type" :@"fast"];
		[service execute:commandContext];
	}
}
//--------UICommandDelegate-----------------------------------------------------
-(void)doViewAfter:(CommandContext *)commandContext
{
	NSString *type = [commandContext getAttribute:@"type"];
	
	if([type isEqualToString:@"ajax"])
	{
		UIAlertView *dialog = [[UIAlertView alloc] initWithTitle:@"Ajax Command" 
							message:@"Command success!!!" 
							delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
		dialog = [dialog autorelease];
		[dialog show];
	}
	else if([type isEqualToString:@"busy"])
	{
		UIAlertView *dialog = [[UIAlertView alloc] initWithTitle:@"Busy Command" 
									message:@"Command success!!!" 
								delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
		dialog = [dialog autorelease];
		[dialog show];
	}
	else if([type isEqualToString:@"fast"])
	{
		UIAlertView *dialog = [[UIAlertView alloc] initWithTitle:@"Fast Command" 
									message:@"Command success!!!" 
								delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
		dialog = [dialog autorelease];
		[dialog show];
	}
}

-(void)doViewError:(CommandContext *)commandContext
{
	UIAlertView *dialog = [[UIAlertView alloc] initWithTitle:@"Error" 
													 message:@"Unknow System Error occured" delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
	dialog = [dialog autorelease];
	[dialog show];
}

-(void)doViewAppException:(CommandContext *)commandContext
{
	UIAlertView *dialog = [[UIAlertView alloc] initWithTitle:@"App Exception" 
					message:@"Unknow App Exception occured" delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
	dialog = [dialog autorelease];
	[dialog show];
}
@end
