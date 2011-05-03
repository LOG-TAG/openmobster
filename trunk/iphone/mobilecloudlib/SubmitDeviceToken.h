//
//  SubmitDeviceToken.h
//  mobilecloudlib
//
//  Created by openmobster on 4/28/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>


@interface SubmitDeviceToken : NSObject 
{

}

+(id)withInit;

-(void)submit:(NSString *)deviceToken;
@end
