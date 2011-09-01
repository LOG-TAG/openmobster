/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "StartHandshake.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation StartHandshake

+(id) withInit
{
	return [[[StartHandshake alloc] init] autorelease];
}

-(NSString *) execute:(Context *)context
{
	Configuration *conf = [Configuration getInstance];
	SyncAdapterRequest *request = (SyncAdapterRequest *)[context getAttribute:@"request"];
	WorkflowManager *manager = (WorkflowManager *)[context getAttribute:@"manager"];
	
	//Process the incoming data
	NSString *source = (NSString *)[request getAttribute:_SOURCE];
	NSString *target = (NSString *)[request getAttribute:_TARGET];
	NSNumber *maxClientSize = (NSNumber *)[request getAttribute:_MAX_CLIENT_SIZE];
	NSNumber *clientInitiated = (NSNumber *)[request getAttribute:_CLIENT_INITIATED];
	NSString *dataSource = (NSString *)[request getAttribute:_DATA_SOURCE];
	NSString *dataTarget = (NSString *)[request getAttribute:_DATA_TARGET];
	NSString *syncType = (NSString *)[request getAttribute:_SYNC_TYPE];
	NSNumber *isBackgroundSync = (NSNumber *)[request getAttribute:@"isBackgroundSync"];
	NSString *streamRecordId = (NSString *)[request getAttribute:_STREAM_RECORD_ID];
    NSString *app = (NSString *)[request getAttribute:_App];
	
	//Setup the active session
	Session *activeSession = [manager activeSession];
	SyncEngine *engine = [manager engine];
	activeSession.sessionId = [engine generateSync];
	activeSession.source = source;
	activeSession.target = target;
	if(![StringUtil isEmpty:streamRecordId])
	{
		[activeSession setAttribute:_STREAM_RECORD_ID :streamRecordId];
	}
    activeSession.app = app;
	
	//Setup the SyncMessage
	SyncMessage *message = [SyncMessage withInit];
	message.messageId = @"1";
	if(maxClientSize != nil)
	{
		message.maxClientSize = [maxClientSize intValue];
	}
	if(clientInitiated != nil)
	{
		message.clientInitiated = [clientInitiated boolValue];
	}
	
	//Anchor support
	NSString *anchorSource = [NSString stringWithFormat:@"%@/%@",source,dataSource];
	Anchor *anchor = [engine createNewAnchor:anchorSource];
	NSString *anchorXml = [SyncXMLGenerator generateAnchor:anchor];
	NSString *meta = [NSString stringWithFormat:@"<![CDATA[\n%@]]>\n",anchorXml];
	activeSession.anchor = anchor;
	
	//Setup SyncAlert
	Item *item = [Item withInit];
	item.source = dataSource;
	item.target = dataTarget;
	item.meta = meta;
	Alert *alert = [Alert withInit];
	alert.cmdId = @"1";
	alert.data = syncType;
	[alert addItem:item];
	activeSession.syncType = syncType;
	[message addAlert:alert];
	
	//Security credentials
	Credential *credential = [Credential withInit];
	credential.type = _sycml_auth_sha;
	credential.data = conf.authenticationHash;
	message.credential = credential;
	
	//finalize the message
	message.final = YES;
	
	//isBackground sync
	if(isBackgroundSync != nil)
	{
		activeSession.backgroundSync = [isBackgroundSync boolValue];
	}

	//Update the Workflow State
	[context setAttribute:_PAYLOAD :message];
	
	
	//Transition to 'decide_end_init' node
	return @"decide_end_init";
}
@end
