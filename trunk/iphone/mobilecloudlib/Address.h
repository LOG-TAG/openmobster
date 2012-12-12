//
//  Address.h
//  mobilecloudlib
//
//  Created by openmobster on 11/18/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>

/*!
 Represents an Address associated with the input latitude and longitude
 
 @author openmobster@gmail.com
 */
@interface Address : NSObject 
{
    @private
    NSString *street;
    NSString *city;
	NSString *state;
	NSString *country;
	NSString *zipCode;
	NSString *county;
	NSString *postal;
	NSString *latitude;
	NSString *longitude;
	NSString *radius;
	NSString *woeid;
	NSString *woetype;
}

@property(nonatomic,retain)NSString *street;
@property(nonatomic,retain)NSString *city;
@property(nonatomic,retain)NSString *state;
@property(nonatomic,retain)NSString *country;
@property(nonatomic,retain)NSString *zipCode;
@property(nonatomic,retain)NSString *county;
@property(nonatomic,retain)NSString *postal;
@property(nonatomic,retain)NSString *latitude;
@property(nonatomic,retain)NSString *longitude;
@property(nonatomic,retain)NSString *radius;
@property(nonatomic,retain)NSString *woeid;
@property(nonatomic,retain)NSString *woetype;

/**
 * Get an instance of Address
 */
+(id)withInit;

@end
