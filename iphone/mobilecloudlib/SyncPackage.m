#import "SyncPackage.h"

@implementation SyncPackage

@synthesize messages;

+(id) withInit
{
	SyncPackage *syncPackage = [[[SyncPackage alloc] init] autorelease];
	
	if(syncPackage.messages == nil)
	{
		syncPackage.messages = [NSMutableArray array];
	}
	
	return syncPackage;
}

-(void) addMessage:(SyncMessage *)message
{
	[self.messages addObject:message];
}

-(SyncMessage *) findMessage:(NSString *)messageId
{
	for(SyncMessage *message in self.messages)
	{
		if([message.messageId isEqualToString:messageId])
		{
			return message;
		}
	}
	return nil;
}

@end
