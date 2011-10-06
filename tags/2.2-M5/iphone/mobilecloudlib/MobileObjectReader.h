/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import <Foundation/Foundation.h>
#import "MobileObject.h"

/**
 * 
 * @author openmobster@gmail.com
 */
@interface MobileObjectReader : NSObject<NSXMLParserDelegate> 
{
	@private
	NSMutableArray *fields;
	NSMutableArray *arrayMetaData;
	NSMutableString *fullPath;
	NSMutableString *dataBuffer;
	Field *currentField;
	ArrayMetaData *currentMetaData;
	NSString *recordId;
	NSString *serverRecordId;
	BOOL proxy;
	NSString *currentElement;
}

+(id) withInit;

-(MobileObject *)parse:(NSString *)xml;

//invoked internally
-(void)closeDataBuffer;
@end
