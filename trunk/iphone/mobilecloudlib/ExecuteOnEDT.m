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
	id target = [commandContext getTarget];
	
	if([target conformsToProtocol:@protocol(RemoteCommand)] ||
	   [target conformsToProtocol:@protocol(BusyCommand)]
	)
	{
		[self startBusyCommand];
	}
	else if([target conformsToProtocol:@protocol(AsyncCommand)])
	{
		[self startAsyncCommand];
	}
	else if([target conformsToProtocol:@protocol(LocalCommand)])
	{
		[self startLocalCommand];
	}
}
//----Busy Command implementation...pops up a busy spinner and disables interaction--------------
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
	@catch(AppException *appe)
	{
		[commandContext setAppException:appe];
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
			AppException *appe = [commandContext getAppException];
			if(appe == nil)
			{
				[local doViewAfter:commandContext];
			}
			else 
			{
				[local doViewAppException:commandContext];
			}
		}
		else 
		{
			[local doViewError:commandContext];
		}
	}
}
//--------Asynccommand implementation...performs actions Asynchronously (Ajaxian)----------------
-(void)startAsyncCommand
{
	NSInvocationOperation *operation = [[NSInvocationOperation alloc] initWithTarget:self selector:@selector(invokeAsyncAction) object:nil];	
	operation = [operation autorelease];
	
	//Add the operation to the queue
	[queue addOperation:operation];
}

-(void)invokeAsyncAction
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
	@catch(AppException *appe)
	{
		[commandContext setAppException:appe];
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
	[self performSelectorOnMainThread:@selector(endAsyncCommand) withObject:nil waitUntilDone:NO];
}

-(void)endAsyncCommand
{
	//Stop the Activity Indicator
	UIViewController *caller = commandContext.caller;
	
	if([caller conformsToProtocol:@protocol(UICommandDelegate)])
	{
		UIViewController<UICommandDelegate> *local = (UIViewController<UICommandDelegate> *)caller;
		
		if(![commandContext hasErrors])
		{
			AppException *appe = [commandContext getAppException];
			if(appe == nil)
			{
				[local doViewAfter:commandContext];
			}
			else 
			{
				[local doViewAppException:commandContext];
			}
		}
		else 
		{
			[local doViewError:commandContext];
		}
	}
}
//-------Execution of 'LocalCommand'...note: make sure this executes fast as this will freeze the 
//UI. This is used to read local data, such as locally synced channel data etc
-(void)startLocalCommand
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
	@catch(AppException *appe)
	{
		[commandContext setAppException:appe];
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
	[self endLocalCommand];
}

-(void)endLocalCommand
{
	//Stop the Activity Indicator
	UIViewController *caller = commandContext.caller;
	
	if([caller conformsToProtocol:@protocol(UICommandDelegate)])
	{
		UIViewController<UICommandDelegate> *local = (UIViewController<UICommandDelegate> *)caller;
		
		if(![commandContext hasErrors])
		{
			AppException *appe = [commandContext getAppException];
			if(appe == nil)
			{
				[local doViewAfter:commandContext];
			}
			else 
			{
				[local doViewAppException:commandContext];
			}
		}
		else 
		{
			[local doViewError:commandContext];
		}
	}
}
@end
