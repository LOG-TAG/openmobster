/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "SyncDataSource.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation SyncDataSource

+(SyncDataSource *) getInstance
{
	Registry *registry = [Registry getInstance];
	return (SyncDataSource *)[registry lookup:[SyncDataSource class]];
}

-(void) clearAll
{
	//Delete all sync errors
	[SyncError deleteAll];
	
	//Delete the changelog
	[ChangeLogEntry deleteAll];
}
@end
