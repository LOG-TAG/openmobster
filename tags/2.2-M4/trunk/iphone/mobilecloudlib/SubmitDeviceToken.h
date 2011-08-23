//
//  SubmitDeviceToken.h
//  mobilecloudlib
//
//  Created by openmobster on 4/28/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>

/*!
    Submits the unique device token and registers it with the Cloud. This is a necessary step
    required by the Apple Push Notification Service
 @author openmobster@gmail.com
*/
@interface SubmitDeviceToken : NSObject 
{

}

+(id)withInit;

/**
 *  Submits the device token to the Cloud. Once registered this App can receive Push notifications
 *
 *  @param deviceToken the unique device token
 */
-(void)submit:(NSString *)deviceToken;
@end
