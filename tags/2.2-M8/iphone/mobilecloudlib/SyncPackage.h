#import <Foundation/Foundation.h>
#import "SyncMessage.h"


@interface SyncPackage : NSObject 
{
	@private
	NSMutableArray *messages;//SyncMessage instances
}

+(id) withInit;

@property (assign) NSMutableArray *messages;

-(void) addMessage:(SyncMessage *)message;
-(SyncMessage *) findMessage:(NSString *)messageId;

@end
