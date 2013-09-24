//
//  MetaData.h
//  mobilecloudlib
//
//  Created by openmobster on 9/23/13.
//  Copyright (c) 2013 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>
#import "PersistentMobileObject.h"

@interface MetaData : NSManagedObject
{
    
}
@property (nonatomic,retain) PersistentMobileObject *parent;
@property (nonatomic,retain) NSString *name;
@property (nonatomic,retain) NSString *value;

@end
