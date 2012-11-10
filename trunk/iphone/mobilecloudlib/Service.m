#import "Service.h"

@implementation Service

@synthesize oid;

-(void) dealloc
{
    [oid release];
    [super dealloc];
}

@end
