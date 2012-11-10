/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "SyncObjectGenerator.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation SyncObjectGenerator

+(id) withInit
{
	SyncObjectGenerator *gen = [[[SyncObjectGenerator alloc] init] autorelease];
	
	gen->alertPath = [NSString stringWithFormat:@"/%@/%@/%@",_SyncML,_SyncBody,_Alert];
	gen->statusPath = [NSString stringWithFormat:@"/%@/%@/%@",_SyncML,_SyncBody,_Status];
	gen->syncPath = [NSString stringWithFormat:@"/%@/%@/%@",_SyncML,_SyncBody,_Sync];
	gen->itemPath = [NSString stringWithFormat:@"/%@",_Item];
	gen->addPath = [NSString stringWithFormat:@"/%@/%@",_Sync,_Add];
	gen->replacePath = [NSString stringWithFormat:@"/%@/%@",_Sync,_Replace];
	gen->deletePath = [NSString stringWithFormat:@"/%@/%@",_Sync,_Delete];
	gen->moreDataPath = [NSString stringWithFormat:@"/%@/%@",_Item,_MoreData];
	gen->finalPath = [NSString stringWithFormat:@"/%@/%@/%@",_SyncML,_SyncBody,_Final];
	gen->deleteArchivePath = [NSString stringWithFormat:@"/%@/%@/%@",_Sync,_Delete,_Archive];
	gen->deleteSoftDeletePath = [NSString stringWithFormat:@"/%@/%@/%@",_Sync,_Delete,_SftDel];
	
	gen->sessionIdPath = [NSString stringWithFormat:@"/%@/%@/%@",_SyncML,_SyncHdr,_SessionID];
    gen->appPath = [NSString stringWithFormat:@"/%@/%@/%@",_SyncML,_SyncHdr,_App];
	gen->sourceUriPath = [NSString stringWithFormat:@"/%@/%@/%@/%@",_SyncML,_SyncHdr,_Source,_LocURI];
	gen->targetUriPath = [NSString stringWithFormat:@"/%@/%@/%@/%@",_SyncML,_SyncHdr,_Target,_LocURI];
	gen->msgIdPath = [NSString stringWithFormat:@"/%@/%@/%@",_SyncML,_SyncHdr,_MsgID];
	gen->maxMsgSizePath = [NSString stringWithFormat:@"/%@/%@/%@/%@",_SyncML,_SyncHdr,_Meta,_MaxMsgSize];
	
	return gen;
}

-(Session *) parse:(NSString *) syncXml
{
	NSData *xmlData = [syncXml dataUsingEncoding:NSUTF8StringEncoding];
	NSXMLParser *parser = [[[NSXMLParser alloc] initWithData:xmlData] autorelease];
	
	//Set the Deletegate to self
	[parser setDelegate:self];
	
	//Start parsing
	[parser parse];
	
	return session;
}
//NSXMLParserDelegate implementation-----------------------------------------------------------
-(void)parserDidStartDocument:(NSXMLParser *)parser
{
	session = [Session withInit];
	syncMessage = [SyncMessage withInit];
	session.currentMessage = syncMessage;
	
	fullPath = [NSMutableString stringWithString:@""];
	dataBuffer = [NSMutableString stringWithString:@""];
}

- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict
{
	[fullPath appendFormat:@"/%@",[StringUtil trim:elementName]];
	dataBuffer = [NSMutableString stringWithString:@""];
	
	//AlertPath
	if([fullPath isEqualToString:alertPath])
	{
		courAlert = [Alert withInit];
	}
	
	//Status Path
	if([fullPath isEqualToString:statusPath])
	{
		courStatus = [Status withInit];
	}
	
	//SyncCommand Path
	if([fullPath isEqualToString:syncPath])
	{
		courCommand = [SyncCommand withInit];
	}
	
	//Add Path
	if([fullPath hasSuffix:addPath])
	{
		courAdd = [Add withInit];
	}
	
	//Replace Path
	if([fullPath hasSuffix:replacePath])
	{
		courReplace = [Replace withInit];
	}
	
	//Delete Path
	if([fullPath hasSuffix:deletePath])
	{
		courDelete = [Delete withInit];
	}
	
	//Archive Delete Path
	if([fullPath hasSuffix:deleteArchivePath])
	{
		courDelete.archive = YES;
	}
	
	//SoftDelete Path
	if([fullPath hasSuffix:deleteSoftDeletePath])
	{
		courDelete.softDelete = YES;
	}
	
	//Item Path
	if([fullPath hasSuffix:itemPath])
	{
		courItem = [Item withInit];
	}	
	
	//MoreData
	if([fullPath hasSuffix:moreDataPath])
	{
		courItem.moreData = YES;
	}
	
	//Final
	if([fullPath isEqualToString:finalPath])
	{
		syncMessage.final = YES;
	}
}

