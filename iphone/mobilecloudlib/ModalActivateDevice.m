/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "ModalActivateDevice.h"


@implementation ModalActivateDevice

@synthesize delegate;
@synthesize forceActivation;
@synthesize login;
@synthesize password;
@synthesize next;

/*
 // The designated initializer.  Override if you create the controller programmatically and want to perform customization that is not appropriate for viewDidLoad.
-(id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil 
{
    if ((self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil])) {
        // Custom initialization
    }
    return self;
}
*/

// Implement viewDidLoad to do additional setup after loading the view, typically from a nib.
-(void)viewDidLoad 
{
    [super viewDidLoad];
	
	Configuration *conf = [Configuration getInstance];
	NSString *email = conf.email;
	
	//Setup Login TextField
	UITextField *textField = [[UITextField alloc] initWithFrame:CGRectMake(110, 10, 185, 30)];
	textField = [textField autorelease];
	self.login = textField;
	textField.adjustsFontSizeToFitWidth = YES;
	textField.textColor = [UIColor blackColor];
	textField.keyboardType = UIKeyboardTypeEmailAddress;
	textField.returnKeyType = UIReturnKeyNext;
	textField.backgroundColor = [UIColor whiteColor];
	textField.autocorrectionType = UITextAutocorrectionTypeNo; // no auto correction support
	textField.autocapitalizationType = UITextAutocapitalizationTypeNone; // no auto capitalization support
	textField.textAlignment = UITextAlignmentLeft;
	textField.tag = 0;
	textField.clearButtonMode = UITextFieldViewModeNever; // no clear 'x' button to the right
	[textField setEnabled: YES];
	if(![StringUtil isEmpty:email])
	{
		self.login.text = email;
	}
	
	//Setup Password TextField
	textField = [[UITextField alloc] initWithFrame:CGRectMake(110, 10, 185, 30)];
	textField = [textField autorelease];
	self.password = textField;
	textField.adjustsFontSizeToFitWidth = YES;
	textField.textColor = [UIColor blackColor];
	textField.keyboardType = UIKeyboardTypeDefault;
	textField.returnKeyType = UIReturnKeyDone;
	textField.secureTextEntry = YES;			
	textField.backgroundColor = [UIColor whiteColor];
	textField.autocorrectionType = UITextAutocorrectionTypeNo; // no auto correction support
	textField.autocapitalizationType = UITextAutocapitalizationTypeNone; // no auto capitalization support
	textField.textAlignment = UITextAlignmentLeft;
	textField.tag = 0;
	textField.clearButtonMode = UITextFieldViewModeNever; // no clear 'x' button to the right
	[textField setEnabled: YES];
	
	ActivateDeviceCloudInfo *cloudInfo = [[ActivateDeviceCloudInfo alloc] initWithNibName:@"ActivateDeviceCloudInfo" bundle:nil];
	self.next = cloudInfo;
	[cloudInfo release];
}

/*
// Override to allow orientations other than the default portrait orientation.
-(BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation 
{
    // Return YES for supported orientations
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}
*/

-(void)didReceiveMemoryWarning 
{
    // Releases the view if it doesn't have a superview.
    [super didReceiveMemoryWarning];
    
    // Release any cached data, images, etc that aren't in use.
}

-(void)viewDidUnload 
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
}


-(void)dealloc 
{
	[delegate release];
	
	[login release];
	[password release];
	[next release];
	
	[super dealloc];
}

-(IBAction) next:(id) sender
{
	//Validation
	if([StringUtil isEmpty:login.text])
	{
		NSString *code = @"Validation Error";
		NSString *message = @"Email is Required";
		UIAlertView *dialog = [[UIAlertView alloc] initWithTitle:code message:message delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
		dialog = [dialog autorelease];
		
		[dialog show];
		
		return;
	}
	else if([StringUtil isEmpty:password.text])
	{
		NSString *code = @"Validation Error";
		NSString *message = @"Password is Required";
		UIAlertView *dialog = [[UIAlertView alloc] initWithTitle:code message:message delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
		dialog = [dialog autorelease];
		
		[dialog show];
		
		return;
	}
	
	//Read the email and password information
	NSString *inputLogin = [NSString stringWithString:login.text];
	NSString *inputPassword = [NSString stringWithString:password.text];
	
	UINavigationController *navCtrl = self.navigationController;
	next.delegate = delegate;
	next.forceActivation = forceActivation;
	
	CommandContext *commandContext = [CommandContext withInit:next];
	[commandContext setAttribute:@"login" :inputLogin];
	[commandContext setAttribute:@"password" :inputPassword];
	next.commandContext = commandContext;
	
	[navCtrl pushViewController:next animated:YES];
	
	//Add the Activate button to the navbar
	UIBarButtonItem *activateButton = [[UIBarButtonItem alloc] initWithTitle:@"Activate" style:UIBarButtonItemStyleDone target:next action:@selector(submit:)];
	navCtrl.topViewController.navigationItem.rightBarButtonItem = activateButton;
	[activateButton release];
}

-(IBAction) cancel:(id) sender
{
	[self dismiss];
}

-(void)dismiss
{
	if(!forceActivation)
	{
		[delegate dismissModalViewControllerAnimated:YES];
	}
	else 
	{
		Configuration *conf = [Configuration getInstance];
		if([conf isActivated])
		{
			[delegate dismissModalViewControllerAnimated:YES];
		}
		else 
		{
			NSString *code = @"Device Activation";
			NSString *message = @"Device must be activated with the Cloud before the App can be used";
			UIAlertView *dialog = [[UIAlertView alloc] initWithTitle:code message:message delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];			
			dialog = [dialog autorelease];
			
			[dialog show];
		}
	}
}
//UITableViewDataSource and UITableViewDelegate protocol implementation
- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section
{
	return @"Login";
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
	UITableViewCell *local = [tableView dequeueReusableCellWithIdentifier:@"activate-device"];
	if(local == nil)
	{
		local = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"activate-device"];
		local = [local autorelease];
		local.accessoryType = UITableViewCellAccessoryNone;
	}
	
	int index = indexPath.row;
	
	UITextField *textField = [[UITextField alloc] initWithFrame:CGRectMake(110, 10, 185, 30)];
	textField = [textField autorelease];
	
	switch(index)
	{
		case 0:
			local.textLabel.text = @"Email :";
			[local addSubview:self.login];
		break;
			
		case 1:
			local.textLabel.text = @"Password :";
			[local addSubview:self.password];
		break;
	}
	
	return local;
}
@end