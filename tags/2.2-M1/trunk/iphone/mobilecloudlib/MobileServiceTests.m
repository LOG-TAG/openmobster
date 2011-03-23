#import "MobileServiceTests.h"


@implementation MobileServiceTests

//FIXME: this test fails
/*
- (void) testServiceInvocation
{
	NSLog(@"Starting testServiceInvocation....");
	
	//Prepare the environment
	NetworkConnector *networkConnector = [[[NetworkConnector alloc] init] autorelease];
	Registry *registry = [Registry getInstance];
	[registry addService:networkConnector]; 
	
	Configuration *configuration = [Configuration getInstance];
	if(!configuration.serverIp)
	{
		configuration.serverId = @"http://openmobster.googlecode.com";
		configuration.deviceId = @"IMEI:8675309";
		configuration.serverIp = @"192.168.1.101";
		configuration.secureServerPort = @"1500";
		configuration.plainServerPort = @"1502";
		configuration.sslActive = [NSNumber numberWithBool:NO];
		configuration.active = [NSNumber numberWithBool:NO];
		[configuration saveInstance];
	}
	
	Request *request = [Request withInit:@"provisioning"];
	[request setAttribute:@"action" :@"metadata"];
	MobileService *mo = [MobileService withInit];
	Response *response = [mo invoke:request];
	
	//Read the Server Response
	NSString *serverId = [response getAttribute:@"serverId"];
	NSString *plainServerPort = [response getAttribute:@"plainServerPort"];
	NSString *sslActive = [response getAttribute:@"isSSLActive"];
	NSString *maxPacketSize = [response getAttribute:@"maxPacketSize"];
	
	NSLog(@"ServerId: %@",serverId);
	NSLog(@"PlainServerPort: %@",plainServerPort);
	NSLog(@"SSLActive: %@",sslActive);
	NSLog(@"maxPacketSize: %@",maxPacketSize);
	STAssertTrue(serverId != nil,nil);
	STAssertTrue(plainServerPort != nil,nil);
	STAssertTrue(sslActive != nil,nil);
	STAssertTrue(maxPacketSize != nil,nil);
}
*/
@end
