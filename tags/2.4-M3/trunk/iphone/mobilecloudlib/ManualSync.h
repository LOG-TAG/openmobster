//
//  ManualSync.h
//  mobilecloudlib
//
//  Created by openmobster on 8/28/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "RemoteCommand.h"

@interface ManualSync : NSObject<RemoteCommand>

+(id)withInit;

@end
