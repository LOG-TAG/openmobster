#import "Configuration.h"
#import "CloudDBManager.h"
#import "StringUtil.h"


@implementation Configuration

@dynamic deviceId;
@dynamic serverId;
@dynamic channels;
@dynamic active;
@dynamic sslActive;
@dynamic maxPacketSize;
@dynamic serverIp;
@dynamic plainServerPort;
@dynamic secureServerPort;
@dynamic httpPort;
@dynamic authenticationHash;
@dynamic authenticationNonce;
@dynamic email;
@dynamic pushMode;
@dynamic pollInterval;
@dynamic appChannels;

+(Configuration *) getInstance
{
	//Get the Storage Context
	NSManagedObjectContext *managedContext = [[CloudDBManager getInstance] storageContext];
	NSEntityDescription *entity = [NSEntityDescription entityForName:@"Configuration" inManagedObjectContext:managedContext];
	
	//Get an instance if its already been provisioned
	NSFetchRequest *request = [[[NSFetchRequest alloc] init] autorelease];
	[request setEntity:entity];
	
	NSArray *all = [managedContext executeFetchRequest:request error:NULL];
	
	Configuration *conf;
	if(all != nil && [all count] > 0)
	{
		//Returns an already present instance
		conf = [all objectAtIndex:0];
	}
	else 
	{
		//Creates a new instance and returns it
		conf = [NSEntityDescription insertNewObjectForEntityForName:@"Configuration" inManagedObjectContext:managedContext];
		conf.deviceId = @"";
		conf.active = [NSNumber numberWithBool:NO];
		conf.maxPacketSize = [NSNumber numberWithInt:0];
		conf.serverId = @"";
		[managedContext save:NULL];
	}

	return conf;
}

+(BOOL)clear
{
	//Get the Storage Context
	NSManagedObjectContext *managedContext = [[CloudDBManager getInstance] storageContext];
	
	//Get the config instance
	Configuration *conf = [Configuration getInstance];
	
	[managedContext deleteObject:conf];
	
	NSError* error;
	BOOL success = [managedContext save:&error];
	
	return success;
}

-(BOOL) saveInstance
{
	//Get the Storage Context
	NSManagedObjectContext *managedContext = [[CloudDBManager getInstance] storageContext];
	
	NSError* error;
	BOOL success = [managedContext save:&error];
	
	//Used for debugging
	if(!success)
	{
		NSLog(@"Error during save!!!");
		NSLog(@"SaveError: %@, %@", error, [error userInfo]);
	}
	
	return success;
}

-(NSString *) authHash
{
	if(![StringUtil isEmpty:self.authenticationNonce])
	{
		return [StringUtil trim:self.authenticationNonce];
	}
	return self.authenticationHash;
}

-(NSString *) serverPort
{
	if([self.sslActive boolValue])
	{
		return self.secureServerPort;
	}
	return self.plainServerPort;
}

-(BOOL) isInPushMode
{
	return [self.pushMode boolValue];
}

-(BOOL) isActivated
{
	return [self.active boolValue];
}

//System-wide channel operations
-(BOOL)establishOwnership:(Channel *)channel :(BOOL)force
{
	if(self.channels == nil)
	{
		self.channels = [NSMutableDictionary dictionary];
	}
	
	NSString *channelName = channel.name;
	NSString *channelOwner = channel.owner;
	[self addAppChannel:channelName];
	if(!force)
	{
		NSString *registeredOwner = (NSString *)[self.channels objectForKey:channelName];
		if(registeredOwner == nil)
		{
			[self.channels setObject:channelOwner forKey:channelName];
			return YES;
		}
		else if([registeredOwner isEqualToString:channelOwner])
		{
			return YES;
		}
	}
	else 
	{
		[self.channels setObject:channelOwner forKey:channelName];
		return YES;
	}

	return NO;
}

-(NSDictionary *)getChannelRegistry
{
	return [NSDictionary dictionaryWithDictionary:self.channels];
}

//App-level channel operations
-(void)addAppChannel:(NSString *)appChannel
{
	//Initialize if not already present
	if(self.appChannels == nil)
	{
		self.appChannels = [NSMutableSet set];
	}
	
	if(![self.appChannels containsObject:appChannel])
	{
		[self.appChannels addObject:appChannel];
	}
}

-(BOOL)isChannelRegistered:(NSString *)appChannel
{
	if(self.appChannels == nil)
	{
		self.appChannels = [NSMutableSet set];
	}
	
	return [self.appChannels containsObject:appChannel];
}

-(BOOL)isChannelWritable:(Channel *)appChannel
{
	if(self.channels == nil)
	{
		self.channels = [NSMutableDictionary dictionary];
	}
	
	if(self.appChannels == nil)
	{
		self.appChannels = [NSMutableSet set];
	}
	
	NSString *channelName = appChannel.name;
	NSString *channelOwner = appChannel.owner;
	if([self.appChannels containsObject:channelName])
	{
		NSString *registeredOwner = (NSString *)[self.channels objectForKey:channelName];
		
		if(registeredOwner != nil && [registeredOwner isEqualToString:channelOwner])
		{
			return YES;
		}
	}
	
	return NO;
}
@end