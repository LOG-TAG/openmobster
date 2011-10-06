//
//  DeviceManager.h
//  mobilecloudlib
//
//  Created by openmobster on 3/17/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "Service.h"
#import "Registry.h"
#import "SystemException.h"
#import "ErrorHandler.h"
#import "Configuration.h"
#import "Request.h"
#import "Response.h"
#import "MobileService.h"

@interface DeviceManager : Service 
{

}

+(DeviceManager *)getInstance;
-(void)sendOsCallback;

@end
