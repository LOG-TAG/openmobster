//
//  ModalActivateDevice.m
//  CloudManager
//
//  Created by openmobster on 12/22/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

#import "ModalActivateDevice.h"


@implementation ModalActivateDevice

@synthesize delegate;

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

/*
// Implement viewDidLoad to do additional setup after loading the view, typically from a nib.
-(void)viewDidLoad 
{
    [super viewDidLoad];
}
*/

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
	[super dealloc];
}

-(IBAction) cancel:(id) sender
{
	[delegate callback:nil];
}

-(IBAction) submit:(id) sender
{
	AsyncSubmit *async = [AsyncSubmit withInit:self];
	[async start];
}

//UITableViewDataSource and UITableViewDelegate protocol implementation
- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section
{
	if(section == 0)
	{
		return @"Login";
	}
	else 
	{
		return @"Cloud Server";
	}
	
	//return @"";
}

-(NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
	return 2;
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
	int section = indexPath.section;
	
	UITextField *textField = [[UITextField alloc] initWithFrame:CGRectMake(110, 10, 185, 30)];
	textField = [textField autorelease];
	
	if(section == 0)
	{
		switch(index)
		{
			case 0:
			local.textLabel.text = @"Email :";
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
			[local addSubview:textField];
			break;
			
			case 1:
			local.textLabel.text = @"Password :";
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
			[local addSubview:textField];
			break;
		}
	}
	else
	{
		switch(index)
		{
				case 0:
				local.textLabel.text = @"Cloud IP :";
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
				[local addSubview:textField];
				break;
				
				case 1:
				local.textLabel.text = @"Cloud Port :";
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
				[local addSubview:textField];
				break;
		}
	}
	return local;
}

-(void)asyncCallback
{
	[delegate callback:nil];
}
@end
