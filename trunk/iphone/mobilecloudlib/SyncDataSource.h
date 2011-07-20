/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>
#import "CloudDBManager.h"
#import "Service.h"
#import "Registry.h"
#import "ChangeLogEntry.h"
#import "SyncError.h"
#import "Anchor.h"

/**
 * 
 * @author openmobster@gmail.com
 */
@interface SyncDataSource : Service 
{

}

+(SyncDataSource *) getInstance;

-(void) clearAll;
@end
