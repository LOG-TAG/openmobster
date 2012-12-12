/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import <Foundation/Foundation.h>
#import "TestContext.h"
#import "Test.h"
#import "SystemException.h"
#import "Kernel.h"

/**
 * 
 * @author openmobster@gmail.com
 */
@interface TestSuite : NSObject 
{
	@private
	NSMutableArray *tests;
	NSMutableArray *errors;
	
	TestContext *context;
	NSString *status;
	NSString *cloudServer;
	NSString *email;
	NSString *password;
}

@property (assign) TestContext *context;
@property (assign) NSString *status;
@property (assign) NSString *cloudServer;
@property (assign) NSString *email;
@property (assign) NSString *password;

+(id) withInit;

-(void) execute;
-(void) addTest:(Test *) test; //had to use id instead of Test due to a circular reference issue
-(void) reportError:(NSString *)error;

@end
