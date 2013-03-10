#import <Foundation/Foundation.h>
#import "Node.h"
#import "Process.h"
#import "StartSync.h"
#import "StartHandshake.h"
#import "DecideEndInit.h"
#import "DecideEndSync.h"
#import "DecideEndClose.h"
#import "StartClose.h"
#import "CloseSession.h"
#import "Context.h"
#import "SyncConstants.h"
#import "Session.h"
#import "SyncEngine.h"
#import "SyncObjectGenerator.h"


@interface WorkflowManager : NSObject 
{
	@private
	Process *process;
}

+(id) withInit;

-(Context *) activeContext;
-(NSString *) activeNode;

-(Session *) activeSession;
-(SyncEngine *)engine;
-(SyncObjectGenerator *)objectGenerator;

-(BOOL) sendSyncNotification;

@end
