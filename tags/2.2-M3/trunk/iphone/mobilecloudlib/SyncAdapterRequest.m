#import "SyncAdapterRequest.h"


@implementation SyncAdapterRequest

-(id) init
{
	if(self = [super init])
	{
		attributes = [GenericAttributeManager withInit];
	}
	return self;
}

+(id) withInit
{
	SyncAdapterRequest *request = [[[SyncAdapterRequest alloc] init] autorelease];
	return request;
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
