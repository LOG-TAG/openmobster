//
//  SaveTicket.m
//  SampleApp
//
//  Created by openmobster on 9/6/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "SaveTicket.h"

#import "AppSession.h"
#import "MobileBean.h"
#import "CommandContext.h"
#import "CommandService.h"
#import "SaveTicketCommand.h"
#import "StringUtil.h"

@implementation SaveTicket

@synthesize ticketComments;
@synthesize ticketTitle;

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

/*
// Implement loadView to create a view hierarchy programmatically, without using a nib.
- (void)loadView
{
}
*/

// Implement viewDidLoad to do additional setup after loading the view, typically from a nib.
-(void)viewDidLoad 
 {
     [super viewDidLoad];
 
     //Setup IP Field
     UITextField *textField = [[UITextField alloc] initWithFrame:CGRectMake(110, 10, 185, 30)];
     textField = [textField autorelease];
     self.ticketTitle = textField;
     textField.adjustsFontSizeToFitWidth = YES;
     textField.textColor = [UIColor blackColor];
     textField.keyboardType = UIKeyboardTypeURL;
     textField.returnKeyType = UIReturnKeyNext;
     textField.backgroundColor = [UIColor whiteColor];
     textField.autocorrectionType = UITextAutocorrectionTypeNo; // no auto correction support
     textField.autocapitalizationType = UITextAutocapitalizationTypeNone; // no auto capitalization support
     textField.textAlignment = UITextAlignmentLeft;
     textField.tag = 0;
     textField.clearButtonMode = UITextFieldViewModeNever; // no clear 'x' button to the right
     [textField setEnabled: YES];
 
 
     //Setup Port Field
     textField = [[UITextField alloc] initWithFrame:CGRectMake(110, 10, 185, 30)];
     textField = [textField autorelease];
     self.ticketComments = textField;
     textField.adjustsFontSizeToFitWidth = YES;
     textField.textColor = [UIColor blackColor];
     textField.keyboardType = UIKeyboardTypeNumberPad;
     textField.returnKeyType = UIReturnKeyNext;
     textField.backgroundColor = [UIColor whiteColor];
     textField.autocorrectionType = UITextAutocorrectionTypeNo; // no auto correction support
     textField.autocapitalizationType = UITextAutocapitalizationTypeNone; // no auto capitalization support
     textField.textAlignment = UITextAlignmentLeft;
     textField.tag = 0;
     textField.clearButtonMode = UITextFieldViewModeNever; // no clear 'x' button to the right
     [textField setEnabled: YES];
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
//---------------------------------------------------------------------------------------------------
-(void) dealloc
{
    [ticketTitle release];
    [ticketComments release];
    [super dealloc];
}

-(IBAction) save:(id) sender
{
    //Validate the data
	NSString *title = ticketTitle.text;
	NSString *comment = ticketComments.text;
    if([StringUtil isEmpty:title] || [StringUtil isEmpty:comment])
	{
		NSString *code = @"Validation Error";
		NSString *message = @"All the fields are required";
		UIAlertView *dialog = [[UIAlertView alloc] initWithTitle:code message:message delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
		dialog = [dialog autorelease];
		
		[dialog show];
		
		return;
	}

    
	CommandContext *commandContext = [CommandContext withInit:self];
	[commandContext setTarget:[SaveTicketCommand withInit]];
	
	CommandService *service = [CommandService getInstance];
	
	[commandContext setAttribute:@"title" :title];
	[commandContext setAttribute:@"comment" :comment];
	
	//start the service invocation
	[service execute:commandContext];
}

//UITableViewDataSource and UITableViewDelegate protocol implementation----------------------------------------------
- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section
{
	return @"Ticket";
}

-(NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
	return 1;
}

-(NSInteger) tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section 
{
	return 2;
}

-(UITableViewCell *) tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath 
{
	UITableViewCell *local = [tableView dequeueReusableCellWithIdentifier:@"save-ticket"];
	if(local == nil)
	{
		local = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"save-ticket"];
		local = [local autorelease];
		local.accessoryType = UITableViewCellAccessoryNone;
	}
	
	int index = indexPath.row;
	
	AppSession *session = [AppSession getInstance];
	MobileBean *activeBean = [session getAttribute:@"active-bean"];
	switch(index)
	{
		case 0:
			local.textLabel.text = @"Title :";
			[local addSubview:self.ticketTitle];
			if(activeBean != nil)
			{
				self.ticketTitle.text = [activeBean getValue:@"title"];
			}
            break;
			
		case 1:
			local.textLabel.text = @"Comments :";
			[local addSubview:self.ticketComments];
			if(activeBean != nil)
			{
				self.ticketComments.text = [activeBean getValue:@"comment"];
			}
            break;
	}
	
	return local;
}
//-----UICommandDelegate implementation------------------------------------------------
-(void)doViewAfter:(CommandContext *)commandContext
{
    UINavigationController *navigationController = self.navigationController;
    [navigationController popViewControllerAnimated:YES];  
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
