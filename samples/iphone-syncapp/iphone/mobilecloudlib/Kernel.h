/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import <Foundation/Foundation.h>
#import "Registry.h"
#import "Service.h"
#import "NetworkConnector.h"
#import "SyncDataSource.h"
#import "SyncObjectGenerator.h"
#import "SyncService.h"
#import "MobileObjectDatabase.h"
#import "DeviceSerializer.h"
#import "CloudDBManager.h"
#import "SyncScheduler.h"
#import "ProxyLoader.h"
#import "Bus.h"
#import "Configuration.h"
#import "AppConfig.h"
#import "AppService.h"
#import "DeviceManager.h"

/**
 * 
 * @author openmobster@gmail.com
 */
@interface Kernel : NSObject 
{

}

+(Kernel *)getInstance;

-(void)startup;
-(void)shutdown;
-(BOOL)isRunning;

//-(void)headlessStartup;
//-(void)headlessShutdown;

@end
