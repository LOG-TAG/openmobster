
#import "MobileBeanTestSuite.h"
#import "TestReadOnlyChannel.h"


@implementation MobileBeanTestSuite

-(void) testAll 
{
	Bootstrapper *bootstrap = [Bootstrapper withInit];
	TestSuite *suite = [bootstrap bootstrap:@"192.168.1.103"];
	
	//Prepare the TestContext
	TestContext *context = suite.context;
	[context setAttribute:@"channel" :@"testServerBean"];
	
	//Prepare the suite
	[suite addTest:[TestSimpleAccess withInit]];
	[suite addTest:[TestArrayAccess withInit]];
	[suite addTest:[TestCRUD withInit]];
	[suite addTest:[TestReadOnlyChannel withInit]];
	
	//execute the suite
	[suite execute];
}
@end
