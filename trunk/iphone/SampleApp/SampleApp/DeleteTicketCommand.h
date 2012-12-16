//
//  DeleteTicketCommand.h
//  SampleApp
//
//  Created by openmobster on 9/7/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "RemoteCommand.h"

/**
 * 
 * @author openmobster@gmail.com
 */
@interface DeleteTicketCommand : NSObject<RemoteCommand> 
{
    
}

+(id)withInit;
@end
