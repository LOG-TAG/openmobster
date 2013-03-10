#import "Credential.h"


@implementation Credential

@synthesize type;
@synthesize nextNonce;
@synthesize data;
@synthesize format;

+(id) withInit
{
	return [[[Credential alloc] init] autorelease];
}

@end
