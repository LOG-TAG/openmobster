/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "SyncXMLGenerator.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation SyncXMLGenerator

+(NSString *) generateInitMessage:(Session *)session :(SyncMessage *)message
{
	NSMutableString *xml = [NSMutableString stringWithString:@""];
	
	NSString *source = [XMLUtil cleanupXML:session.source];
	NSString *target = [XMLUtil cleanupXML:session.target];
	NSString *sessionId = [XMLUtil cleanupXML:session.sessionId];
    NSString *app = [XMLUtil cleanupXML:session.app];
	
	NSString *messageId = [XMLUtil cleanupXML:message.messageId];
	Credential *credential = message.credential;
	BOOL isFinal = message.final;
	NSArray *status = message.status;
	NSArray *alerts = message.alerts;
	
	[xml appendFormat:@"<%@>\n",_SyncML];
	
	//Sync Header
	[xml appendFormat:@"<%@>\n",_SyncHdr];
	
	[xml appendFormat:@"<%@>1.1</%@>\n",_VerDTD,_VerDTD];
	[xml appendFormat:@"<%@>SyncML/1.1</%@>\n",_VerProto,_VerProto];
	[xml appendFormat:@"<%@>%@</%@>\n",_SessionID,sessionId,_SessionID];
    [xml appendFormat:@"<%@>%@</%@>\n",_App,app,_App];
	
	[xml appendFormat:@"<%@>\n",_Source];
	[xml appendFormat:@"<%@>%@</%@>\n",_LocURI,source,_LocURI];
	[xml appendFormat:@"</%@>\n",_Source];
	
	[xml appendFormat:@"<%@>\n",_Target];
	[xml appendFormat:@"<%@>%@</%@>\n",_LocURI,target,_LocURI];
	[xml appendFormat:@"</%@>\n",_Target];
	
	[xml appendFormat:@"<%@>%@</%@>\n",_MsgID,messageId,_MsgID];
	
	if(credential != nil)
	{
		[xml appendFormat:@"<%@>\n",_Cred];
		
		[xml appendFormat:@"<%@>\n",_Meta];
		[xml appendFormat:@"<%@>%@</%@>\n",_Type,credential.type,_Type];
		[xml appendFormat:@"</%@>\n",_Meta];
		
		[xml appendFormat:@"<%@>%@</%@>\n",_Data,credential.data,_Data];
		
		[xml appendFormat:@"</%@>\n",_Cred];
	}
	
	[xml appendFormat:@"</%@>\n",_SyncHdr];
	
	//Sync Body
	[xml appendFormat:@"<%@>\n",_SyncBody];
	
	if(status != nil)
	{
		for(Status *local in status)
		{
			NSString *statusXml = [SyncXMLGenerator generateStatus:local];
			[xml appendString:statusXml];
		}
	}
	
	if(alerts != nil)
	{
		for(Alert *local in alerts)
		{
			NSString *alertXml = [SyncXMLGenerator generateAlert:local];
			[xml appendString:alertXml];
		}
	}
	
	if(isFinal)
	{
		[xml appendFormat:@"<%@/>\n",_Final];
	}
	
	[xml appendFormat:@"</%@>\n",_SyncBody];
	
	
	[xml appendFormat:@"</%@>\n",_SyncML];
	
	return xml;
}

