//
//  SyncTests.m
//  mobilecloudlib
//
//  Created by openmobster on 11/11/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

#import "SyncTests.h"


@implementation SyncTests

-(void)testAllSyncTypes 
{
 Bootstrapper *bootstrap = [Bootstrapper withInit];
 TestSuite *suite = [bootstrap bootstrap:@"192.168.1.103"];
 
 //Prepare the TestContext
 TestContext *context = suite.context;
 [context setAttribute:@"channel" :@"testServerBean"];
 
 //Prepare the tests to be executed
 [suite addTest:[TestSlowSync withInit]];
 [suite addTest:[TestBootSync withInit]];
 [suite addTest:[TestTwoWaySync withInit]];
 [suite addTest:[TestOneWayServerSync withInit]];
 [suite addTest:[TestOneWayClientSync withInit]];
 
 //TODO: Write this test...not a show stopper
 //[suite addTest:[TestObjectStreaming withInit]];
 
 
 //Start test execution
 [suite execute];
}

-(void)testSyncScheduler
{
	NSLog(@"Executing testSyncScheduler.....");
    
	Kernel *kernel = [Kernel getInstance];
	[kernel startup];
	
	SyncScheduler *syncScheduler = [SyncScheduler getInstance];
	
	for(int i=0; i<10; i++)
	{
		[syncScheduler startBackgroundSync];
	}
	
	//sleep for 5 seconds before exiting
	[NSThread sleepForTimeInterval:5];
	
	
	[kernel shutdown];
}

/*
-(void)testBootSync 
{
    Bootstrapper *bootstrap = [Bootstrapper withInit];
	TestSuite *suite = [bootstrap bootstrap:@"192.168.1.107"];
	
	//Prepare the TestContext
	TestContext *context = suite.context;
	[context setAttribute:@"channel" :@"testServerBean"];
	
	//Prepare the tests to be executed
	[suite addTest:[TestBootSync withInit]];
	
	
	//Start test execution
	[suite execute];
}
*/
/*
-(void)testTwoWaySync 
{
    Bootstrapper *bootstrap = [Bootstrapper withInit];
	TestSuite *suite = [bootstrap bootstrap:@"192.168.1.101"];
	
	//Prepare the TestContext
	TestContext *context = suite.context;
	[context setAttribute:@"channel" :@"testServerBean"];
	
	//Prepare the tests to be executed
	[suite addTest:[TestTwoWaySync withInit]];
	
	
	//Start test execution
	[suite execute];
}
*/
/*
-(void)testOneWayClientSync 
{
    Bootstrapper *bootstrap = [Bootstrapper withInit];
	TestSuite *suite = [bootstrap bootstrap:@"192.168.1.107"];
	
	//Prepare the TestContext
	TestContext *context = suite.context;
	[context setAttribute:@"channel" :@"testServerBean"];
	
	//Prepare the tests to be executed
	[suite addTest:[TestOneWayClientSync withInit]];
	
	
	//Start test execution
	[suite execute];
}
*/
/*
-(void)testOneWayServerSync 
{
    Bootstrapper *bootstrap = [Bootstrapper withInit];
	TestSuite *suite = [bootstrap bootstrap:@"192.168.1.107"];
	
	//Prepare the TestContext
	TestContext *context = suite.context;
	[context setAttribute:@"channel" :@"testServerBean"];
	
	//Prepare the tests to be executed
	[suite addTest:[TestOneWayServerSync withInit]];
	
	
	//Start test execution
	[suite execute];
}
*/
/*
-(void)testSlowSync 
{
    Bootstrapper *bootstrap = [Bootstrapper withInit];
	TestSuite *suite = [bootstrap bootstrap:@"192.168.1.107"];
	
	//Prepare the TestContext
	TestContext *context = suite.context;
	[context setAttribute:@"channel" :@"testServerBean"];
	
	//Prepare the tests to be executed
	[suite addTest:[TestSlowSync withInit]];
	
	
	//Start test execution
	[suite execute];
}
*/
/*
-(void)testStreamSync 
{
 Bootstrapper *bootstrap = [Bootstrapper withInit];
 TestSuite *suite = [bootstrap bootstrap:@"192.168.1.103"];
 
 //Prepare the TestContext
 TestContext *context = suite.context;
 [context setAttribute:@"channel" :@"testServerBean"];
 
 //Prepare the tests to be executed
 [suite addTest:[TestObjectStreaming withInit]];
 
 
 //Start test execution
 [suite execute];
}
*/
@end