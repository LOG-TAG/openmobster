/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import <Foundation/Foundation.h>
#import "MobileObject.h"
#import "MobileObjectReader.h"
#import "StringUtil.h"
#import "XMLUtil.h"

/**
 * 
 * @author openmobster@gmail.com
 */
@interface DeviceSerializer : NSObject 

+(DeviceSerializer *) getInstance;
+(void)stop;

-(NSString *) serialize:(MobileObject *)mobileObject;
-(MobileObject *)deserialize:(NSString *)xml;
@end
