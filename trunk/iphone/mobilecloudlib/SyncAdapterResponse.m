#import "SyncAdapterResponse.h"

@implementation SyncAdapterResponse

@synthesize status;

-init
{
	if(self = [super init])
	{
		attributes = [GenericAttributeManager withInit];
	}
	return self;
}

+(id) withInit
{
	SyncAdapterResponse *response = [[[SyncAdapterResponse alloc] init] autorelease];
	return response;
}

-(void) setAttribute:(NSString *) name :(id) value
{
	[attributes setAttribute:name :value];
}

-(id) getAttribute:(NSString *) attribute
{
	return [attributes getAttribute:attribute];
}

-(void) removeAttribute:(NSString *) attribute
{
	[attributes removeAttribute:attribute];
}

@end
