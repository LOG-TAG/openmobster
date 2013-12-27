
#import "MobileBeanTestSuite.h"
#import "TestReadOnlyChannel.h"
#import "TestLocationService.h"
#import "TestLargeObject.h"
#import "TestEmptyStringsFromCloud.h"
#import "TestNullValuesFromCloud.h"
#import "TestSettingNullExistingBean.h"
#import "TestSettingNullNewBean.h"


@implementation MobileBeanTestSuite

-(void) testAll 
{
	Bootstrapper *bootstrap = [Bootstrapper withInit];
	TestSuite *suite = [bootstrap bootstrap:@"192.168.1.104"];
	
	//Prepare the TestContext
	TestContext *context = suite.context;
	[context setAttribute:@"channel" :@"testServerBean"];
	
	//Prepare the suite
	[suite addTest:[TestSimpleAccess withInit]];
	[suite addTest:[TestArrayAccess withInit]];
    [suite addTest:[TestCRUD withInit]];
    [suite addTest:[TestLargeObject withInit]];
    [suite addTest:[TestEmptyStringsFromCloud withInit]];
    [suite addTest:[TestNullValuesFromCloud withInit]];
    [suite addTest:[TestSettingNullExistingBean withInit]];
    [suite addTest:[TestSettingNullNewBean withInit]];
    
    //[suite addTest:[TestLocationService withInit]];
	//[suite addTest:[TestReadOnlyChannel withInit]]; @Deprecate:This feature is removed. Channels are not readonly and stuff anymore
	
	//execute the suite
	[suite execute];
}
@end
