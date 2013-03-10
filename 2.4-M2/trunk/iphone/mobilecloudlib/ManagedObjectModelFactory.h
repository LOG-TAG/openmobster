//
//  ManagedObjectModelFactory.h
//  mobilecloudlib
//
//  Created by openmobster on 5/27/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>


@interface ManagedObjectModelFactory : NSObject 
{

}

+(id) withInit;

-(NSManagedObjectModel *) buildConfigurationModel;

-(NSManagedObjectModel *) buildMobileObjectModel;

-(NSManagedObjectModel *) buildAnchorModel;

-(NSManagedObjectModel *) buildChangeLogModel;

-(NSManagedObjectModel *) buildSyncErrorModel;
@end
