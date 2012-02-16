/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "AbstractSyncTest.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation AbstractSyncTest

-(void) setUp
{
	TestSuite *suite = (TestSuite *)self.suite;
	TestContext *context = suite.context;
	channel = [context getAttribute:@"channel"];
}

-(void) tearDown
{
	SyncDataSource *ds = [SyncDataSource getInstance];
	[ds clearAll];
	
	NSString *payload = [NSString 
						 stringWithFormat:@"setUp=%@/CleanUp\n",[[self class] description]];
	[self resetServerAdapter:payload];
}

-(void) resetServerAdapter:(NSString *)payload
{
	NetSession *netSession = NULL;
	@try
	{
		netSession = [[NetworkConnector getInstance] openSession:NO];
	
		NSString *request =@"<request><header><name>processor</name><value>testsuite</value></header></request>";
	
		NSString *status = [netSession performHandshake:request];
	
		if([StringUtil indexOf:status :@"status=200"] != -1)
		{
			[netSession sendPayload:payload];
		}
	}
	@catch (NSException *exception) 
	{
		NSLog(@"Exception: %@",[exception description]);
	}
	@finally 
	{
		if(netSession != NULL)
		{
			[netSession close];
		}
	}
}

-(void) setUp:(NSString *)operation
{
	@try
	{
		NSString *service = @"testServerBean"; //TODO: grab this from test suite context
		MobileObjectDatabase *mdb = [MobileObjectDatabase getInstance];
		SyncDataSource *syncds = [SyncDataSource getInstance];
		SyncService *sync = [SyncService getInstance];
	
		[mdb deleteAll:service];
		[syncds clearAll];
		
		if([operation caseInsensitiveCompare:@"add"] == NSOrderedSame)
		{
			//Adding records 'unique-3' and 'unique-4' to the device in addition
			//to the existing 'unique-1' and 'unique-2'	
			for(int i=3; i<5; i++)
			{
				NSString *uniqueId = [NSString stringWithFormat:@"unique-%d",i];
				MobileObject *local = [MobileObject withInit];
				local.recordId = uniqueId;
				local.service = service;
				[local setValue:@"from" value:[NSString stringWithFormat:@"%@@from.com",uniqueId]];
				[local setValue:@"to" value:[NSString stringWithFormat:@"%@@to.com",uniqueId]];
				[local setValue:@"subject" value:[NSString stringWithFormat:@"%@/Subject",uniqueId]];
				[local setValue:@"message" value:[NSString stringWithFormat:@"<tag apos='apos' quote=\"quote\" ampersand='&'>%@/Message</tag>",uniqueId]];
				
				[mdb create:local];
				
				[sync updateChangeLog:service :_Add :uniqueId];
			}
		}
		else if([operation caseInsensitiveCompare:@"replace"] == NSOrderedSame ||
				[operation caseInsensitiveCompare:@"multirecord"] == NSOrderedSame
		)
		{
			//Only modifying record 'unique-2' on the device	
			for(int i=1; i<3; i++)
			{
				NSString *uniqueId = [NSString stringWithFormat:@"unique-%d",i];
				MobileObject *local = [MobileObject withInit];
				local.recordId = uniqueId;
				local.service = service;
				[local setValue:@"from" value:[NSString stringWithFormat:@"%@@from.com",uniqueId]];
				[local setValue:@"to" value:[NSString stringWithFormat:@"%@@to.com",uniqueId]];
				[local setValue:@"subject" value:[NSString stringWithFormat:@"%@/Subject",uniqueId]];
				
				if(i == 2)
				{
					[local setValue:@"message" value:[NSString stringWithFormat:@"<tag apos='apos' quote=\"quote\" ampersand='&'>%@/Updated/Client</tag>",uniqueId]];
					[sync updateChangeLog:service :_Replace :uniqueId];
				}
				else 
				{
					[local setValue:@"message" value:[NSString stringWithFormat:@"<tag apos='apos' quote=\"quote\" ampersand='&'>%@/Message</tag>",uniqueId]];
				}

				
				[mdb create:local];
			}
		}
		else if([operation caseInsensitiveCompare:@"conflict"] == NSOrderedSame)
		{
			//Only modifying record 'unique-1' on the device	
			for(int i=1; i<3; i++)
			{
				NSString *uniqueId = [NSString stringWithFormat:@"unique-%d",i];
				MobileObject *local = [MobileObject withInit];
				local.recordId = uniqueId;
				local.service = service;
				[local setValue:@"from" value:[NSString stringWithFormat:@"%@@from.com",uniqueId]];
				[local setValue:@"to" value:[NSString stringWithFormat:@"%@@to.com",uniqueId]];
				[local setValue:@"subject" value:[NSString stringWithFormat:@"%@/Subject",uniqueId]];
				[local setValue:@"message" value:[NSString stringWithFormat:@"<tag apos='apos' quote=\"quote\" ampersand='&'>%@/Message</tag>",uniqueId]];
				
				if(i == 1)
				{
					[local setValue:@"message" value:[NSString stringWithFormat:@"<tag apos='apos' quote=\"quote\" ampersand='&'>%@/Updated/Client</tag>",uniqueId]];
					[sync updateChangeLog:service :_Replace :uniqueId];
				}
				
				[mdb create:local];
			}
		}
		else if([operation caseInsensitiveCompare:@"delete"] == NSOrderedSame)
		{
			//Delete unique-2 from the device
			NSString *uniqueId = @"unique-1";
			MobileObject *local = [MobileObject withInit];
			local.recordId = uniqueId;
			local.service = service;
			[local setValue:@"from" value:[NSString stringWithFormat:@"%@@from.com",uniqueId]];
			[local setValue:@"to" value:[NSString stringWithFormat:@"%@@to.com",uniqueId]];
			[local setValue:@"subject" value:[NSString stringWithFormat:@"%@/Subject",uniqueId]];
			[local setValue:@"message" value:[NSString stringWithFormat:@"<tag apos='apos' quote=\"quote\" ampersand='&'>%@/Message</tag>",uniqueId]];
			[mdb create:local];
			
			[sync updateChangeLog:service :_Delete :@"unique-2"];
		}
	}
	@finally 
	{
        NSString *appPayload = [NSString 
							 stringWithFormat:@"setUp=%@/App/com.yourcompany.syncTests\n",[[self class] description]];
        [self resetServerAdapter:appPayload];
        
		NSString *payload = [NSString 
							 stringWithFormat:@"setUp=%@/%@\n",[[self class] description],operation];
		[self resetServerAdapter:payload];
	}
}

-(MobileObject *)getRecord:(NSString *)recordId
{
	NSString *service = @"testServerBean"; //TODO: grab this from test suite context
	MobileObjectDatabase *mdb = [MobileObjectDatabase getInstance];	
	MobileObject *record = [mdb read:service :recordId];
	
	return record;
}

-(void)assertRecordPresence:(NSString *)recordId :(NSString *)context
{
	NSString *service = @"testServerBean"; //TODO: grab this from test suite context
	MobileObjectDatabase *mdb = [MobileObjectDatabase getInstance];
	
	MobileObject *record = [mdb read:service :recordId];
	NSString *message = [NSString stringWithFormat:@"%@/%@",context,recordId];
	[self assertTrue:(record != nil) :message];
}

-(void)assertRecordAbsence:(NSString *)recordId :(NSString *)context
{
	NSString *service = @"testServerBean"; //TODO: grab this from test suite context
	MobileObjectDatabase *mdb = [MobileObjectDatabase getInstance];
	
	MobileObject *record = [mdb read:service :recordId];
	NSString *message = [NSString stringWithFormat:@"%@/%@",context,recordId];
	[self assertTrue:(record == nil) :message];
}
@end
