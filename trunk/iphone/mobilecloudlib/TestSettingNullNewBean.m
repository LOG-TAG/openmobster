//
//  TestSettingNullNewBean.m
//  mobilecloudlib
//
//  Created by openmobster on 6/3/13.
//  Copyright (c) 2013 __MyCompanyName__. All rights reserved.
//

#import "TestSettingNullNewBean.h"
#import "MobileBean.h"

@implementation TestSettingNullNewBean

+(id)withInit
{
    return [[[TestSettingNullNewBean alloc] init] autorelease]; 
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
    [self setUp:@"settingnullnewbean"];
    [sync performBootSync:channel :NO];
    
    //NewBean
    MobileBean *bean = [MobileBean newInstance:channel];
    
    [bean setValue:@"to" :@"to@gmail.com"];
    [bean setValue:@"to" :nil];
    [bean setValue:@"newField" :nil];
    [bean save];
    
    NSString *beanId = [bean getId];
    
    //sync up the new data with the Cloud
    [sync performTwoWaySync:channel :NO];
    
    [sync performBootSync:channel :NO];
    
    [sync performStreamSync:channel :NO :beanId];
    
    MobileBean *mobileBean = [MobileBean readById:channel :beanId];
    NSString *context = [NSString stringWithFormat:@"%@/%@",[self getInfo],@"MobileBeanMustNotBeNull"];
    [self assertTrue:(mobileBean != nil) :context];
    
    NSString *to = [mobileBean getValue:@"to"];
    context = [NSString stringWithFormat:@"%@/%@",[self getInfo],@"ToMustBeNull"];
    [self assertTrue:to==nil :context];
    
    NSString *newField = [mobileBean getValue:@"newField"];
    context = [NSString stringWithFormat:@"%@/%@",[self getInfo],@"NewFieldMustBeNull"];
    [self assertTrue:newField==nil :context];
}
@end
