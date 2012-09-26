//
//  BackgroundSyncCommand.m
//  CloudManagerApp
//
//  Created by openmobster on 8/28/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "BackgroundSyncCommand.h"
#import "AppService.h"

@implementation BackgroundSyncCommand

+(id)withInit
{
    BackgroundSyncCommand *instance = [[BackgroundSyncCommand alloc] init];
    instance = [instance autorelease];
    return instance;
}

-(void)doAction:(CommandContext *)commandContext
{
    AppService *appService = [AppService getInstance];
    [appService start];
}
@end
