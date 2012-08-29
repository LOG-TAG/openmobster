//
//  SyncUtility.m
//  mobilecloudlib
//
//  Created by openmobster on 8/26/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "SyncUtility.h"
#import "SyncService.h"
#import "AutoSync.h"

@implementation SyncUtility

+(id)withInit
{
	SyncUtility *syncUtility = (SyncUtility *)[[[SyncUtility alloc] init] autorelease];
	return syncUtility;
}

-(void)resetChannel:(NSString *)channel
{
    @try 
    {
       SyncService *sync = [SyncService getInstance];
       [sync performBootSync:channel :NO];
    }
    @catch (NSException *exception) 
    {
        @throw exception;
    }
}

-(void)syncChannel:(NSString *)channel
{
    @try 
    {
        SyncService *sync = [SyncService getInstance];
        [sync performTwoWaySync:channel :NO];
    }
    @catch (NSException *exception) 
    {
        @throw exception;
    }
}

-(void)syncAll
{
    AutoSync *autoSync = [AutoSync withInit];
    [autoSync sync];
}
@end
