/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "AutoSync.h"
#import "AppService.h"
#import "MobileBean.h"
#import "SyncService.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation AutoSync

+(id)withInit
{
	AutoSync *autosync = (AutoSync *)[[[AutoSync alloc] init] autorelease];
	return autosync;
}

-(void)sync
{
	@try 
	{
	AppService *appService = [AppService getInstance];
	SyncService *sync = [SyncService getInstance];
	NSMutableDictionary *booted = [NSMutableDictionary dictionary];
	
	//Get all the channels and perfom boot-sync on the unbooted ones
	//NSLog(@"Starting Boot Sync");
	NSArray *allChannels = [appService myChannels];
	if(allChannels != nil && [allChannels count]>0)
	{
		//NSLog(@"Boot Sync candidat found!!!");
		for(Channel *local in allChannels)
		{
			NSString *name = local.name;
			if(![MobileBean isBooted:name])
			{
				//NSLog(@"Performing Boot Sync on: %@",name);
				[sync performBootSync:name :NO];
				[booted setObject:name forKey:name];
			}
		}
	}
	
	//Get writable channels, and perform a two-way sync on these
	NSArray *writableChannels = [appService writableChannels];
	if(writableChannels != nil && [writableChannels count]>0)
	{
		for(Channel *local in writableChannels)
		{
			NSString *name = local.name;
			
			//NSLog(@"Performing Two Way Sync on: %@",name);
			[sync performTwoWaySync:name :NO];
		}
	}
	
	//Do a load proxies sync on the channels
    if(allChannels != nil && [allChannels count]>0)
	{
		//NSLog(@"Boot Sync candidat found!!!");
		for(Channel *local in allChannels)
		{
			NSString *name = local.name;
			[self proxySync:name];
		}
	}
	}
	@catch(NSException *e)
	{
		//saves from a crash...tried to sync
	}
}

-(void)syncWithoutProxy
{
    @try 
	{
        AppService *appService = [AppService getInstance];
        SyncService *sync = [SyncService getInstance];
        NSMutableDictionary *booted = [NSMutableDictionary dictionary];
        
        //Get all the channels and perfom boot-sync on the unbooted ones
        //NSLog(@"Starting Boot Sync");
        NSArray *allChannels = [appService myChannels];
        if(allChannels != nil && [allChannels count]>0)
        {
            //NSLog(@"Boot Sync candidat found!!!");
            for(Channel *local in allChannels)
            {
                NSString *name = local.name;
                if(![MobileBean isBooted:name])
                {
                    //NSLog(@"Performing Boot Sync on: %@",name);
                    [sync performBootSync:name :NO];
                    [booted setObject:name forKey:name];
                }
            }
        }
        
        //Get writable channels, and perform a two-way sync on these
        NSArray *writableChannels = [appService writableChannels];
        if(writableChannels != nil && [writableChannels count]>0)
        {
            for(Channel *local in writableChannels)
            {
                NSString *name = local.name;
                
                //NSLog(@"Performing Two Way Sync on: %@",name);
                [sync performTwoWaySync:name :NO];
            }
        }
	}
	@catch(NSException *e)
	{
		//saves from a crash...tried to sync
	}  
}

-(void)proxySync:(NSString *)channel
{
	SyncService *sync = [SyncService getInstance];
	MobileObjectDatabase *mdb = [MobileObjectDatabase getInstance];
	NSArray *allObjects = [mdb readAll:channel];
	if(allObjects != nil)
	{
		for(MobileObject *local in allObjects)
		{
			if(local.proxy)
			{
				NSString *oid = local.recordId;
				//NSLog(@"Performing Load Proxy sync....");
				[sync performStreamSync:channel :NO :oid];
			}
		}
	}
}
@end
