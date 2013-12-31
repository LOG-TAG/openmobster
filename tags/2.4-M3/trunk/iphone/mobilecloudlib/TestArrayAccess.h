/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import <Foundation/Foundation.h>
#import "AbstractIntegrationTest.h"
#import "MobileObject.h"
#import "MobileBean.h"
#import "BeanList.h"
#import "BeanListEntry.h"

/**
 * 
 * @author openmobster@gmail.com
 */
@interface TestArrayAccess : AbstractIntegrationTest 
{

}

+(id)withInit;

-(void) accessArrays:(NSArray *)beans;
-(void) accessArray:(BeanList *)emails;
@end
