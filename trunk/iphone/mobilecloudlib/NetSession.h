/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import <Foundation/Foundation.h>
#import <CFNetwork/CFNetwork.h>
#import "StringUtil.h"
#import "SystemException.h"
#import "BufferStreamReader.h"

/**
 * 
 * @author openmobster@gmail.com
 */
@interface NetSession : NSObject 
{
	@private
	CFReadStreamRef is;
	CFWriteStreamRef os;
}

+(id) withInit:(BOOL) secure :(NSString *)host :(int)port;

-(NSString *) performHandshake:(NSString *) request;
-(NSString *) sendPayload:(NSString *) request;
-(void) close;

//used internally only
/** throws SystemException */
-(NSString *) read:(BufferStreamReader *)reader;

-(void) write:(NSString *)payload;

/** throws SystemException */
-(void) writeToStream:(NSData *) packet;

-(NSData *) readFromStream;
@end
