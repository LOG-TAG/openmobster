//
//  BufferStreamReader.m
//  mobilecloudlib
//
//  Created by openmobster on 1/29/13.
//  Copyright (c) 2013 __MyCompanyName__. All rights reserved.
//

#import "BufferStreamReader.h"

@implementation BufferStreamReader

@synthesize buffer;

-init
{
    if(self == [super init])
	{
		buffer = [[NSMutableData alloc] initWithLength:0];	
    }
	
	return self;
}

+(id) withInit:(NSData *)data
{
    BufferStreamReader *instance = [[[BufferStreamReader alloc] init] autorelease];
    [instance.buffer appendData:data];
    return instance;
}

-(void) dealloc
{
    [buffer release];
    [super dealloc];
}


-(void) fillBuffer:(NSData *)data
{
    [buffer appendData:data]; 
}

-(NSString *)readLine
{
    unsigned char *contents = (unsigned char *)[buffer bytes];
    int length = [buffer length];
    
    NSMutableData *line = [NSMutableData data];
    int pointer = 0;
    BOOL lineFound = NO;
    for(int i=0; i<length; i++)
    {
        UInt8 bytes[1];
        bytes[0] = contents[i];
        [line appendBytes:bytes length:1];
        
        if(bytes[0] == '\n')
        {
            pointer = i+1;
            lineFound = YES;
            break;
        }
    }
    
    if(lineFound)
    {
        //re-position the buffer
        NSMutableData *newBuffer = [NSMutableData data];
        for(int i=pointer; i<length; i++)
        {
            UInt8 bytes[1];
            bytes[0] = contents[i];
            [newBuffer appendBytes:bytes length:1];  
        }
        self.buffer = newBuffer;
        
        //Add a 'NULL' termination character since it will be processed as a C-string
        UInt8 bytes[1];
        bytes[0] = '\0';
        [line appendBytes:bytes length:1]; 
        
        //Construct the Line to be returned
        NSString *lineStr = [NSString stringWithCString:[line bytes] encoding:NSUTF8StringEncoding];
        return lineStr;
    }
    
    return nil;
}

@end
