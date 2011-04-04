//
//  SyncAppViewController.m
//  SyncApp
//
//  Created by openmobster on 3/31/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import "SyncAppViewController.h"
#import "MobileBean.h"
#import "SyncService.h"

@implementation SyncAppViewController

@synthesize beanList;

/*
// The designated initializer. Override to perform setup that is required before the view is loaded.
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil {
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}
*/

/*
// Implement loadView to create a view hierarchy programmatically, without using a nib.
- (void)loadView {
}
*/


/*
// Implement viewDidLoad to do additional setup after loading the view, typically from a nib.
- (void)viewDidLoad {
    [super viewDidLoad];
}
*/


/*
// Override to allow orientations other than the default portrait orientation.
- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation {
    // Return YES for supported orientations
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}
*/

- (void)didReceiveMemoryWarning {
	// Releases the view if it doesn't have a superview.
    [super didReceiveMemoryWarning];
	
	// Release any cached data, images, etc that aren't in use.
}

- (void)viewDidUnload {
	// Release any retained subviews of the main view.
	// e.g. self.myOutlet = nil;
}


- (void)dealloc {
	[beanList release];
    [super dealloc];
}
//------UITableView related operations------------------------------------------------
-(NSInteger) tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
	//Read the synchronized beans from the channel
	NSArray *beans = [MobileBean readAll:@"sync_bean_channel"];
	
	if(beans == nil || [beans count] == 0)
	{
		channelIsEmpty = YES;
		return 1;
	}
	else 
	{
		channelIsEmpty = NO;
		return [beans count];
	}
}

-(UITableViewCell *) tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
	//Prepare the table cell 
	UITableViewCell *local = [tableView dequeueReusableCellWithIdentifier:@"sync_bean"];
	if(local == nil)
	{
		local = [[UITableViewCell alloc] initWithFrame:CGRectZero reuseIdentifier:@"sync_bean"];
		local = [local autorelease];
	}
	
	//Check in case the channel is empty and needs manual synchronization
	if(channelIsEmpty)
	{
		local.textLabel.text = @"Sync Manually";
		return local;
	}
	
	//Read the synchronized beans from the channel
	NSArray *beans = [MobileBean readAll:@"sync_bean_channel"];
	
	//Find the Bean in question
	int index = indexPath.row;
	MobileBean *bean = [beans objectAtIndex:index];
	
	//Display the 'value1' field of the bean
	NSString *display = [bean getValue:@"value1"];
	local.textLabel.text = display;
	
	return local;
	
}

-(void) tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
	//Handle selection of the item
	[tableView deselectRowAtIndexPath:indexPath animated:YES];
	[tableView scrollToRowAtIndexPath:indexPath atScrollPosition:UITableViewScrollPositionMiddle animated:YES];
	
	//Check if the channel is empty and needs manual synchronization
	if(channelIsEmpty)
	{
		SyncService *syncService = [SyncService getInstance];
		[syncService performBootSync:@"sync_bean_channel" :NO];
		channelIsEmpty = NO;
		
		//reload the table
		[beanList reloadData];
		
		return;
	}
	
	//Read all the beans
	NSArray *beans = [MobileBean readAll:@"sync_bean_channel"];
	
	//Get the selected bean
	int index = indexPath.row;
	MobileBean *bean = [beans objectAtIndex:index];
	
	//Get the value of all the fields
	NSString *value1 = [bean getValue:@"value1"];
	NSString *value2 = [bean getValue:@"value2"];
	NSString *value3 = [bean getValue:@"value3"];
	NSString *message = [NSString stringWithFormat:@"Value2=%@, Value3=%@",value2,value3];
	
	//Setup the Dialog Box
	UIAlertView *dialog = [[UIAlertView alloc] initWithTitle:value1
													 message:message 
													delegate:self cancelButtonTitle:@"OK" 
										   otherButtonTitles:nil];
	
	//Display the dialog
	dialog = [dialog autorelease];
	[dialog show];
}
@end
