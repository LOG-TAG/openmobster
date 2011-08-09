//
//  ManagedObjectModelFactory.m
//  mobilecloudlib
//
//  Created by openmobster on 5/27/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import "ManagedObjectModelFactory.h"


@implementation ManagedObjectModelFactory

+(id) withInit
{
	ManagedObjectModelFactory *instance = [[ManagedObjectModelFactory alloc] init];
	instance = [instance autorelease];
	
	return instance;
}

-(NSManagedObjectModel *) buildConfigurationModel
{
	NSManagedObjectModel *mom = [[NSManagedObjectModel alloc] init];
	mom = [mom autorelease];
	
	//Set the Entity
	NSEntityDescription *entity = [[NSEntityDescription alloc] init];
    [entity setName:@"Configuration"];
    [entity setManagedObjectClassName:@"Configuration"];
    [mom setEntities:[NSArray arrayWithObject:entity]];
	[entity release];
	
	//Properties
	NSMutableArray *properties = [NSMutableArray array];
	
	//Set the deviceId attribute
	NSAttributeDescription *deviceIdAttribute = [[NSAttributeDescription alloc] init];
    [deviceIdAttribute setName:@"deviceId"];
    [deviceIdAttribute setAttributeType:NSStringAttributeType];
    [deviceIdAttribute setOptional:NO];
	[properties addObject:deviceIdAttribute];
	[deviceIdAttribute release];
	
	//Set the active attribute
	NSAttributeDescription *activeAttribute = [[NSAttributeDescription alloc] init];
    [activeAttribute setName:@"active"];
    [activeAttribute setAttributeType:NSBooleanAttributeType];
    [activeAttribute setOptional:NO];
	[activeAttribute setDefaultValue:NO];
	[properties addObject:activeAttribute];
	[activeAttribute release];
	
	//Set the MaxPacketSize
	NSAttributeDescription *maxPacketSizeAttribute = [[NSAttributeDescription alloc] init];
    [maxPacketSizeAttribute setName:@"maxPacketSize"];
    [maxPacketSizeAttribute setAttributeType:NSInteger32AttributeType];
    [maxPacketSizeAttribute setOptional:NO];
	[maxPacketSizeAttribute setDefaultValue:0];
	[properties addObject:maxPacketSizeAttribute];
	[maxPacketSizeAttribute release];
	
	//Set the ServerId 
	NSAttributeDescription *serverId = [[NSAttributeDescription alloc] init];
    [serverId setName:@"serverId"];
    [serverId setAttributeType:NSStringAttributeType];
    [serverId setOptional:NO];
	[properties addObject:serverId];
	[serverId release];
	
	//Set the AppChannels
	NSAttributeDescription *appChannels = [[NSAttributeDescription alloc] init];
    [appChannels setName:@"appChannels"];
    [appChannels setAttributeType:NSTransformableAttributeType];
    [appChannels setOptional:YES];
	[properties addObject:appChannels];
	[appChannels release];
	
	//Set the Channels
	NSAttributeDescription *channels = [[NSAttributeDescription alloc] init];
    [channels setName:@"channels"];
    [channels setAttributeType:NSTransformableAttributeType];
    [channels setOptional:YES];
	[properties addObject:channels];
	[channels release];
	
	//Set pollInterval
	NSAttributeDescription *pollInterval = [[NSAttributeDescription alloc] init];
    [pollInterval setName:@"pollInterval"];
    [pollInterval setAttributeType:NSInteger32AttributeType];
    [pollInterval setOptional:YES];
	[pollInterval setDefaultValue:0];
	[properties addObject:pollInterval];
	[pollInterval release];
	
	//Set pushMode
	NSAttributeDescription *pushMode = [[NSAttributeDescription alloc] init];
    [pushMode setName:@"pushMode"];
    [pushMode setAttributeType:NSBooleanAttributeType];
    [pushMode setOptional:YES];
	[properties addObject:pushMode];
	[pushMode release];
	
	//Set sslActive
	NSAttributeDescription *sslActive = [[NSAttributeDescription alloc] init];
    [sslActive setName:@"sslActive"];
    [sslActive setAttributeType:NSBooleanAttributeType];
    [sslActive setOptional:NO];
	[sslActive setDefaultValue:NO];
	[properties addObject:sslActive];
	[sslActive release];
	
	//Set AuthenticationHash
	NSAttributeDescription *hash = [[NSAttributeDescription alloc] init];
    [hash setName:@"authenticationHash"];
    [hash setAttributeType:NSStringAttributeType];
    [hash setOptional:YES];
	[properties addObject:hash];
	[hash release];
	
	//Set AuthenticationNonce
	NSAttributeDescription *nonce = [[NSAttributeDescription alloc] init];
    [nonce setName:@"authenticationNonce"];
    [nonce setAttributeType:NSStringAttributeType];
    [nonce setOptional:YES];
	[properties addObject:nonce];
	[nonce release];
	
	//Set email
	NSAttributeDescription *email = [[NSAttributeDescription alloc] init];
    [email setName:@"email"];
    [email setAttributeType:NSStringAttributeType];
    [email setOptional:YES];
	[properties addObject:email];
	[email release];
	
	//Set httpPort
	NSAttributeDescription *httpPort = [[NSAttributeDescription alloc] init];
    [httpPort setName:@"httpPort"];
    [httpPort setAttributeType:NSStringAttributeType];
    [httpPort setOptional:YES];
	[properties addObject:httpPort];
	[httpPort release];
	
	//Set plainServerPort
	NSAttributeDescription *plainServerPort = [[NSAttributeDescription alloc] init];
    [plainServerPort setName:@"plainServerPort"];
    [plainServerPort setAttributeType:NSStringAttributeType];
    [plainServerPort setOptional:YES];
	[properties addObject:plainServerPort];
	[plainServerPort release];
	
	//Set secureServerPort
	NSAttributeDescription *secureServerPort = [[NSAttributeDescription alloc] init];
    [secureServerPort setName:@"secureServerPort"];
    [secureServerPort setAttributeType:NSStringAttributeType];
    [secureServerPort setOptional:YES];
	[properties addObject:secureServerPort];
	[secureServerPort release];
	
	//Set serverIp
	NSAttributeDescription *serverIp = [[NSAttributeDescription alloc] init];
    [serverIp setName:@"serverIp"];
    [serverIp setAttributeType:NSStringAttributeType];
    [serverIp setOptional:YES];
	[properties addObject:serverIp];
	[serverIp release];
	
	[entity setProperties:properties];
	
	return mom;	
}

