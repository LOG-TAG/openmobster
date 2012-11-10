//
//  BackgroundSyncCommand.h
//  CloudManagerApp
//
//  Created by openmobster on 8/28/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "RemoteCommand.h"

@interface BackgroundSyncCommand : NSObject<RemoteCommand>

+(id)withInit;

@end
