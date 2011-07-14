#import "Status.h"

@implementation Status

@synthesize cmdId;
@synthesize data;
@synthesize msgRef;
@synthesize cmdRef;
@synthesize cmd;
@synthesize targetRefs;
@synthesize sourceRefs;
@synthesize items;
@synthesize credential;

+(id) withInit
{
	Status *status = [[[Status alloc] init] autorelease];
	
	if(status.items == nil)
	{
		status.items = [NSMutableArray array];
	}
	
	if(status.sourceRefs == nil)
	{
		status.sourceRefs = [NSMutableArray array];
	}
	
	if(status.targetRefs == nil)
	{
		status.targetRefs = [NSMutableArray array];
	}
	
	return status;
}

-(void) addItem:(Item *) item
{
	[self.items addObject:item];
}

-(void) addSourceRef:(NSString *) sourceRef
{
	[self.sourceRefs addObject:sourceRef];
}

-(void) addTargetRef:(NSString *) targetRef
{
	[self.targetRefs addObject:targetRef];
}
@end