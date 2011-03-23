#import "SyncMessage.h"


@implementation SyncMessage

@synthesize messageId;
@synthesize maxClientSize;
@synthesize final;
@synthesize clientInitiated;
@synthesize alerts;
@synthesize status;
@synthesize syncCommands;
@synthesize recordMap;
@synthesize credential;

+(id) withInit
{
	SyncMessage *message = [[[SyncMessage alloc] init] autorelease];
	
	if(message.alerts == nil)
	{
		message.alerts = [NSMutableArray array];
	}
	
	if(message.status == nil)
	{
		message.status = [NSMutableArray array];
	}
	
	if(message.syncCommands == nil)
	{
		message.syncCommands = [NSMutableArray array];
	}
	
	return message;
}


-(void) addAlert:(Alert *)alert
{
	[self.alerts addObject:alert];
}


-(void) addStatus:(Status *)incoming
{	
	[self.status addObject:incoming];	
}

-(void) addCommand:(SyncCommand *)command
{
	[self.syncCommands addObject:command];	
}

@end