-(void)parser:(NSXMLParser *)parser didEndElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName
{
	//Alert paths
	NSString *alertCmdId = [NSString stringWithFormat:@"%@/%@",alertPath,_CmdID];
	NSString *alertData = [NSString stringWithFormat:@"%@/%@",alertPath,_Data];
	
	//Status paths
	NSString *statusCmdId = [NSString stringWithFormat:@"%@/%@",statusPath,_CmdID];
	NSString *statusData = [NSString stringWithFormat:@"%@/%@",statusPath,_Data];
	NSString *statusMsgRef = [NSString stringWithFormat:@"%@/%@",statusPath,_MsgRef];
	NSString *statusCmdRef = [NSString stringWithFormat:@"%@/%@",statusPath,_CmdRef];
	NSString *statusCmd = [NSString stringWithFormat:@"%@/%@",statusPath,_Cmd];
	NSString *statusSourceRef = [NSString stringWithFormat:@"%@/%@",statusPath,_SourceRef];
	NSString *statusTargetRef = [NSString stringWithFormat:@"%@/%@",statusPath,_TargetRef];
	NSString *chalType = [NSString stringWithFormat:@"%@/%@/%@/%@",statusPath,_Chal,_Meta,_Type];
	NSString *chalFormat = [NSString stringWithFormat:@"%@/%@/%@/%@",statusPath,_Chal,_Meta,_Format];
	NSString *chalNextNonce = [NSString stringWithFormat:@"%@/%@/%@/%@",statusPath,_Chal,_Meta,_NextNonce];
	
	//Sync Paths
	NSString *syncCmdId = [NSString stringWithFormat:@"/%@/%@",_Sync,_CmdID];
	NSString *syncSourceUri = [NSString stringWithFormat:@"/%@/%@/%@",_Sync,_Source,_LocURI];
	NSString *syncTargetUri = [NSString stringWithFormat:@"/%@/%@/%@",_Sync,_Target,_LocURI];
	NSString *syncMeta = [NSString stringWithFormat:@"/%@/%@",_Sync,_Meta];
	NSString *syncNumberOfChanges = [NSString stringWithFormat:@"/%@/%@",_Sync,_NumberOfChanges];
	
	//Item Paths
	NSString *itemSourceUri = [NSString stringWithFormat:@"/%@/%@/%@",_Item,_Source,_LocURI];
	NSString *itemTargetUri = [NSString stringWithFormat:@"/%@/%@/%@",_Item,_Target,_LocURI];
	NSString *itemMeta = [NSString stringWithFormat:@"/%@/%@",_Item,_Meta];
	NSString *itemData = [NSString stringWithFormat:@"/%@/%@",_Item,_Data];
	
	//Add Paths
	NSString *addMeta = [NSString stringWithFormat:@"%@/%@",addPath,_Meta];
	NSString *addCmdId = [NSString stringWithFormat:@"%@/%@",addPath,_CmdID];
	
	//Replace Paths
	NSString *replaceMeta = [NSString stringWithFormat:@"%@/%@",replacePath,_Meta];
	NSString *replaceCmdId = [NSString stringWithFormat:@"%@/%@",replacePath,_CmdID];
	
	//Delete Paths
	NSString *deleteMeta = [NSString stringWithFormat:@"%@/%@",deletePath,_Meta];
	NSString *deleteCmdId = [NSString stringWithFormat:@"%@/%@",deletePath,_CmdID];
	
	if([fullPath isEqualToString:sessionIdPath])
	{
		session.sessionId = dataBuffer;
	}
    else if([fullPath isEqualToString:appPath])
    {
        session.app = dataBuffer;
    }
	else if([fullPath isEqualToString:sourceUriPath])
	{
		session.source = dataBuffer;
	}
	else if([fullPath isEqualToString:targetUriPath])
	{
		session.target = dataBuffer;
	}
	
	//Processing SyncMessage related data
	else if([fullPath isEqualToString:msgIdPath])
	{
		syncMessage.messageId = dataBuffer;
	}
	else if([fullPath isEqualToString:maxMsgSizePath])
	{
		syncMessage.maxClientSize = [dataBuffer intValue];
	}
	
	//Processing Alert
	else if([fullPath isEqualToString:alertCmdId])
	{
		courAlert.cmdId = dataBuffer;
	}
	else if([fullPath isEqualToString:alertData])
	{
		courAlert.data = dataBuffer;
	}
	
	//Processing Status
	else if([fullPath isEqualToString:statusCmdId])
	{
		courStatus.cmdId = dataBuffer;
	}
	else if([fullPath isEqualToString:statusData])
	{
		courStatus.data = dataBuffer;
	}
	else if([fullPath isEqualToString:statusMsgRef])
	{
		courStatus.msgRef = dataBuffer;
	}
	else if([fullPath isEqualToString:statusCmdRef])
	{
		courStatus.cmdRef = dataBuffer;
	}
	else if([fullPath isEqualToString:statusCmd])
	{
		courStatus.cmd = dataBuffer;
	}
	else if([fullPath isEqualToString:statusSourceRef])
	{
		[courStatus addSourceRef:dataBuffer];
	}
	else if([fullPath isEqualToString:statusTargetRef])
	{
		[courStatus addTargetRef:dataBuffer];
	}
	else if([fullPath isEqualToString:chalType])
	{
		Credential *credential = courStatus.credential;
		if(credential == nil)
		{
			credential = [Credential withInit];
			courStatus.credential = credential;
		}
		credential.type = dataBuffer;
	}
	else if([fullPath isEqualToString:chalFormat])
	{
		Credential *credential = courStatus.credential;
		if(credential == nil)
		{
			credential = [Credential withInit];
			courStatus.credential = credential;
		}
		credential.format = dataBuffer;
	}
	else if([fullPath isEqualToString:chalNextNonce])
	{
		Credential *credential = courStatus.credential;
		if(credential == nil)
		{
			credential = [Credential withInit];
			courStatus.credential = credential;
		}
		credential.nextNonce = dataBuffer;
	}
	
	//Process SyncCommand
	else if([fullPath hasSuffix:syncCmdId])
	{
		courCommand.cmdId = dataBuffer;
	}
	else if([fullPath hasSuffix:syncSourceUri])
	{
		courCommand.source = dataBuffer;
	}
	else if([fullPath hasSuffix:syncTargetUri])
	{
		courCommand.target = dataBuffer;
	}
	else if([fullPath hasSuffix:syncMeta])
	{
		courCommand.meta = dataBuffer;
	}
	else if([fullPath hasSuffix:syncNumberOfChanges])
	{
		courCommand.numberOfChanges = dataBuffer;
	}
	
	//Process Add
	else if([fullPath hasSuffix:addMeta])
	{
		courAdd.meta = dataBuffer;
	}
	else if([fullPath hasSuffix:addCmdId])
	{
		courAdd.cmdId = dataBuffer;
	}
	
	//Process Replace
	else if([fullPath hasSuffix:replaceMeta])
	{
		courReplace.meta = dataBuffer;
	}
	else if([fullPath hasSuffix:replaceCmdId])
	{
		courReplace.cmdId = dataBuffer;
	}
	
	//Process Delete
	else if([fullPath hasSuffix:deleteMeta])
	{
		courDelete.meta = dataBuffer;
	}
	else if([fullPath hasSuffix:deleteCmdId])
	{
		courDelete.cmdId = dataBuffer;
	}
	
	//Process Item
	else if([fullPath hasSuffix:itemSourceUri])
	{
		courItem.source = dataBuffer;
	}
	else if([fullPath hasSuffix:itemTargetUri])
	{
		courItem.target = dataBuffer;
	}
	else if([fullPath hasSuffix:itemMeta])
	{
		courItem.meta = dataBuffer;
	}
	else if([fullPath hasSuffix:itemData])
	{
		courItem.data = dataBuffer;
	}
	
	//Organize the populated Object model
	if([fullPath isEqualToString:alertPath])
	{
		if(courAlert != nil)
		{
			[syncMessage addAlert:courAlert];
		}
		courAlert = nil;
	}
	
	if([fullPath isEqualToString:statusPath])
	{
		if(courStatus != nil)
		{
			[syncMessage addStatus:courStatus];
		}
		courStatus = nil;
	}
	
	if([fullPath isEqualToString:syncPath])
	{
		if(courCommand != nil)
		{
			[syncMessage addCommand:courCommand];
		}
		courCommand = nil;
	}
	
	if([fullPath hasSuffix:addPath])
	{
		if(courAdd != nil)
		{
			[courCommand addOperation:courAdd];
		}
		courAdd = nil;
	}
	
	if([fullPath hasSuffix:replacePath])
	{
		if(courReplace != nil)
		{
			[courCommand addOperation:courReplace];
		}
		courReplace = nil;
	}
	
	if([fullPath hasSuffix:deletePath])
	{
		if(courDelete != nil)
		{
			[courCommand addOperation:courDelete];
		}
		courDelete = nil;
	}
	
	
	if([fullPath hasSuffix:itemPath])
	{				
		NSString *alertItem = [NSString stringWithFormat:@"/%@/%@",_Alert,_Item];
		NSString *statusItem = [NSString stringWithFormat:@"/%@/%@",_Status,_Item];
		NSString *addItem = [NSString stringWithFormat:@"/%@/%@",_Add,_Item];
		NSString *replaceItem = [NSString stringWithFormat:@"/%@/%@",_Replace,_Item];
		NSString *deleteItem = [NSString stringWithFormat:@"/%@/%@",_Delete,_Item];
		if([fullPath hasSuffix:alertItem])
		{
			[courAlert addItem:courItem];
		}
		else if([fullPath hasSuffix:statusItem])
		{
			[courStatus addItem:courItem];
		}
		else if([fullPath hasSuffix:addItem])
		{
			[courAdd addItem:courItem];
		}
		else if([fullPath hasSuffix:replaceItem])
		{
			[courReplace addItem:courItem];
		}
		else if([fullPath hasSuffix:deleteItem])
		{
			[courDelete addItem:courItem];
		}
		courItem = nil;
	}
	
	//Reset
	int lastIndex = [StringUtil lastIndexOf:fullPath :@"/"];
	int length = [fullPath length];
	int numberOfCharactersToDelete = length - lastIndex;
	NSRange deleteRange = NSMakeRange(lastIndex, numberOfCharactersToDelete);
	[fullPath deleteCharactersInRange:deleteRange];
}

-(void)parser:(NSXMLParser *)parser foundCharacters:(NSString *)string
{
	if(![StringUtil isEmpty:string])
	{
		[dataBuffer appendString:string];
	}
}

-(void)parser:(NSXMLParser *)parser foundCDATA:(NSData *)CDATABlock
{
	if(CDATABlock != nil)
	{
		NSString *string = [[NSString alloc] initWithData:CDATABlock encoding:NSUTF8StringEncoding];
		string = [string autorelease];
		if(![StringUtil isEmpty:string])
		{
			[dataBuffer appendString:string];
		}		
	}
}
@end