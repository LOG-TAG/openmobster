/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import <Foundation/Foundation.h>
#import "RemoteCommand.h"
#import "Configuration.h"
#import "Request.h"
#import "Response.h"
#import "MobileService.h"
#import "GeneralTools.h"
#import "ErrorHandler.h"
#import "SystemException.h"
#import "AppException.h"

/**
 * 
 * @author openmobster@gmail.com
 */
@interface ActivateDevice : NSObject<RemoteCommand> 
{

}

+(id)withInit;

//Used internally
-(void)activateDevice:(NSString *)deviceIdentifier :(NSString *)cloudIp :(NSString *)cloudPort;
-(void)processProvisioningSuccess:(NSString *)email :(Response *)response;
@end
