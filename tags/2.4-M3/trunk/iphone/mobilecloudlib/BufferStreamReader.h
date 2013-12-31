//
//  BufferStreamReader.h
//  mobilecloudlib
//
//  Created by openmobster on 1/29/13.
//  Copyright (c) 2013 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface BufferStreamReader : NSObject
{
    @private
    NSMutableData *buffer;
}
@property (nonatomic,retain) NSMutableData *buffer;


+(id) withInit:(NSData *)data;



-(void) fillBuffer:(NSData *)data;
-(NSString *)readLine;
@end