+(NSString *) generateSyncMessage:(Session *)session :(SyncMessage *)message
{
	NSMutableString *xml = [NSMutableString stringWithString:@""];
	
	NSString *source = [XMLUtil cleanupXML:session.source];
	NSString *target = [XMLUtil cleanupXML:session.target];
	NSString *sessionId = [XMLUtil cleanupXML:session.sessionId];
    NSString *app = [XMLUtil cleanupXML:session.app];
	
	NSString *messageId = [XMLUtil cleanupXML:message.messageId];
	Credential *credential = message.credential;
	BOOL isFinal = message.final;
	NSArray *status = message.status;
	NSArray *alerts = message.alerts;
	NSArray *commands = message.syncCommands;
	
	[xml appendFormat:@"<%@>\n",_SyncML];
	
	//Sync Header
	[xml appendFormat:@"<%@>\n",_SyncHdr];
	
	[xml appendFormat:@"<%@>1.1</%@>\n",_VerDTD,_VerDTD];
	[xml appendFormat:@"<%@>SyncML/1.1</%@>\n",_VerProto,_VerProto];
	[xml appendFormat:@"<%@>%@</%@>\n",_SessionID,sessionId,_SessionID];
    [xml appendFormat:@"<%@>%@</%@>\n",_App,app,_App];
	
	[xml appendFormat:@"<%@>\n",_Source];
	[xml appendFormat:@"<%@>%@</%@>\n",_LocURI,source,_LocURI];
	[xml appendFormat:@"</%@>\n",_Source];
	
	[xml appendFormat:@"<%@>\n",_Target];
	[xml appendFormat:@"<%@>%@</%@>\n",_LocURI,target,_LocURI];
	[xml appendFormat:@"</%@>\n",_Target];
	
	[xml appendFormat:@"<%@>%@</%@>\n",_MsgID,messageId,_MsgID];
	
	if(credential != nil)
	{
		[xml appendFormat:@"<%@>\n",_Cred];
		
		[xml appendFormat:@"<%@>\n",_Meta];
		[xml appendFormat:@"<%@>%@</%@>\n",_Type,credential.type,_Type];
		[xml appendFormat:@"</%@>\n",_Meta];
		
		[xml appendFormat:@"<%@>%@</%@>\n",_Data,credential.data,_Data];
		
		[xml appendFormat:@"</%@>\n",_Cred];
	}
	
	[xml appendFormat:@"</%@>\n",_SyncHdr];
	
	//Sync Body
	[xml appendFormat:@"<%@>\n",_SyncBody];
	
	if(status != nil)
	{
		for(Status *local in status)
		{
			NSString *statusXml = [SyncXMLGenerator generateStatus:local];
			[xml appendString:statusXml];
		}
	}
	
	if(alerts != nil)
	{
		for(Alert *local in alerts)
		{
			NSString *alertXml = [SyncXMLGenerator generateAlert:local];
			[xml appendString:alertXml];
		}
	}
	
	if(commands != nil)
	{
		for(SyncCommand *local in commands)
		{
			NSString *commandXml = [SyncXMLGenerator generateCommand:local];
			[xml appendString:commandXml];
		}
	}
	
	if(isFinal)
	{
		[xml appendFormat:@"<%@/>\n",_Final];
	}
	
	[xml appendFormat:@"</%@>\n",_SyncBody];
	
	
	[xml appendFormat:@"</%@>\n",_SyncML];
	
	return xml;
}

+(NSString *) generateAnchor:(Anchor *) anchor
{
	NSMutableString *xml = [NSMutableString stringWithString:@""];
	
	NSString *lastSync = [XMLUtil cleanupXML:anchor.lastSync];
	NSString *nextSync = [XMLUtil cleanupXML:anchor.nextSync];
	
	[xml appendFormat:@"<%@ xmlns='%@'>\n",_Anchor,_sycml_metinf];
	[xml appendFormat:@"<%@>%@</%@>\n",_Last,lastSync,_Last];
	[xml appendFormat:@"<%@>%@</%@>\n",_Next,nextSync,_Next];
	[xml appendFormat:@"</%@>\n",_Anchor];
	
	return xml;
}
//------------------------------------------------------------------------------------------------
+(NSString *) generateItem:(Item *)item
{
	NSMutableString *xml = [NSMutableString stringWithString:@""];
	
	NSString *target = item.target;
	NSString *source = item.source;
	NSString *data = item.data;
	NSString *meta = item.meta;
	BOOL moreData = item.moreData;
	
	[xml appendFormat:@"<%@>\n",_Item];
	
	if(source != nil)
	{
		source = [XMLUtil cleanupXML:source];
		[xml appendFormat:@"<%@>\n",_Source];
		[xml appendFormat:@"<%@>%@</%@>\n",_LocURI,source,_LocURI];
		[xml appendFormat:@"</%@>\n",_Source];
	}
	
	if(target != nil)
	{
		target = [XMLUtil cleanupXML:target];
		[xml appendFormat:@"<%@>\n",_Target];
		[xml appendFormat:@"<%@>%@</%@>\n",_LocURI,target,_LocURI];
		[xml appendFormat:@"</%@>\n",_Target];
	}
	
	if(data != nil)
	{
		data = [XMLUtil addCData:data];
		[xml appendFormat:@"<%@>%@</%@>\n",_Data,data,_Data];
	}
	
	if(meta != nil)
	{
		meta = [XMLUtil cleanupXML:meta];
		[xml appendFormat:@"<%@>%@</%@>\n",_Meta,meta,_Meta];
	}
	
	if(moreData)
	{
		[xml appendFormat:@"<%@/>\n",_MoreData];
	}
	
	[xml appendFormat:@"</%@>\n",_Item];
	
	return xml;
}

