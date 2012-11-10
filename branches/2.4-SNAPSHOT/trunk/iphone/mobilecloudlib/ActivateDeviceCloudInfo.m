//
//  ActivateDeviceCloudInfo.m
//  mobilecloudlib
//
//  Created by openmobster on 12/29/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

#import "ActivateDeviceCloudInfo.h"


@implementation ActivateDeviceCloudInfo

@synthesize delegate;
@synthesize forceActivation;
@synthesize cloudIp;
@synthesize cloudPort;
@synthesize commandContext;

/*
 // The designated initializer.  Override if you create the controller programmatically and want to perform customization that is not appropriate for viewDidLoad.
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil {
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
	NSString *ip = conf.serverIp;
	NSString *port = [conf serverPort];
	
	//Setup IP Field
	UITextField *textField = [[UITextField alloc] initWithFrame:CGRectMake(110, 10, 185, 30)];
	textField = [textField autorelease];
	self.cloudIp = textField;
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
	if(![StringUtil isEmpty:ip])
	{
		self.cloudIp.text = ip;
	}
	
	//Setup Port Field
	textField = [[UITextField alloc] initWithFrame:CGRectMake(110, 10, 185, 30)];
	textField = [textField autorelease];
	self.cloudPort = textField;
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
	if([StringUtil isEmpty:port])
	{
		self.cloudPort.text = @"1502";
	}
	else 
	{
		self.cloudPort.text = port;
	}
}

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
	
	[cloudIp release];
	[cloudPort release];
	
	[commandContext release];
    [super dealloc];
}

-(IBAction) submit:(id) sender
{
	//Validation
	if([StringUtil isEmpty:cloudIp.text])
	{
		NSString *code = @"Validation Error";
		NSString *message = @"Cloud IP or Host address is Required";
		UIAlertView *dialog = [[UIAlertView alloc] initWithTitle:code message:message delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
		dialog = [dialog autorelease];
		
		[dialog show];
		
		return;
	}
	else if([StringUtil isEmpty:cloudPort.text])
	{
		NSString *code = @"Validation Error";
		NSString *message = @"Cloud Port is Required";
		UIAlertView *dialog = [[UIAlertView alloc] initWithTitle:code message:message delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
		dialog = [dialog autorelease];
		
		[dialog show];
		
		return;
	}
	
	//Read the email and password information
	NSString *ip = [NSString stringWithString:cloudIp.text];
	NSString *port = [NSString stringWithString:cloudPort.text];
	
	[commandContext setAttribute:@"cloudIp" :ip];
	[commandContext setAttribute:@"cloudPort" :port];
	[commandContext setTarget:[ActivateDevice withInit]];
	CommandService *service = [CommandService getInstance];
	
	//start the service invocation
	[service execute:commandContext];
}

-(void)doViewAfter:(CommandContext *)callback
{
	[self dismiss];
	
	NSString *code = @"Device Activation";
	NSString *message = @"Device successfully activated";
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
    
    //cleanup the error from the commandcontext
    [callback clearErrors];
	
	[dialog show];
}

-(void)doViewAppException:(CommandContext *)callback
{
	AppException *appe = [callback getAppException];
	
	NSString *code = [appe getType];
	NSString *message = [appe getMessage];
    
    if([message isEqualToString:@"validation_error"])
    {
        message = @"Email is invalid";
    }
	UIAlertView *dialog = [[UIAlertView alloc] initWithTitle:code message:message delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
	dialog = [dialog autorelease];
	
	[dialog show];
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
	return @"Cloud Server";
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
			local.textLabel.text = @"IP or Host :";
			[local addSubview:self.cloudIp];
		break;
			
		case 1:
			local.textLabel.text = @"Port :";
			[local addSubview:self.cloudPort];
		break;
	}
	
	return local;
}
@end