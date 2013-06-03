//
//  TestEmptyStringsFromCloud.m
//  mobilecloudlib
//
//  Created by openmobster on 5/31/13.
//  Copyright (c) 2013 __MyCompanyName__. All rights reserved.
//

#import "TestEmptyStringsFromCloud.h"
#import "SyncService.h"
#import "MobileBean.h"

@implementation TestEmptyStringsFromCloud 

+(id)withInit
{
   return [[[TestEmptyStringsFromCloud alloc] init] autorelease]; 
}

-(NSString *) getInfo
{
	NSString *info = [NSString stringWithFormat:@"%@/%@",[[self class] description],@"SetUpAPITestSuite"];
	return info;
}

-(void) runTest
{
    SyncService *sync = [SyncService getInstance];
    
    //Perform a BootSync to download the appropriate data
    [self setUp:@"emptystrings"];
    [sync performBootSync:channel :NO];
    
    //Read the MobileBean unique-8675309
    MobileBean *mobileBean = [MobileBean readById:channel :@"unique-8675309"];
    
    //assert nullness
    NSString *context = [NSString stringWithFormat:@"%@/%@",[self getInfo],@"MobileBeanMustNotBeNull"];
    [self assertTrue:(mobileBean != nil) :context];
    
    //assert the value of 'to'
    NSString *to = [mobileBean getValue:@"to"];
    context = [NSString stringWithFormat:@"%@/%@",[self getInfo],@"ToMustBeEmpty"];
    [self assertTrue:[to length]==0 :context];
    
    //Set a value for 'from'
    [mobileBean setValue:@"from" :@"from@gmail.com"];
    [mobileBean save];
    
    //sync up the new data with the Cloud
    [sync performTwoWaySync:channel :NO];
    
    //assert the value of from
    NSString *from = [mobileBean getValue:@"from"];
    context = [NSString stringWithFormat:@"%@/%@",[self getInfo],@"FromMustNotBeEmpty"];
    [self assertTrue:[from isEqualToString:@"from@gmail.com"] :context];
    
    //now bootsync and make sure the from value was synced with the Cloud
    [sync performBootSync:channel :NO];
    
    mobileBean = [MobileBean readById:channel :@"unique-8675309"];
    
    to = [mobileBean getValue:@"to"];
    context = [NSString stringWithFormat:@"%@/%@",[self getInfo],@"ToMustBeEmpty"];
    [self assertTrue:[to length]==0 :context];
    
    from = [mobileBean getValue:@"from"];
    context = [NSString stringWithFormat:@"%@/%@",[self getInfo],@"FromMustNotBeEmpty"];
    [self assertTrue:[from isEqualToString:@"from@gmail.com"] :context];
}
@end
