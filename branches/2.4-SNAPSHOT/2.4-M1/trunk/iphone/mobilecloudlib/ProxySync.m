/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "ProxySync.h"
#import "AppService.h"

/**
 * 
 * @author openmobster@gmail.com
 */
@implementation ProxySync

+(id)withInit
{
	ProxySync *instance = [[[ProxySync alloc] init] autorelease];
	return instance;
}

-(void)sync
{
    @try 
    {
        AppService *appService = [AppService getInstance];
        
        NSArray *allChannels = [appService myChannels];
        if(allChannels != nil && [allChannels count]>0)
        {
            for(Channel *local in allChannels)
            {
                NSString *name = local.name;
                [self proxySync:name];
            }
        }
    }
    @catch (NSException *exception) 
    {
        //this is happening in the background....do nothing
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
