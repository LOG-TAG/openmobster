#import "WorkflowTests.h"
#import "Node.h"
#import "Process.h"
#import "SimpleActionHandler.h"
#import "SimpleDecisionHandler.h"


@implementation WorkflowTests

- (void) testSimpleWorkflow 
{
    NSLog(@"Starting testSimpleWorkflow..........");
	
	//Prepare the workflow nodes
	Node *startNode = [[Node alloc] init];
	Node *actionNode = [[Node alloc] init];
	Node *decisionNode = [[Node alloc] init];
	Node *endNode = [[Node alloc] init];
	NSMutableArray *otherNodes = [NSMutableArray array];
	SimpleActionHandler *actionHandler = [[SimpleActionHandler alloc] init];
	SimpleDecisionHandler *decisionHandler = [[SimpleDecisionHandler alloc] init];
	
	//Prepare the startNode
	startNode.activeTransition = @"act";
	
	//Prepare the endNode
	endNode.name = @"end";
	
	//Prepare action node
	actionNode.name = @"act";
	actionNode.handler = actionHandler;
	[otherNodes addObject:actionNode];
	
	//Prepare decision node
	decisionNode.name = @"decide";
	decisionNode.handler = decisionHandler;
	[otherNodes addObject:decisionNode];
	
	//Start the workflow and process it
	Process *process = [Process withConfiguration:startNode endNode:endNode otherNodes:otherNodes];
		
	while(![process signal]);
	
	[startNode autorelease];
	[actionNode autorelease];
	[decisionNode autorelease];
	[endNode autorelease];
	[actionHandler autorelease];
	[decisionHandler autorelease];
}

//This test has a compilation error. DecideSyncScenario and PerformNormalSync are missing!!!
/*
-(void) testSimpleSyncWorkflow
{
	NSLog(@"Starting testSimpleSyncWorkflow.......");
	
	NSMutableArray *syncNodes = [NSMutableArray array];
	
	//Start Node
	Node *startNode = [[[Node alloc] init] autorelease];
	startNode.activeTransition = @"handshake";
	
	//End Node
	Node *endNode = [[[Node alloc] init] autorelease];
	endNode.name = @"end";
	
	//Handshake node
	Node *handshake = [[[Node alloc] init] autorelease];
	handshake.name = @"handshake";
	handshake.handler = [[[StartHandshake alloc] init] autorelease];
	[syncNodes addObject:handshake];
	
	//StartSync Node
	Node *startSync = [[[Node alloc] init] autorelease];
	startSync.name = @"start_sync";
	startSync.handler = [[[StartSync alloc] init] autorelease];
	[syncNodes addObject:startSync];
	
	//DecideSyncScenario Node
	Node *decideSync = [[[Node alloc] init] autorelease];
	decideSync.name = @"decide_sync";
	decideSync.handler = [[[DecideSyncScenario alloc] init] autorelease];
	[syncNodes addObject:decideSync];
	
	//PerformNormal Sync 
	Node *normalSync = [[[Node alloc] init] autorelease];
	normalSync.name = @"normal";
	normalSync.handler = [[[PerformNormalSync alloc] init] autorelease];
	[syncNodes addObject:normalSync];
	
	//DecideEnd Sync
	Node *decideEndSync = [[[Node alloc] init] autorelease];
	decideEndSync.name = @"decide_end";
	decideEndSync.handler = [[[DecideEndSync alloc] init] autorelease];
	[syncNodes addObject:decideEndSync];
	
	//Start Close
	Node *startClose = [[[Node alloc] init] autorelease];
	startClose.name = @"start_close";
	startClose.handler = [[[StartClose alloc] init] autorelease];
	[syncNodes addObject:startClose];
	
	
	//Start the workflow and process it
	Process *process = [Process withConfiguration:startNode endNode:endNode otherNodes:syncNodes];

	while(![process signal]);
}
*/
@end
