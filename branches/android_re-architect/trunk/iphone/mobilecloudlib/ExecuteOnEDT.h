/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import <Foundation/Foundation.h>
#import "CommandContext.h"
#import "ActivityIndicatorView.h"
#import "UICommandDelegate.h"
#import "SystemException.h"
#import "Command.h"
#import "LocalCommand.h"
#import "RemoteCommand.h"
#import "AsyncCommand.h"
#import "BusyCommand.h"
#import "AppException.h"

/**
 * 
 * @author openmobster@gmail.com
 */
@interface ExecuteOnEDT : NSObject 
{
	@private 
	CommandContext *commandContext;
	NSOperationQueue *queue;
	ActivityIndicatorView *activityIndicatorView;
}

@property (nonatomic,retain) CommandContext *commandContext;

+(id)withInit:(CommandContext *)commandContext;

-(void)run;

//internal use only
-(void)startBusyCommand;
-(void)invokeBusyAction;
-(void)endBusyCommand;

-(void)startAsyncCommand;
-(void)invokeAsyncAction;
-(void)endAsyncCommand;

-(void)startLocalCommand;
-(void)endLocalCommand;
@end
