/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "SyncService.h"


/**
 * FIXME: Implement proper synchronization strategy when Channel Locking is figured
 * out
 * 
 * @author openmobster@gmail.com
 */
@implementation SyncService

-init
{
	if(self == [super init])
	{
		engine = [[SyncEngine alloc] init];
	}
	
	return self;
}

-(void)dealloc
{
	[engine release];
	[super dealloc];
}

+(SyncService *) getInstance
{
	Registry *registry = [Registry getInstance];
	return (SyncService *)[registry lookup:[SyncService class]];
}

-(void)startDaemons
{
     /*NSArray *changelog = [engine getChangeLog:@"phonegap_channel" operation:@"Replace"];
     NSLog(@"-----ChangeLog Dump On the Main Thread-----------------------");
     if(changelog != nil && [changelog count]>0)
     {
     for(ChangeLogEntry *local in changelog)
     {
     NSString * recordId = local.recordId;
     NSLog(@"RecordId(Main): %@",local.recordId);
     NSLog(@"--------------------------------------");
     }
     }
     else 
     {
     NSLog(@"ChangeLog is Empty!!!");
     }*/
    
    //persist the managedcontext in the database
    /*NSManagedObjectContext *managedContext = [[CloudDBManager getInstance] storageContext];
	
	NSError* error;
	BOOL success = [managedContext save:&error];
    if(!success)
    {
        NSLog(@"Error during save!!!");
        NSLog(@"SaveError: %@, %@", error, [error userInfo]);
    }
    
    NSLog(@"Save Success:%d",success);*/
    
	//Schedule a background sync to sync up the channes at App startup
	SyncScheduler *backgroudSync = [SyncScheduler getInstance];
	[backgroudSync startBackgroundSync];
}

-(void)startDeltaSync:(NSString *)channel
{
    SyncScheduler *backgroudSync = [SyncScheduler getInstance];
	[backgroudSync startDeltaSync:channel];
}

-(void)performBootSync:(NSString *)channel :(BOOL)isBackground
{
	@synchronized(self)
	{
		[self startSync:_BOOT_SYNC :channel :isBackground :nil];
	}
}

-(void)performTwoWaySync:(NSString *)channel :(BOOL)isBackground
{
	@synchronized(self)
	{
		[self startSync:_TWO_WAY :channel :isBackground :nil];
	}
}

-(void)performOneWayServerSync:(NSString *)channel :(BOOL)isBackground
{
	@synchronized(self)
	{
		[self startSync:_ONE_WAY_SERVER :channel :isBackground :nil];
	}
}

-(void)performOneWayClientSync:(NSString *)channel :(BOOL)isBackground
{
	@synchronized(self)
	{
		[self startSync:_ONE_WAY_CLIENT :channel :isBackground :nil];
	}
}

-(void)performSlowSync:(NSString *)channel :(BOOL)isBackground
{
	@synchronized(self)
	{
		[self startSync:_SLOW_SYNC :channel :isBackground :nil];
	}
}

-(void)performStreamSync:(NSString *)channel :(BOOL)isBackground :(NSString *)oid
{
	@synchronized(self)
	{
		[self startSync:_STREAM :channel :isBackground :oid];
	}
}

-(void)updateChangeLog:(NSString *)channel :(NSString *) operation :(NSString *) objectId
{
	@synchronized(self)
	{
		NSMutableArray *entries = [NSMutableArray array];
	
		GenericAttributeManager *attr = [GenericAttributeManager withInit];
		[attr setAttribute:@"nodeId" :channel];
		[attr setAttribute:@"operation" :operation];
		[attr setAttribute:@"recordId" :objectId];
	
		[entries addObject:attr];
	
		[engine addChangeLogEntries:entries];
        
        
		//sync in the background
		[self startDeltaSync:channel];
	}
}


//Used internally
-(void)performSync:(NetSession *)session :(NSString *)syncType :(NSString *)channel :(BOOL)isBackground :(NSString *)oid
{
	Configuration *conf = [Configuration getInstance];
	SyncAdapter *syncAdapter = [SyncAdapter withInit];
	
	//Setup the Sync Initialization payload
	SyncAdapterRequest *request = [SyncAdapterRequest withInit];
	[request setAttribute:_SOURCE :conf.deviceId];
	[request setAttribute:_TARGET :conf.serverId];
	[request setAttribute:_MAX_CLIENT_SIZE :conf.maxPacketSize];
	[request setAttribute:_CLIENT_INITIATED :[NSNumber numberWithBool:YES]];
	[request setAttribute:_DATA_SOURCE :channel];
	[request setAttribute:_DATA_TARGET :channel];
	[request setAttribute:_SYNC_TYPE :syncType];
	[request setAttribute:@"isBackgroundSync" :[NSNumber numberWithBool:isBackground]];
	if(![StringUtil isEmpty:oid])
	{
		[request setAttribute:_STREAM_RECORD_ID :oid];
	}
    
    NSBundle *bundle = [NSBundle bundleForClass:[self class]];
    NSString *appId = [bundle bundleIdentifier];
    [request setAttribute:_App :appId];
	
	//Start the Sync Session
	SyncAdapterResponse *response = [syncAdapter service:request];
	
	//Payload to start the sync session
	NSString *devicePayload = (NSString *)[response getAttribute:_PAYLOAD];
	
	//Sending the start request
	NSString *cloudPayload = [session sendPayload:devicePayload];
	
	//SyncSession orchestration
	while(YES)
	{
		request = [SyncAdapterRequest withInit];
		[request setAttribute:_PAYLOAD :cloudPayload];
		
		response = [syncAdapter service:request];
		devicePayload = (NSString *)[response getAttribute:_PAYLOAD];
		
		//Check for end of sync session
		if(response.status == _RESPONSE_CLOSE)
		{
			//Sync Session successfully ended
			break;
		}
		
		cloudPayload = [session sendPayload:devicePayload];
		if([cloudPayload isEqualToString:@"status=500"])
		{
			//Unknown CloudSide error occurred
			break;
		}
	}
}

-(void)startSync:(NSString *)syncType :(NSString *)channel :(BOOL)isBackground :(NSString *)oid
{
	NetSession *session = NULL;
	@try
	{
		Configuration *conf = [Configuration getInstance];
		BOOL secure = [conf.sslActive boolValue];
		NetworkConnector *connector = [NetworkConnector getInstance];
		session = [connector openSession:secure];
		
		NSString *handshake = NULL;
		NSString *deviceId = conf.deviceId;
		NSString *authHash = conf.authenticationHash;
		handshake = [NSString stringWithFormat:_SYNC_PAYLOAD,deviceId,authHash];
		
		
		NSString *response = [session performHandshake:handshake];
		if([StringUtil indexOf:response :@"status=200"] != -1)
		{
			[self performSync:session :syncType :channel :isBackground :oid];
		}
        else
        {
            SystemException *syse = [SystemException withContext:@"Start Sync" method:@"startSync" parameters:[NSMutableArray arrayWithObjects:response,nil]];
            @throw syse;
        }
	}
	@finally 
	{
		if(session != NULL)
		{
			[session close];
		}
	}
}
@end