-(NSManagedObjectModel *) buildMobileObjectModel
{
	NSManagedObjectModel *mom = [[NSManagedObjectModel alloc] init];
	mom = [mom autorelease];
	
	//Set the Entity
	NSEntityDescription *entity = [[NSEntityDescription alloc] init];
    [entity setName:@"PersistentMobileObject"];
    [entity setManagedObjectClassName:@"PersistentMobileObject"];
    [mom setEntities:[NSArray arrayWithObject:entity]];
	[entity release];
	
	//Properties
	NSMutableArray *properties = [NSMutableArray array];
	
	//Set the createdOnDevice attribute
	NSAttributeDescription *createdOnDevice = [[NSAttributeDescription alloc] init];
    [createdOnDevice setName:@"createdOnDevice"];
    [createdOnDevice setAttributeType:NSBooleanAttributeType];
    [createdOnDevice setOptional:NO];
	[createdOnDevice setDefaultValue:NO];
	[properties addObject:createdOnDevice];
	[createdOnDevice release];
	
	//Set the locked attribute
	NSAttributeDescription *locked = [[NSAttributeDescription alloc] init];
    [locked setName:@"locked"];
    [locked setAttributeType:NSBooleanAttributeType];
    [locked setOptional:NO];
	[locked setDefaultValue:NO];
	[properties addObject:locked];
	[locked release];
	
	//Set the proxy attribute
	NSAttributeDescription *proxy = [[NSAttributeDescription alloc] init];
    [proxy setName:@"proxy"];
    [proxy setAttributeType:NSBooleanAttributeType];
    [proxy setOptional:NO];
	[proxy setDefaultValue:NO];
	[properties addObject:proxy];
	[proxy release];
	
	//Set the dirtyStatus attribute
	NSAttributeDescription *dirtyStatus = [[NSAttributeDescription alloc] init];
    [dirtyStatus setName:@"dirtyStatus"];
    [dirtyStatus setAttributeType:NSStringAttributeType];
    [dirtyStatus setOptional:YES];
	[properties addObject:dirtyStatus];
	[dirtyStatus release];
	
	//Set the oid attribute
	NSAttributeDescription *oid = [[NSAttributeDescription alloc] init];
    [oid setName:@"oid"];
    [oid setAttributeType:NSStringAttributeType];
    [oid setOptional:NO];
	[properties addObject:oid];
	[oid release];
	
	//Set serverRecordId attribute
	NSAttributeDescription *serverRecordId = [[NSAttributeDescription alloc] init];
    [serverRecordId setName:@"serverRecordId"];
    [serverRecordId setAttributeType:NSStringAttributeType];
    [serverRecordId setOptional:YES];
	[properties addObject:serverRecordId];
	[serverRecordId release];
	
	//set service attribute
	NSAttributeDescription *service = [[NSAttributeDescription alloc] init];
    [service setName:@"service"];
    [service setAttributeType:NSStringAttributeType];
    [service setOptional:NO];
	[properties addObject:service];
	[service release];
	
	//set the arraymetadata attribute
	NSAttributeDescription *arrayMetaData = [[NSAttributeDescription alloc] init];
    [arrayMetaData setName:@"arrayMetaData"];
    [arrayMetaData setAttributeType:NSTransformableAttributeType];
    [arrayMetaData setOptional:YES];
	[properties addObject:arrayMetaData];
	[arrayMetaData release];
	
	//set the fields attribute
	NSAttributeDescription *fields = [[NSAttributeDescription alloc] init];
    [fields setName:@"fields"];
    [fields setAttributeType:NSTransformableAttributeType];
    [fields setOptional:YES];
	[properties addObject:fields];
	[fields release];
    
    //Set nameValuePairs attribute
	NSAttributeDescription *nameValuePairs = [[NSAttributeDescription alloc] init];
    [nameValuePairs setName:@"nameValuePairs"];
    [nameValuePairs setAttributeType:NSStringAttributeType];
    [nameValuePairs setOptional:YES];
	[properties addObject:nameValuePairs];
	[nameValuePairs release];
	
	[entity setProperties:properties];
	
	return mom;	
}

