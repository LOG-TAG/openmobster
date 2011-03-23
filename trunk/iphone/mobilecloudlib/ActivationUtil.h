/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import <Foundation/Foundation.h>
#import "Request.h"
#import "Response.h"
#import "MobileService.h"
#import "TestSuite.h"
#import "SystemException.h"
#import "Configuration.h"

/**
 * 
 * @author openmobster@gmail.com
 */

extern NSString *const deviceIdentifier;

@interface ActivationUtil : NSObject 
{

}

+(id)withInit;

/** throws SystemException */
-(void)activateDevice:(TestSuite *)testSuite;

//Used internally
-(void)processProvisioningSuccess:(NSString *)email :(Response *)response;
-(void)bootup:(NSString *)deviceIdentifier :(NSString *)serverIp :(NSString *)port;
-(void)handlePostActivation;

@end
