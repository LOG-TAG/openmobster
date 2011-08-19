/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import <Foundation/Foundation.h>

/*!
    MobileBeanMetaData contains meta information related to a particular MobileBean
 
 @author openmobster@gmail.com
 */
@interface MobileBeanMetaData : NSObject 
{
	@private
	NSString *service;
	NSString *oid;
	BOOL deleted;
}

@property (nonatomic,retain) NSString *service;
@property (nonatomic,retain) NSString *oid;
@property (nonatomic,assign) BOOL deleted;

+(MobileBeanMetaData *)withInit:(NSString *)service :(NSString *)oid;

@end
