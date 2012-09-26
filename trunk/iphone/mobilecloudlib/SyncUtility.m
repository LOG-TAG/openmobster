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
#import "ProxyLoader.h"

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
        
        //load proxies in the background
        ProxyLoader *proxyLoader = [ProxyLoader getInstance];
        [proxyLoader startProxySync];
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
    @try 
    {
        AutoSync *autoSync = [AutoSync withInit];
        [autoSync syncWithoutProxy];
        
        //load proxies in the background
        ProxyLoader *proxyLoader = [ProxyLoader getInstance];
        [proxyLoader startProxySync];
    }
    @catch (NSException *exception) 
    {
        @throw exception;
    }
}
@end
