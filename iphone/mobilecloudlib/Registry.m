#import "Registry.h"
#import "SystemException.h"

static Registry *singleton = nil;

@implementation Registry

-init
{
	if(self = [super init])
	{
		//Initialize the services array
		services = [[NSMutableArray alloc] init];
		isStarted = NO;
	}
	
	return self;
}

-(void)dealloc
{
	[super dealloc];
	[services release];
}

+(Registry *) getInstance
{
	if(singleton)
	{
		return singleton;
	}
	
	@synchronized([Registry class])
	{
		if(singleton == nil)
		{
			singleton = [[Registry alloc] init];
		}
	}
	return singleton;
}

+(BOOL) isActive
{
	if(singleton != nil)
	{
		return YES;
	}
	return NO;
}

-(BOOL) isStarted
{
	return isStarted;
}

-(void)start
{
	isStarted = YES;
}

-(void)stop
{
	if(singleton != nil)
	{
		[self release];
		singleton = nil;
	}
}

-(Service *) lookup:(Class) serviceClass
{
	//Validate the state of the registry
	if(services == nil)
	{
		SystemException *illegalArgument = 
		[SystemException withContext:@"Registry" method:@"lookup" parameters:nil];
		
		@throw illegalArgument;
	}
	
	//Search the registry and return the proper registered 'Service'
	for(Service *cour in services)
	{
		if([cour class] == serviceClass)
		{
			return cour;
		}
	}
	
	return nil;
}

-(void)addService:(Service *)service
{
	[services addObject:service];
}
@end
