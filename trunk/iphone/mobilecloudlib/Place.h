//
//  Place.h
//  mobilecloudlib
//
//  Created by openmobster on 11/18/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>


@interface Place : NSObject 
{
    @private
    NSString *address;
	NSString *phone;
	NSString *internationalPhoneNumber;
	NSString *url;
	NSString *website;
	NSString *icon;
	NSString *name;
	NSString *latitude;
	NSString *longitude;
	NSString *placeId;
	NSString *reference;
	NSString *rating;
	NSArray *types;
	NSString *vicinity;
	NSString *htmlAttribution;
}

@property(nonatomic,retain)NSString *address;
@property(nonatomic,retain)NSString *phone;
@property(nonatomic,retain)NSString *internationalPhoneNumber;
@property(nonatomic,retain)NSString *url;
@property(nonatomic,retain)NSString *website;
@property(nonatomic,retain)NSString *icon;
@property(nonatomic,retain)NSString *name;
@property(nonatomic,retain)NSString *latitude;
@property(nonatomic,retain)NSString *longitude;
@property(nonatomic,retain)NSString *placeId;
@property(nonatomic,retain)NSString *reference;
@property(nonatomic,retain)NSString *rating;
@property(nonatomic,retain)NSArray  *types;
@property(nonatomic,retain)NSString *vicinity;
@property(nonatomic,retain)NSString *htmlAttribution;

+(id)withInit;

@end
