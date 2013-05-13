
#import "MobileBeanTestSuite.h"
#import "TestReadOnlyChannel.h"
#import "TestLocationService.h"
#import "TestLargeObject.h"


@implementation MobileBeanTestSuite

-(void) testAll 
{
	Bootstrapper *bootstrap = [Bootstrapper withInit];
	TestSuite *suite = [bootstrap bootstrap:@"192.168.1.108"];
	
	//Prepare the TestContext
	TestContext *context = suite.context;
	[context setAttribute:@"channel" :@"testServerBean"];
	
	//Prepare the suite
	[suite addTest:[TestSimpleAccess withInit]];
	[suite addTest:[TestArrayAccess withInit]];
    [suite addTest:[TestCRUD withInit]];
    [suite addTest:[TestLargeObject withInit]];
    
    
    //[suite addTest:[TestLocationService withInit]];
	//[suite addTest:[TestReadOnlyChannel withInit]]; @Deprecate:This feature is removed. Channels are not readonly and stuff anymore
	
	//execute the suite
	[suite execute];
}
@end
