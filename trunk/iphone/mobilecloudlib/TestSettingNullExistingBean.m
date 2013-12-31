//
//  TestSettingNullExistingBean.m
//  mobilecloudlib
//
//  Created by openmobster on 6/3/13.
//  Copyright (c) 2013 __MyCompanyName__. All rights reserved.
//

#import "TestSettingNullExistingBean.h"
#import "MobileBean.h"

@implementation TestSettingNullExistingBean

+(id)withInit
{
    return [[[TestSettingNullExistingBean alloc] init] autorelease]; 
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
    [self setUp:@"settingnullexistingbean"];
    [sync performBootSync:channel :NO];
    
    //Read the MobileBean unique-8675309
    MobileBean *mobileBean = [MobileBean readById:channel :@"unique-8675309"];
    
    //assert nullness
    NSString *context = [NSString stringWithFormat:@"%@/%@",[self getInfo],@"MobileBeanMustNotBeNull"];
    [self assertTrue:(mobileBean != nil) :context];
    
    [mobileBean setValue:@"to" :nil];
    [mobileBean setValue:@"newField" :nil];
    [mobileBean save];
    
    //sync up the new data with the Cloud
    [sync performTwoWaySync:channel :NO];
    
    [sync performBootSync:channel :NO];
    
    mobileBean = [MobileBean readById:channel :@"unique-8675309"];
    context = [NSString stringWithFormat:@"%@/%@",[self getInfo],@"MobileBeanMustNotBeNull"];
    [self assertTrue:(mobileBean != nil) :context];
    
    NSString *to = [mobileBean getValue:@"to"];
    context = [NSString stringWithFormat:@"%@/%@",[self getInfo],@"ToMustBeNull"];
    [self assertTrue:to==nil :context];
    
    NSString *newField = [mobileBean getValue:@"newField"];
    context = [NSString stringWithFormat:@"%@/%@",[self getInfo],@"NewFieldMustBeNull"];
    [self assertTrue:newField==nil :context];
}
@end
