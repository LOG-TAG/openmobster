/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "ExecuteOnEDT.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation ExecuteOnEDT

@synthesize commandContext;

-(id)initWithContext:(CommandContext *)context
{
	if([super init] == self)
	{
		queue = [[NSOperationQueue alloc] init];
		self.commandContext = context;
	}
	return self;
}

-(void)dealloc
{
	[queue release];
	[commandContext release];
	if(activityIndicatorView != nil)
	{
		[activityIndicatorView release];
	}
	[super dealloc];
}

+(id)withInit:(CommandContext *)commandContext
{
	ExecuteOnEDT *instance = [[ExecuteOnEDT alloc] initWithContext:commandContext];
	instance = [instance autorelease];
	
	return instance;
}

-(void)run
{
	@try 
	{
		[self startBusyCommand];
	}
	@catch (NSException * e) 
	{
		//FIXME: If this happens show a Global Error Message
	}
}

-(void)startBusyCommand
{	
	//Start the Activity Indicators
	activityIndicatorView = [[ActivityIndicatorView alloc] initWithNibName:@"ActivityIndicatorView" bundle:nil];
	UIViewController *caller = commandContext.caller;
	
	//Disable the screen from interactions
	[caller.view setUserInteractionEnabled:NO];
	if(caller.navigationController != nil)
	{
		[caller.navigationController.view setUserInteractionEnabled:NO];
	}
	if(caller.parentViewController != nil)
	{
		[caller.parentViewController.view setUserInteractionEnabled:NO];
	}
	if(caller.tabBarController != nil)
	{
		[caller.tabBarController.view setUserInteractionEnabled:NO];
	}
	
	[caller.view addSubview:activityIndicatorView.view];
	[caller.view bringSubviewToFront:activityIndicatorView.view];
	
	NSInvocationOperation *operation = [[NSInvocationOperation alloc] initWithTarget:self selector:@selector(invokeBusyAction) object:nil];	
	operation = [operation autorelease];
	
	//Add the operation to the queue
	[queue addOperation:operation];
}

-(void)invokeBusyAction
{
	@try
	{
		id<Command> target = (id<Command>)[commandContext getTarget];
		[target doAction:commandContext];
	}
	@catch(SystemException *syse)
	{
		[commandContext setError:@"500" :[syse getMessage]];
	}
	@catch (NSException *e) 
	{
		NSString *message = @"No Message Found";
		if([e reason] != nil)
		{
			message = [e reason];
		}
		else if([e name] != nil)
		{
			message = [e name];
		}
		[commandContext setError:@"500" :message];	
	}
	[self performSelectorOnMainThread:@selector(endBusyCommand) withObject:nil waitUntilDone:NO];
}

-(void)endBusyCommand
{
	@try 
	{
		//Stop the Activity Indicator
		[activityIndicatorView.view removeFromSuperview];
		UIViewController *caller = commandContext.caller;
	
		if(caller.navigationController != nil)
		{
			[caller.navigationController.view setUserInteractionEnabled:YES];
		}
		if(caller.parentViewController != nil)
		{
			[caller.parentViewController.view setUserInteractionEnabled:YES];
		}
		if(caller.tabBarController != nil)
		{
			[caller.tabBarController.view setUserInteractionEnabled:YES];
		}
		[caller.view setUserInteractionEnabled:YES];
	
		if([caller conformsToProtocol:@protocol(UICommandDelegate)])
		{
			UIViewController<UICommandDelegate> *local = (UIViewController<UICommandDelegate> *)caller;
			
			if(![commandContext hasErrors])
			{
				[local doViewAfter:commandContext];
			}
			else 
			{
				[local doViewError:commandContext];
			}
		}
	}
	@catch (NSException * e) 
	{
		//FIXME: If this happens show a Global Error Message
	}
}
@end
