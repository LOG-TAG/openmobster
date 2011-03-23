/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "SyncConstants.h"


/**
 * 
 * @author openmobster@gmail.com
 */

NSString *const _SOURCE = @"source";
NSString *const _TARGET = @"target";
NSString *const _MAX_CLIENT_SIZE = @"maxClientSize";
NSString *const _CLIENT_INITIATED = @"clientInitiated";
NSString *const _DATA_SOURCE = @"dataSource";
NSString *const _DATA_TARGET = @"dataTarget";
NSString *const _SYNC_TYPE = @"syncType";
NSString *const _PAYLOAD = @"payload";

NSString *const _TWO_WAY = @"200";
NSString *const _SLOW_SYNC = @"201";
NSString *const _ONE_WAY_CLIENT = @"202";
NSString *const _ONE_WAY_SERVER = @"204";
NSString *const _STREAM = @"250";
NSString *const _STREAM_RECORD_ID = @"streamRecordId";
NSString *const _BOOT_SYNC = @"260";
NSString *const _SUCCESS = @"200";
NSString *const _AUTH_SUCCESS = @"202";
NSString *const _COMMAND_FAILURE = @"500";
NSString *const _ANCHOR_FAILURE = @"508";
NSString *const _CHUNK_ACCEPTED = @"213";
NSString *const _CHUNK_SUCCESS = @"201";
NSString *const _NEXT_MESSAGE = @"222";
NSString *const _SIZE_MISMATCH = @"424";
NSString *const _GENERIC_SYNC_FAILURE = @"500"; 
NSString *const _AUTHENTICATION_SUCESS = @"212";
NSString *const _AUTHENTICATION_FAILURE = @"401";

NSString *const _SESSION = @"session";
NSString *const _SYNC_ENGINE = @"sync_engine";
NSString *const _SYNC_XML_GENERATOR = @"sync_xml_generator";
NSString *const _SYNC_OBJECT_GENERATOR = @"sync_object_generator";

int const _RESPONSE_CLOSE = 1;

@implementation SyncConstants

@end
