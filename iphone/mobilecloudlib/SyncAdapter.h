#import <Foundation/Foundation.h>
#import "WorkflowManager.h"
#import "SyncAdapterRequest.h"
#import "SyncAdapterResponse.h"


@interface SyncAdapter : NSObject 
{
	@private
	WorkflowManager *manager;
}

+(id) withInit;

-(SyncAdapterResponse *) service:(SyncAdapterRequest *) request;

//Used internally
-(void)storeIncomingPayload:(Session *)session :(SyncMessage *)message;
-(NSString *)storeOutgoingPayload:(Session *)session :(SyncMessage *)message;

@end
