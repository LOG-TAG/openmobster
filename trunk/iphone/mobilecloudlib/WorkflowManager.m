#import "WorkflowManager.h"

@implementation WorkflowManager

-init
{
	if(self = [super init])
	{
		NSMutableArray *syncNodes = [NSMutableArray array];
		
		//Start Node
		Node *startNode = [Node withInit];
		startNode.activeTransition = @"handshake";
		
		//Handshake node
		Node *handshake = [Node withInit];
		handshake.name = @"handshake";
		handshake.handler = [StartHandshake withInit];
		[syncNodes addObject:handshake];
		
		//Phase Decision Node
		Node *endInit = [Node withInit];
		endInit.name = @"decide_end_init";
		endInit.handler = [DecideEndInit withInit];
		[syncNodes addObject:endInit];
		
		//StartSync Node
		Node *startSync = [Node withInit];
		startSync.name = @"start_sync";
		startSync.handler = [StartSync withInit];
		[syncNodes addObject:startSync];
		
		//Phase Decision Node
		Node *endSync = [Node withInit];
		endSync.name = @"decide_end_sync";
		endSync.handler = [DecideEndSync withInit];
		[syncNodes addObject:endSync];
		
		//Start Close
		Node *startClose = [Node withInit];
		startClose.name = @"start_close";
		startClose.handler = [StartClose withInit];
		[syncNodes addObject:startClose];
		
		//Phase Decision Node
		Node *endClose = [Node withInit];
		endClose.name = @"decide_end_close";
		endClose.handler = [DecideEndClose withInit];
		[syncNodes addObject:endClose];
		
		//Close Session Node
		Node *closeSession = [Node withInit];
		closeSession.name = @"close_session";
		closeSession.handler = [CloseSession withInit];
		[syncNodes addObject:closeSession];
		
		//End Node
		Node *endNode = [Node withInit];
		endNode.name = @"end";
		
		process = [Process withConfiguration:startNode endNode:endNode otherNodes:syncNodes];
	}
	
	return self;
}

+(id) withInit
{
	return [[[WorkflowManager alloc] init] autorelease];
}

-(Context *) activeContext
{
	return process.activeContext;
}


-(Session *) activeSession
{
	Context *activeContext = [self activeContext];
	Session *activeSession = (Session *)[activeContext getAttribute:_SESSION];
	if(activeSession == nil)
	{
		activeSession = [Session withInit];
		[activeContext setAttribute:_SESSION :activeSession];
	}
	
	return activeSession;
}

-(SyncEngine *)engine
{
	Context *activeContext = [self activeContext];
	SyncEngine *engine = (SyncEngine *)[activeContext getAttribute:_SYNC_ENGINE];
	if(engine == nil)
	{
		engine = [SyncEngine withInit];
		[activeContext setAttribute:_SYNC_ENGINE :engine];
	}
	return engine;
}

-(SyncObjectGenerator *)objectGenerator
{
	Context *activeContext = [self activeContext];
	SyncObjectGenerator *service = (SyncObjectGenerator *)[activeContext getAttribute:_SYNC_OBJECT_GENERATOR];
	if(service == nil)
	{
		service = [SyncObjectGenerator withInit];
		[activeContext setAttribute:_SYNC_OBJECT_GENERATOR :service];
	}
	return service;
}

-(NSString *) activeNode
{
	return [process currentNodeName];
}

-(BOOL) sendSyncNotification
{
	return [process signal];
}

@end
