/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import <Foundation/Foundation.h>
#import "SyncDataSource.h"
#import "NetworkConnector.h"
#import "NetSession.h"
#import "StringUtil.h"
#import "MobileObjectDatabase.h"
#import "MobileObject.h"
#import "ChangeLogEntry.h"
#import "Test.h"
#import "TestContext.h"
#import "TestSuite.h"
#import "SyncConstants.h"
#import "SyncService.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@interface AbstractSyncTest : Test 
{
	@protected
	NSString *channel;
}

//Test specific
-(void) setUp:(NSString *)operation;
-(void) resetServerAdapter:(NSString *)payload;

-(MobileObject *)getRecord:(NSString *)recordId;
-(void)assertRecordPresence:(NSString *)recordId :(NSString *)context;
-(void)assertRecordAbsence:(NSString *)recordId :(NSString *)context;

@end
