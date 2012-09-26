#import "Alert.h"

@implementation Alert

@synthesize cmdId;
@synthesize data;
@synthesize items;

+(id) withInit
{
	Alert *alert = [[[Alert alloc] init] autorelease];
	
	if(alert.items == nil)
	{
		alert.items = [NSMutableArray array];
	}	
	
	return alert;
}

-(void) addItem:(Item *) item
{
	[self.items addObject:item];
}

@end
