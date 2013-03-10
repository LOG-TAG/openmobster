#import "SyncBootstrapTests.h"


@implementation SyncBootstrapTests

-(void) testSyncBootstrap
{
	NSLog(@"Testing TestSyncBootstrap......");
	
	SyncAdapterRequest *request = [SyncAdapterRequest withInit];
	SyncAdapter *adapter = [SyncAdapter withInit];
	
	SyncAdapterResponse *response = [adapter service:request];
}

@end
