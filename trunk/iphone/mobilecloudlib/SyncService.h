/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import <Foundation/Foundation.h>
#import "Service.h"
#import "Registry.h"
#import "NetworkConnector.h"
#import "NetSession.h"
#import "SyncConstants.h"
#import "CloudPayload.h"
#import "SyncAdapter.h"
#import "SyncAdapterRequest.h"
#import "SyncAdapterResponse.h"
#import "Configuration.h"
#import "SyncEngine.h"
#import "GenericAttributeManager.h"
#import "SyncScheduler.h"
#import "ProxyLoader.h"

/**
 * 
 * @author openmobster@gmail.com
 */
@interface SyncService : Service 
{
	@private 
	SyncEngine *engine;
}

+(SyncService *) getInstance; 

-(void)startDaemons;
-(void)startDeltaSync:(NSString *)channel;

-(void)performBootSync:(NSString *)channel :(BOOL)isBackground;
-(void)performTwoWaySync:(NSString *)channel :(BOOL)isBackground;
-(void)performOneWayServerSync:(NSString *)channel :(BOOL)isBackground;
-(void)performOneWayClientSync:(NSString *)channel :(BOOL)isBackground;
-(void)performSlowSync:(NSString *)channel :(BOOL)isBackground;
-(void)performStreamSync:(NSString *)channel :(BOOL)isBackground :(NSString *)oid;
-(void)updateChangeLog:(NSString *)channel :(NSString *) operation :(NSString *) objectId;


//Used internally
-(void)startSync:(NSString *)syncType :(NSString *)channel :(BOOL)isBackground :(NSString *)oid;
-(void)performSync:(NetSession *)session :(NSString *)syncType :(NSString *)channel :(BOOL)isBackground :(NSString *)oid;

@end
