#import "SyncAdapter.h"


@implementation SyncAdapter

-(id) init
{
	if(self = [super init])
	{
		manager = [WorkflowManager withInit];
		Context *context = [manager activeContext];
		[context setAttribute:@"manager" :manager];
	}
	return self;
}

+(id) withInit
{
	SyncAdapter *adapter = [[[SyncAdapter alloc] init] autorelease];
	
	return adapter;
}

-(SyncAdapterResponse *) service:(SyncAdapterRequest *) request
{
	SyncAdapterResponse *response = [SyncAdapterResponse withInit];
	Context *context = [manager activeContext];
	
	//Pre-Processing
	NSString *payload = (NSString *)[request getAttribute:_PAYLOAD];
	SyncObjectGenerator *gen = [SyncObjectGenerator withInit];
	Session *parsedSession = [gen parse:payload];
	Session *activeSession = [manager activeSession];
	activeSession.currentMessage = parsedSession.currentMessage;
	[context setAttribute:@"request" :request];
	[self storeIncomingPayload:activeSession :activeSession.currentMessage];
	
	//Processing
	SyncMessage *responsePayload = nil;
	BOOL endOfWorkflow = NO;
	while(responsePayload == nil)
	{
		endOfWorkflow = [manager sendSyncNotification];
		
		if(endOfWorkflow)
		{
			response.status = _RESPONSE_CLOSE;
			return response;
		}
		
		responsePayload = (SyncMessage *)[context getAttribute:_PAYLOAD];
	}
	
	//Post-Processing
	if(responsePayload != nil)
	{
		[context removeAttribute:_PAYLOAD];
		NSString *outgoing = [self storeOutgoingPayload:activeSession :responsePayload];
		if(outgoing != nil)
		{
			[response setAttribute:_PAYLOAD :outgoing];
		}
	}
	
	return response;
}

-(void)storeIncomingPayload:(Session *)session :(SyncMessage *)message
{
	NSString *phase = [manager activeNode];
	
	if([phase isEqualToString:@"handshake"] || 
	   [phase isEqualToString:@"decide_end_init"]
	)
	{
		[session.serverInitPackage addMessage:message];
	}
	else if([phase isEqualToString:@"start_sync"] || 
			[phase isEqualToString:@"decide_end_sync"]
	)
	{
		[session.serverSyncPackage addMessage:message];
	}
	else if([phase isEqualToString:@"start_close"] ||
			[phase isEqualToString:@"decide_end_close"] ||
			[phase isEqualToString:@"close_session"] ||
			[phase isEqualToString:@"end"]
	)
	{
		[session.serverClosePackage addMessage:message];
	}
}

-(NSString *)storeOutgoingPayload:(Session *)session :(SyncMessage *)message
{
	NSString *phase = [manager activeNode];
	NSString *xml = nil;
	
	if([phase isEqualToString:@"handshake"] || 
	   [phase isEqualToString:@"decide_end_init"]
	)
	{
		[session.clientInitPackage addMessage:message];
		
		xml = [SyncXMLGenerator generateInitMessage:session :message];
	}
	else if([phase isEqualToString:@"start_sync"] || 
			[phase isEqualToString:@"decide_end_sync"]
	)
	{
		[session.clientSyncPackage addMessage:message];
		
		xml = [SyncXMLGenerator generateSyncMessage:session :message];
        
        session.hasSyncExecutedOnce = YES;
	}
	else if([phase isEqualToString:@"start_close"] || 
			[phase isEqualToString:@"decide_end_close"] ||
			[phase isEqualToString:@"close_session"] ||
			[phase isEqualToString:@"end"]
	)
	{
		[session.clientClosePackage addMessage:message];
		
		xml = [SyncXMLGenerator generateSyncMessage:session :message];
	}
	
	return xml;
}
@end
