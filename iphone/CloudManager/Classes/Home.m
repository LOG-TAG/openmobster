/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "Home.h"


@implementation Home

/*
 // The designated initializer.  Override if you create the controller programmatically and want to perform customization that is not appropriate for viewDidLoad.
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil {
    if ((self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil])) {
        // Custom initialization
    }
    return self;
}
*/

// Override to allow orientations other than the default portrait orientation.
/*
- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation 
{
    // Return YES for supported orientations
    //return (interfaceOrientation == UIInterfaceOrientationPortrait);
}
*/

// Implement viewDidLoad to do additional setup after loading the view, typically from a nib.
- (void)viewDidLoad 
{
    [super viewDidLoad];
}

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
    [super dealloc];
}

//UITableViewDataSource and UITableViewDelegate protocol implementation
-(NSInteger) tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section 
{
	return 3;
}

-(UITableViewCell *) tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath 
{
	UITableViewCell *local = [tableView dequeueReusableCellWithIdentifier:@"control-panel"];
	if(local == nil)
	{
		local = [[UITableViewCell alloc] initWithFrame:CGRectZero reuseIdentifier:@"control-panel"];
		local = [local autorelease];
	}
	
	int index = indexPath.row;
	
	switch(index)
	{
		case 0:
			local.textLabel.text = @"Activate Device";
		break;
			
		case 1:
			 local.textLabel.text = @"Security";
		break;
			
		case 2:
			 local.textLabel.text = @"Cloud Status";
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
	
	
	if(index == 0)
	{
		//Activate Device
		ModalActivateDevice *modalView = [[ModalActivateDevice alloc] initWithNibName:@"ModalActivateDevice" bundle:nil];
		modalView.delegate = self;
	
		UINavigationController *navCtrl = [[UINavigationController alloc] initWithRootViewController:modalView];
		[modalView release];
	
		//Add the Title
		navCtrl.navigationBar.topItem.title = @"Activate Device";
	
		//Add the Cancel button to the navbar
		UIBarButtonItem *cancelButton = [[UIBarButtonItem alloc] initWithTitle:@"Cancel" style:UIBarButtonItemStyleDone target:modalView action:@selector(cancel:)];
		navCtrl.topViewController.navigationItem.leftBarButtonItem = cancelButton;
		[cancelButton release];
		
		//Add the Activate button to the navbar
		UIBarButtonItem *activateButton = [[UIBarButtonItem alloc] initWithTitle:@"Activate" style:UIBarButtonItemStyleDone target:modalView action:@selector(submit:)];
		navCtrl.topViewController.navigationItem.rightBarButtonItem = activateButton;
		[activateButton release];
	
	
		[self presentModalViewController:navCtrl animated:YES];
		[navCtrl release];
	}
	else if(index == 1)
	{
		//Security
		SecurityConfig *modalView = [[SecurityConfig alloc] initWithNibName:@"SecurityConfig" bundle:nil];
		modalView.delegate = self;
		
		UINavigationController *navCtrl = [[UINavigationController alloc] initWithRootViewController:modalView];
		[modalView release];
		
		//Add the Title
		navCtrl.navigationBar.topItem.title = @"Security";
		
		//Add the Cancel button to the navbar
		UIBarButtonItem *cancelButton = [[UIBarButtonItem alloc] initWithTitle:@"Cancel" style:UIBarButtonItemStyleDone target:modalView action:@selector(cancel:)];
		navCtrl.topViewController.navigationItem.leftBarButtonItem = cancelButton;
		[cancelButton release];
		
		
		[self presentModalViewController:navCtrl animated:YES];
		[navCtrl release];
	}	
	else if(index == 2)
	{
		//Cloud Status
		//Dialog Popup prototype
		UIAlertView *dialog = [[UIAlertView alloc] initWithTitle:@"Hello!!" message:@"Hello World!!" delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
		dialog = [dialog autorelease];
		[dialog show];
	}
}
//protocol for prototyping Modal ViewController
-(void)callback:(NSDictionary *)returnVal
{
	[self dismissModalViewControllerAnimated:YES];
}
@end