+(NSString *) generateStatus:(Status *)status
{
	NSMutableString *xml = [NSMutableString stringWithString:@""];
	
	NSString *cmdId = [XMLUtil cleanupXML:status.cmdId];
	NSString *data = status.data;
	NSString *msgRef = status.msgRef;
	NSString *cmdRef = status.cmdRef;
	NSString *cmd = status.cmd;
	NSArray *targetRefs = status.targetRefs;
	NSArray *sourceRefs = status.sourceRefs;
	NSArray *items = status.items;
	
	[xml appendFormat:@"<%@>\n",_Status];
	
	[xml appendFormat:@"<%@>%@</%@>\n",_CmdID,cmdId,_CmdID];
	[xml appendFormat:@"<%@>%@</%@>\n",_CmdRef,cmdRef,_CmdRef];
	[xml appendFormat:@"<%@>%@</%@>\n",_MsgRef,msgRef,_MsgRef];
	[xml appendFormat:@"<%@>%@</%@>\n",_Cmd,cmd,_Cmd];
	[xml appendFormat:@"<%@>%@</%@>\n",_Data,data,_Data];
	
	if(targetRefs != nil)
	{
		for(NSString *ref in targetRefs)
		{
			[xml appendFormat:@"<%@>%@</%@>\n",_TargetRef,ref,_TargetRef];
		}
	}
	
	if(sourceRefs != nil)
	{
		for(NSString *ref in sourceRefs)
		{
			[xml appendFormat:@"<%@>%@</%@>\n",_SourceRef,ref,_SourceRef];
		}
	}
	
	if(items != nil)
	{
		for(Item *item in items)
		{
			NSString *itemXml = [SyncXMLGenerator generateItem:item];
			[xml appendString:itemXml];
		}
	}
	
	[xml appendFormat:@"</%@>\n",_Status];
	
	return xml;
}

+(NSString *) generateAlert:(Alert *)alert
{
	NSMutableString *xml = [NSMutableString stringWithString:@""];
	
	NSString *cmdId = [XMLUtil cleanupXML:alert.cmdId];
	NSString *data = alert.data;
	NSArray *items = alert.items;
	
	[xml appendFormat:@"<%@>\n",_Alert];
	
	[xml appendFormat:@"<%@>%@</%@>\n",_CmdID,cmdId,_CmdID];
	[xml appendFormat:@"<%@>%@</%@>\n",_Data,data,_Data];
	
	if(items != nil)
	{
		for(Item *item in items)
		{
			NSString *itemXml = [SyncXMLGenerator generateItem:item];
			[xml appendString:itemXml];
		}
	}
	
	[xml appendFormat:@"</%@>\n",_Alert];
	
	return xml;
}

+(NSString *) generateCommand:(SyncCommand *)command
{
	NSMutableString *xml = [NSMutableString stringWithString:@""];
	
	NSString *cmdId = [XMLUtil cleanupXML:command.cmdId];
	NSString *target = command.target;
	NSString *source = command.source;
	NSString *meta = command.meta;
	NSString *numberOfChanges = command.numberOfChanges;
	NSArray *add = command.addCommands;
	NSArray *replace = command.replaceCommands;
	NSArray *delete = command.deleteCommands;
	
	[xml appendFormat:@"<%@>\n",_Sync];
	
	[xml appendFormat:@"<%@>%@</%@>\n",_CmdID,cmdId,_CmdID];
	
	if(source != nil)
	{
		source = [XMLUtil cleanupXML:source];
		[xml appendFormat:@"<%@>\n",_Source];
		[xml appendFormat:@"<%@>%@</%@>\n",_LocURI,source,_LocURI];
		[xml appendFormat:@"</%@>\n",_Source];
	}
	
	if(target != nil)
	{
		target = [XMLUtil cleanupXML:target];
		[xml appendFormat:@"<%@>\n",_Target];
		[xml appendFormat:@"<%@>%@</%@>\n",_LocURI,target,_LocURI];
		[xml appendFormat:@"</%@>\n",_Target];
	}
	
	if(meta != nil)
	{
		meta = [XMLUtil cleanupXML:meta];
		[xml appendFormat:@"<%@>%@</%@>\n",_Meta,meta,_Meta];
	}
	
	if(numberOfChanges != nil)
	{
		numberOfChanges = [XMLUtil cleanupXML:numberOfChanges];
		[xml appendFormat:@"<%@>%@</%@>\n",_NumberOfChanges,numberOfChanges,_NumberOfChanges];
	}
	
	if(add != nil)
	{
		for(Add *local in add)
		{
			NSString *addXml = [SyncXMLGenerator generateAdd:local];
			[xml appendString:addXml];
		}
	}
	
	if(replace != nil)
	{
		for(Replace *local in replace)
		{
			NSString *replaceXml = [SyncXMLGenerator generateReplace:local];
			[xml appendString:replaceXml];
		}
	}
	
	if(delete != nil)
	{
		for(Delete *local in delete)
		{
			NSString *deleteXml = [SyncXMLGenerator generateDelete:local];
			[xml appendString:deleteXml];
		}
	}
	
	[xml appendFormat:@"</%@>\n",_Sync];
	
	return xml;
}

