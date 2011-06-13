/**
 * Your Copyright Here
 *
 * Appcelerator Titanium is Copyright (c) 2009-2010 by Appcelerator, Inc.
 * and licensed under the Apache Public License (version 2)
 */
#import "OrgOpenmobsterCloudModule.h"
#import "TiBase.h"
#import "TiHost.h"
#import "TiUtils.h"
#import "SyncProxy.h"
#import "TitaniumKernel.h"

@implementation OrgOpenmobsterCloudModule

#pragma mark Internal

// this is generated for your module, please do not change it
-(id)moduleGUID
{
	return @"cd675829-a15b-4c7b-9973-47809eb6136a";
}

// this is generated for your module, please do not change it
-(NSString*)moduleId
{
	return @"org.openmobster.cloud";
}

#pragma mark Lifecycle

-(void)startup
{
	// this method is called when the module is first loaded
	// you *must* call the superclass
	[super startup];
	
	//[TitaniumKernel startApp];
	
	NSLog(@"OpenMobster Cloud Module Loaded.....");
}

-(void)shutdown:(id)sender
{
	// this method is called when the module is being unloaded
	// typically this is during shutdown. make sure you don't do too
	// much processing here or the app will be quit forceably
	
	// you *must* call the superclass
	[super shutdown:sender];
	
	TitaniumKernel *kernel = [TitaniumKernel getInstance];
	if(kernel != nil)
	{
		[kernel stop];
	}
	
	NSLog(@"OpenMobster Cloud Module Shutdown.....");
}

#pragma mark Cleanup 

-(void)dealloc
{
	// release any resources that have been retained by the module
	[super dealloc];
}

#pragma mark Internal Memory Management

-(void)didReceiveMemoryWarning:(NSNotification*)notification
{
	// optionally release any resources that can be dynamically
	// reloaded once memory is available - such as caches
	[super didReceiveMemoryWarning:notification];
}

#pragma mark Listener Notifications

-(void)_listenerAdded:(NSString *)type count:(int)count
{
	if (count == 1 && [type isEqualToString:@"my_event"])
	{
		// the first (of potentially many) listener is being added 
		// for event named 'my_event'
	}
}

-(void)_listenerRemoved:(NSString *)type count:(int)count
{
	if (count == 0 && [type isEqualToString:@"my_event"])
	{
		// the last listener called for event named 'my_event' has
		// been removed, we can optionally clean up any resources
		// since no body is listening at this point for that event
	}
}

#pragma Public APIs

-(id)example:(id)args
{
	// example method
	
	NSLog(@"Example Invoked****************");
	
	return @"hello world";
}

-(id)exampleProp
{
	// example property getter
	return @"hello world";
}

-(void)exampleProp:(id)value
{
	// example property setter
}

-(id)sync:(id)args
{
	SyncProxy *syncProxy = [SyncProxy withInit];
	return syncProxy;
}
@end
