#import "MapItem.h"

@implementation MapItem

@synthesize source;
@synthesize target;

+(id) withInit
{
	return [[[MapItem alloc] init] autorelease];
}

@end