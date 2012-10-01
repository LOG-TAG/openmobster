/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import <Foundation/Foundation.h>

/**
 * 
 * @author openmobster@gmail.com
 */


extern NSString *const _SOURCE;
extern NSString *const _TARGET;
extern NSString *const _MAX_CLIENT_SIZE;
extern NSString *const _CLIENT_INITIATED;
extern NSString *const _DATA_SOURCE;
extern NSString *const _DATA_TARGET;
extern NSString *const _SYNC_TYPE;
extern NSString *const _PAYLOAD;

extern NSString *const _TWO_WAY;
extern NSString *const _SLOW_SYNC;
extern NSString *const _ONE_WAY_CLIENT;
extern NSString *const _ONE_WAY_SERVER;
extern NSString *const _STREAM;
extern NSString *const _STREAM_RECORD_ID;
extern NSString *const _BOOT_SYNC;
extern NSString *const _SUCCESS;
extern NSString *const _AUTH_SUCCESS;
extern NSString *const _COMMAND_FAILURE;
extern NSString *const _ANCHOR_FAILURE;
extern NSString *const _CHUNK_ACCEPTED;
extern NSString *const _CHUNK_SUCCESS;
extern NSString *const _NEXT_MESSAGE;
extern NSString *const _SIZE_MISMATCH;
extern NSString *const _GENERIC_SYNC_FAILURE;
extern NSString *const _AUTHENTICATION_SUCESS;
extern NSString *const _AUTHENTICATION_FAILURE;

extern NSString *const _SESSION;
extern NSString *const _SYNC_ENGINE;
extern NSString *const _SYNC_XML_GENERATOR;
extern NSString *const _SYNC_OBJECT_GENERATOR;

extern int const _RESPONSE_CLOSE;

@interface SyncConstants : NSObject 
{

}

@end
