//
//  TestLargeObject.m
//  mobilecloudlib
//
//  Created by openmobster on 5/12/13.
//  Copyright (c) 2013 __MyCompanyName__. All rights reserved.
//

#import "TestLargeObject.h"
#import "MobileBean.h"
#import "SyncService.h"

@implementation TestLargeObject

+(id)withInit
{
  return [[[TestLargeObject alloc] init] autorelease];  
}

-(void) runTest
{
    NSMutableString *packetBuilder = [NSMutableString string];
    for(int i=0; i<1000; i++)
    {
        [packetBuilder appendString:@"a"];
    }
    NSMutableString *messageBuilder = [NSMutableString string];
    for(int i=0; i<100; i++)
    {
        [messageBuilder appendString:packetBuilder];
    }
    
    for(int i=0; i<3; i++)
    {
        MobileBean *largeBean = [MobileBean newInstance:@"large_object_channel"];
        [largeBean setValue:@"message" :messageBuilder];
        [largeBean save];
    }
    
    SyncService *sync = [SyncService getInstance];
    [sync performTwoWaySync:@"large_object_channel" :NO];
}


-(NSString *) getInfo
{
	NSString *info = [NSString stringWithFormat:@"%@%@",[[self class] description],@"TestLargeObject"];
	return info;
}

@end
