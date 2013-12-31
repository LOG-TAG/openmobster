/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import <Foundation/Foundation.h>
#import "Service.h"
#import "Registry.h"
#import "MobileObject.h"
#import "GenericAttributeManager.h"
#import "PersistentMobileObject.h"
#import "SystemException.h"
#import "LogicExpression.h"

/**
 * 
 * @author openmobster@gmail.com
 */
@interface MobileObjectDatabase : Service 
{

}

+(MobileObjectDatabase *) getInstance;

-(NSArray *)readAll:(NSString *)channel;

-(MobileObject *)read:(NSString *)channel:(NSString *)recordId;

-(NSString *)create:(MobileObject *)mobileObject;

-(void)update:(MobileObject *)mobileObject;

-(void)delete:(MobileObject *)mobileObject;

-(void)deleteAll:(NSString *)channel;

-(NSSet *)query:(NSString *)channel:(GenericAttributeManager *)queryAttributes;

-(BOOL) isBooted:(NSString *)channel;

-(NSFetchedResultsController *)searchExactMatchAND:(NSString *)channel :(GenericAttributeManager *)criteria;

-(NSFetchedResultsController *)searchExactMatchOR:(NSString *)channel :(GenericAttributeManager *)criteria;

-(NSFetchedResultsController *)readProxyCursor:(NSString *)channel;

//returns MetaData objects
-(NSFetchedResultsController *)readByName:(NSString *)channel :(NSString *)name :(BOOL) ascending;

//returns MetaData objects
-(NSFetchedResultsController *)readByName:(NSString *)channel :(NSString *)name;

//returns MetaData objects
-(NSFetchedResultsController *)readByNameValuePair:(NSString *)channel :(NSString *)name :(NSString *)value;


//internally used
-(NSSet *)logicExpressionBeans:(NSString *) channel :(LogicExpression *) expression;

@end
