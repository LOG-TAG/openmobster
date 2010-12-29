/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "CloudManager.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation CloudManager

+(void)modalActivateDevice:(UIViewController *)caller
{
	ModalActivateDevice *modalView = [[ModalActivateDevice alloc] initWithNibName:@"ModalActivateDevice" bundle:nil];
	modalView.delegate = caller;
	
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
	
	
	[caller presentModalViewController:navCtrl animated:YES];
	[navCtrl release];
}

+(void)modalForceActivation:(UIViewController *)caller
{
	ModalActivateDevice *modalView = [[ModalActivateDevice alloc] initWithNibName:@"ModalActivateDevice" bundle:nil];
	modalView.delegate = caller;
	[modalView forceActivation];
	
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
	
	
	[caller presentModalViewController:navCtrl animated:YES];
	[navCtrl release];
}

+(void)modalSecurityConfig:(UIViewController *)caller
{
	SecurityConfig *modalView = [[SecurityConfig alloc] initWithNibName:@"SecurityConfig" bundle:nil];
	modalView.delegate = caller;
	
	UINavigationController *navCtrl = [[UINavigationController alloc] initWithRootViewController:modalView];
	[modalView release];
	
	//Add the Title
	navCtrl.navigationBar.topItem.title = @"Security";
	
	//Add the Cancel button to the navbar
	UIBarButtonItem *cancelButton = [[UIBarButtonItem alloc] initWithTitle:@"Cancel" style:UIBarButtonItemStyleDone target:modalView action:@selector(cancel:)];
	navCtrl.topViewController.navigationItem.leftBarButtonItem = cancelButton;
	[cancelButton release];
	
	
	[caller presentModalViewController:navCtrl animated:YES];
	[navCtrl release];
}

+(void)modalCloudManager:(UIViewController *)caller
{
	CloudManagerApp *modalView = [[CloudManagerApp alloc] initWithNibName:@"CloudManagerApp" bundle:nil];
	modalView.delegate = caller;
	
	UINavigationController *navCtrl = [[UINavigationController alloc] initWithRootViewController:modalView];
	[modalView release];
	
	//Add the Title
	navCtrl.navigationBar.topItem.title = @"Cloud Manager";
	
	//Add the Cancel button to the navbar
	UIBarButtonItem *doneButton = [[UIBarButtonItem alloc] initWithTitle:@"Done" style:UIBarButtonItemStyleDone target:modalView action:@selector(done:)];
	navCtrl.topViewController.navigationItem.leftBarButtonItem = doneButton;
	[doneButton release];
	
	
	[caller presentModalViewController:navCtrl animated:YES];
	[navCtrl release];
}
@end