-(NSManagedObjectModel *) buildAnchorModel
{
	NSManagedObjectModel *mom = [[NSManagedObjectModel alloc] init];
	mom = [mom autorelease];
	
	//Set the Entity
	NSEntityDescription *entity = [[NSEntityDescription alloc] init];
    [entity setName:@"Anchor"];
    [entity setManagedObjectClassName:@"Anchor"];
    [mom setEntities:[NSArray arrayWithObject:entity]];
	[entity release];
	
	//Properties
	NSMutableArray *properties = [NSMutableArray array];
	
	//Set the lastSync attribute
	NSAttributeDescription *lastSync = [[NSAttributeDescription alloc] init];
    [lastSync setName:@"lastSync"];
    [lastSync setAttributeType:NSStringAttributeType];
    [lastSync setOptional:NO];
	[properties addObject:lastSync];
	[lastSync release];
	
	//set the nextsync attribute
	NSAttributeDescription *nextSync = [[NSAttributeDescription alloc] init];
    [nextSync setName:@"nextSync"];
    [nextSync setAttributeType:NSStringAttributeType];
    [nextSync setOptional:NO];
	[properties addObject:nextSync];
	[nextSync release];
	
	//set the oid attribute
	NSAttributeDescription *oid = [[NSAttributeDescription alloc] init];
    [oid setName:@"oid"];
    [oid setAttributeType:NSStringAttributeType];
    [oid setOptional:NO];
	[properties addObject:oid];
	[oid release];
	
	//set the target attribute
	NSAttributeDescription *target = [[NSAttributeDescription alloc] init];
    [target setName:@"target"];
    [target setAttributeType:NSStringAttributeType];
    [target setOptional:NO];
	[properties addObject:target];
	[target release];
	
	
	[entity setProperties:properties];
	
	return mom;	
}

