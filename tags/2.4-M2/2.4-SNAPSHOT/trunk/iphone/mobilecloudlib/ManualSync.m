//
//  ManualSync.m
//  mobilecloudlib
//
//  Created by openmobster on 8/28/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "ManualSync.h"
#import "AppException.h"
#import "SyncUtility.h"

@implementation ManualSync

+(id)withInit
{
	ManualSync *instance = [[ManualSync alloc] init];
	instance = [instance autorelease];
	return instance;
}

-(void)doAction:(CommandContext *)commandContext
{
    @try 
    {
        NSString *channel = [commandContext getAttribute:@"channel"];
        NSString *syncType = [commandContext getAttribute:@"sync-type"];
        
        SyncUtility *util = [SyncUtility withInit];
        if([syncType isEqualToString:@"reset"])
        {
            [util resetChannel:channel];
        }
        else
        {
            [util syncChannel:channel];
        }
    }
    @catch (NSException *exception) 
    {
        AppException *appe = [AppException withInit:@"Sync Error" :[exception reason]];
        @throw appe;
    }
}
@end
