//
//  SyncUtility.h
//  mobilecloudlib
//
//  Created by openmobster on 8/26/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface SyncUtility : NSObject

+(id)withInit;

-(void)resetChannel:(NSString *)channel;
-(void)syncChannel:(NSString *)channel;
-(void)syncAll;
@end