-(NSManagedObjectModel *) buildChangeLogModel
{
	NSManagedObjectModel *mom = [[NSManagedObjectModel alloc] init];
	mom = [mom autorelease];
	
	//Set the Entity
	NSEntityDescription *entity = [[NSEntityDescription alloc] init];
    [entity setName:@"ChangeLogEntry"];
    [entity setManagedObjectClassName:@"ChangeLogEntry"];
    [mom setEntities:[NSArray arrayWithObject:entity]];
	[entity release];
	
	//Properties
	NSMutableArray *properties = [NSMutableArray array];
	
	//Set the nodeId attribute
	NSAttributeDescription *nodeId = [[NSAttributeDescription alloc] init];
    [nodeId setName:@"nodeId"];
    [nodeId setAttributeType:NSStringAttributeType];
    [nodeId setOptional:NO];
	[properties addObject:nodeId];
	[nodeId release];
	
	//set the operation attribute
	NSAttributeDescription *operation = [[NSAttributeDescription alloc] init];
    [operation setName:@"operation"];
    [operation setAttributeType:NSStringAttributeType];
    [operation setOptional:NO];
	[properties addObject:operation];
	[operation release];
	
	//set the recordId attribute
	NSAttributeDescription *recordId = [[NSAttributeDescription alloc] init];
    [recordId setName:@"recordId"];
    [recordId setAttributeType:NSStringAttributeType];
    [recordId setOptional:NO];
	[properties addObject:recordId];
	[recordId release];
	
	
	[entity setProperties:properties];
	
	return mom;	
}

-(NSManagedObjectModel *) buildSyncErrorModel
{
	NSManagedObjectModel *mom = [[NSManagedObjectModel alloc] init];
	mom = [mom autorelease];
	
	//Set the Entity
	NSEntityDescription *entity = [[NSEntityDescription alloc] init];
    [entity setName:@"SyncError"];
    [entity setManagedObjectClassName:@"SyncError"];
    [mom setEntities:[NSArray arrayWithObject:entity]];
	[entity release];
	
	//Properties
	NSMutableArray *properties = [NSMutableArray array];
	
	//Set the code attribute
	NSAttributeDescription *code = [[NSAttributeDescription alloc] init];
    [code setName:@"code"];
    [code setAttributeType:NSStringAttributeType];
    [code setOptional:NO];
	[properties addObject:code];
	[code release];
	
	//set the source attribute
	NSAttributeDescription *source = [[NSAttributeDescription alloc] init];
    [source setName:@"source"];
    [source setAttributeType:NSStringAttributeType];
    [source setOptional:NO];
	[properties addObject:source];
	[source release];
	
	//set the target attribute
	NSAttributeDescription *target = [[NSAttributeDescription alloc] init];
    [target setName:@"target"];
    [target setAttributeType:NSStringAttributeType];
    [target setOptional:NO];
	[properties addObject:target];
	[target release];
	
	[entity setProperties:properties];
	
	return mom;	
}
@end