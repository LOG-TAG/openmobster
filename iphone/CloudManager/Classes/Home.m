/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "Home.h"
#import "ChannelBootstrapCommand.h"
#import "ShowList.h"
#import "AjaxShowList.h"
#import "AjaxGetListCommand.h"
#import "AppSession.h"
#import "Form.h"
#import "UICommandFramework.h"

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
	return 2;
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
			local.textLabel.text = @"CRUD/Sync Showcase";
		break;
			
		case 1:
			local.textLabel.text = @"Ajax Showcase";
		break;
			
		/*case 2:
			local.textLabel.text = @"Form Submission Showcase";
		break;*/
		
		/*case 3:
			local.textLabel.text = @"Command Framework Showcase";
		break;*/
			
		/*case 4:
			local.textLabel.text = @"Activate Device";
		break;*/
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
		CommandContext *commandContext = [CommandContext withInit:self];
		[commandContext setTarget:[ChannelBootstrapCommand withInit]];
		[commandContext setAttribute:@"indexPath" :indexPath];
		
		CommandService *service = [CommandService getInstance];
		[service execute:commandContext];		
	}
	else if(index == 1)
	{
		CommandContext *commandContext = [CommandContext withInit:self];
		[commandContext setTarget:[AjaxGetListCommand withInit]];
		[commandContext setAttribute:@"indexPath" :indexPath];
		
		CommandService *service = [CommandService getInstance];
		[service execute:commandContext];
	}
	/*else if(index == 2)
	{
		Form *modalView = [[Form alloc] initWithNibName:@"Form" bundle:nil];
		modalView.delegate = self;
		
		UINavigationController *navCtrl = [[UINavigationController alloc] initWithRootViewController:modalView];
		[modalView release];
		
		//Add the Title
		navCtrl.navigationBar.topItem.title = @"Form Submission";
		
		//Add the Done button to the navbar
		UIBarButtonItem *doneButton = [[UIBarButtonItem alloc] initWithTitle:@"Done" style:UIBarButtonItemStyleDone target:modalView action:@selector(done:)];
		navCtrl.topViewController.navigationItem.leftBarButtonItem = doneButton;
		[doneButton release];
		
		
		[self presentModalViewController:navCtrl animated:YES];
		[navCtrl release];		
	}
	else if(index == 3)
	{
		UICommandFramework *modalView = [[UICommandFramework alloc] initWithNibName:@"UICommandFramework" bundle:nil];
		modalView.delegate = self;
		
		UINavigationController *navCtrl = [[UINavigationController alloc] initWithRootViewController:modalView];
		[modalView release];
		
		//Add the Title
		navCtrl.navigationBar.topItem.title = @"Command Framework Demo";
		
		//Add the Done button to the navbar
		UIBarButtonItem *doneButton = [[UIBarButtonItem alloc] initWithTitle:@"Done" style:UIBarButtonItemStyleDone target:modalView action:@selector(done:)];
		navCtrl.topViewController.navigationItem.leftBarButtonItem = doneButton;
		[doneButton release];
		
		
		[self presentModalViewController:navCtrl animated:YES];
		[navCtrl release];
	}
	else if(index == 4)
	{
		UIKernel *uiKernel = [UIKernel getInstance];
		[uiKernel launchDeviceActivation:self];
	}*/
}
//------UICommandDelegate impl-----------------------------------------------------
-(void)doViewAfter:(CommandContext *)commandContext
{
	NSIndexPath *indexPath = [commandContext getAttribute:@"indexPath"];
	int index = indexPath.row;
	
	if(index == 0)
	{
		ShowList *modalView = [[ShowList alloc] initWithNibName:@"ShowList" bundle:nil];
		modalView.delegate = self;
	
		UINavigationController *navCtrl = [[UINavigationController alloc] initWithRootViewController:modalView];
		[modalView release];
	
		//Add the Title
		navCtrl.navigationBar.topItem.title = @"CRUD/Sync Showcase";
	
		//Add the Done button to the navbar
		UIBarButtonItem *doneButton = [[UIBarButtonItem alloc] initWithTitle:@"Done" style:UIBarButtonItemStyleDone target:modalView action:@selector(done:)];
		navCtrl.topViewController.navigationItem.leftBarButtonItem = doneButton;
		[doneButton release];
	
	
		[self presentModalViewController:navCtrl animated:YES];
		[navCtrl release];
	}
	else if(index == 1)
	{
		NSArray *emails = [commandContext getAttribute:@"emails"];
	
		AppSession *appSession = [AppSession getInstance];
		[appSession setAttribute:@"emails" :emails];
	
		AjaxShowList *modalView = [[AjaxShowList alloc] initWithNibName:@"AjaxShowList" bundle:nil];
		modalView.delegate = self;
	 
		UINavigationController *navCtrl = [[UINavigationController alloc] initWithRootViewController:modalView];
		[modalView release];
	 
		//Add the Title
		navCtrl.navigationBar.topItem.title = @"Ajax Showcase";
	 
		//Add the Done button to the navbar
		UIBarButtonItem *doneButton = [[UIBarButtonItem alloc] initWithTitle:@"Done" style:UIBarButtonItemStyleDone target:modalView action:@selector(done:)];
		navCtrl.topViewController.navigationItem.leftBarButtonItem = doneButton;
		[doneButton release];
	 
	 
		[self presentModalViewController:navCtrl animated:YES];
		[navCtrl release];
	}
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