+(NSString *) generateAdd:(Add *)add
{
	NSMutableString *xml = [NSMutableString stringWithString:@""];
	
	NSString *cmdId = [XMLUtil cleanupXML:add.cmdId];
	NSString *meta = add.meta;
	NSArray *items = add.items;
	
	
	[xml appendFormat:@"<%@>\n",_Add];
	
	[xml appendFormat:@"<%@>%@</%@>\n",_CmdID,cmdId,_CmdID];
	
	if(![StringUtil isEmpty:meta])
	{
		meta = [XMLUtil cleanupXML:meta];
		[xml appendFormat:@"<%@>%@</%@>\n",_Meta,meta,_Meta];
	}
	
	if(items != nil)
	{
		for(Item *item in items)
		{
			NSString *itemXml = [SyncXMLGenerator generateItem:item];
			[xml appendString:itemXml];
		}
	}
	
	[xml appendFormat:@"</%@>\n",_Add];
	
	return xml;
}

+(NSString *) generateReplace:(Replace *)replace
{
	NSMutableString *xml = [NSMutableString stringWithString:@""];
	
	NSString *cmdId = [XMLUtil cleanupXML:replace.cmdId];
	NSString *meta = replace.meta;
	NSArray *items = replace.items;
	
	[xml appendFormat:@"<%@>\n",_Replace];
	
	[xml appendFormat:@"<%@>%@</%@>\n",_CmdID,cmdId,_CmdID];
	
	if(![StringUtil isEmpty:meta])
	{
		meta = [XMLUtil cleanupXML:meta];
		[xml appendFormat:@"<%@>%@</%@>\n",_Meta,meta,_Meta];
	}
	
	if(items != nil)
	{
		for(Item *item in items)
		{
			NSString *itemXml = [SyncXMLGenerator generateItem:item];
			[xml appendString:itemXml];
		}
	}
	
	[xml appendFormat:@"</%@>\n",_Replace];
	
	return xml;
}

+(NSString *) generateDelete:(Delete *)delete
{
	NSMutableString *xml = [NSMutableString stringWithString:@""];
	
	NSString *cmdId = [XMLUtil cleanupXML:delete.cmdId];
	NSString *meta = delete.meta;
	NSArray *items = delete.items;
	BOOL archive = delete.archive;
	BOOL softDelete = delete.softDelete;
	
	[xml appendFormat:@"<%@>\n",_Delete];
	
	[xml appendFormat:@"<%@>%@</%@>\n",_CmdID,cmdId,_CmdID];
	
	if(![StringUtil isEmpty:meta])
	{
		meta = [XMLUtil cleanupXML:meta];
		[xml appendFormat:@"<%@>%@</%@>\n",_Meta,meta,_Meta];
	}
	
	if(items != nil)
	{
		for(Item *item in items)
		{
			NSString *itemXml = [SyncXMLGenerator generateItem:item];
			[xml appendString:itemXml];
		}
	}
	
	if(archive)
	{
		[xml appendFormat:@"<%@/>\n",_Archive];
	}
	
	if(softDelete)
	{
		[xml appendFormat:@"<%@/>\n",_SftDel];
	}
	
	[xml appendFormat:@"</%@>\n",_Delete];
	
	return xml;
}
@end