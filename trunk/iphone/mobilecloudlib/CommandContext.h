/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "GenericAttributeManager.h"
#import "AppException.h"

/**
 * 
 * @author openmobster@gmail.com
 */
@interface CommandContext : NSObject 
{
	@private
	GenericAttributeManager *attrMgr;
	UIViewController *caller;
}

@property (nonatomic,assign) UIViewController *caller;

+(id)withInit:(UIViewController *)caller;

-(void)setAttribute:(NSString *)name :(id)value;
-(id)getAttribute:(NSString *)name;

-(void)setTarget:(id)target;
-(id)getTarget;

-(void)setError:(NSString *)code :(NSString *)message;
-(NSString *)getErrorCode;
-(NSString *)getErrorMessage;
-(BOOL)hasErrors;

-(void)activateTimeout;
-(void)deactivateTimeout;
-(BOOL)isTimeoutActivated;

-(void)setAppException:(AppException *)appe;
-(AppException *)getAppException;
@end
