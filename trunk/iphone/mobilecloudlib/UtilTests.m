#import "UtilTests.h"
#import "GeneralTools.h"
#import "GenericAttributeManager.h"
#import "Configuration.h"
#import "CloudDBManager.h"
#import "XMLUtil.h"
#import "Kernel.h"
#import "Bus.h"
#import "QSStrings.h"

@implementation UtilTests

-(void) testGenerateUniqueId
{
	NSLog(@"UniqueId: %@",[GeneralTools generateUniqueId]);
}

-(void) testGetDeviceIdentifier
{
	NSLog(@"DeviceIdentifier: %@",[GeneralTools getDeviceIdentifier]);
}

-(void) testGenericAttributeManager
{
	GenericAttributeManager *attrMgr = [GenericAttributeManager withInit];
	
	if([attrMgr isEmpty])
	{
		NSLog(@"isEmpty: YES");
	}
	else 
	{
		NSLog(@"isEmpty: NO");
	}
	STAssertTrue([attrMgr isEmpty], @"AttributeManager must be empty!!");
	
	NSArray *names = [attrMgr getNames];
	NSArray *values = [attrMgr getValues];
	
	NSLog(@"Names Count: %@",[[NSNumber numberWithLong:[names count]] stringValue]);
	NSLog(@"Value Count: %@",[[NSNumber numberWithLong:[values count]] stringValue]);
	
	STAssertTrue([names count]==0, nil);
	STAssertTrue([values count]==0, nil);

	
	for(int i=0; i<5; i++)
	{
		NSString *index = [[NSNumber numberWithInt:i] stringValue];
		NSString *key = [NSString stringWithFormat:@"%@%@",@"key",index];
		NSString *keyValue = [NSString stringWithFormat:@"%@%@",@"keyValue",index];
		
		[attrMgr setAttribute:key :keyValue];
	}
	
	names = [attrMgr getNames];
	for(NSString *key in names)
	{
		NSLog(@"%@:%@",key,[attrMgr getAttribute:key]);
	}
	
	if([attrMgr isEmpty])
	{
		NSLog(@"isEmpty: YES");
	}
	else 
	{
		NSLog(@"isEmpty: NO");
	}
	STAssertFalse([attrMgr isEmpty], @"AttributeManager must not be empty!!");
	
	names = [attrMgr getNames];
	values = [attrMgr getValues];
	
	NSLog(@"Names Count: %@",[[NSNumber numberWithLong:[names count]] stringValue]);
	NSLog(@"Value Count: %@",[[NSNumber numberWithLong:[values count]] stringValue]);
	
	STAssertTrue([names count]==5, nil);
	STAssertTrue([values count]==5, nil);
	
	//Remove some attributes
	[attrMgr removeAttribute:@"key0"];
	[attrMgr removeAttribute:@"key1"];
	
	names = [attrMgr getNames];
	values = [attrMgr getValues];
	
	NSLog(@"Names Count: %@",[[NSNumber numberWithLong:[names count]] stringValue]);
	NSLog(@"Value Count: %@",[[NSNumber numberWithLong:[values count]] stringValue]);
	
	STAssertTrue([names count]==3, nil);
	STAssertTrue([values count]==3, nil);
}

-(void) testConfigurationStorage
{
	NSLog(@"Starting testConfigurationStorage......");
	
	Configuration *configuration = [Configuration getInstance];
	NSString *owner = @"mockapp";
	
	NSDictionary *systemChannels = configuration.channels;
	NSSet *appChannels = configuration.appChannels;
	if(systemChannels != nil)
	{
		configuration.channels = nil;
	}
	if(appChannels != nil)
	{
		configuration.appChannels = nil;
	}
	
	configuration.serverId = @"http://openmobster.googlecode.com";
	configuration.deviceId = @"IMEI:8675309";
	STAssertTrue(configuration.serverId == @"http://openmobster.googlecode.com",nil);
	STAssertTrue(configuration.deviceId == @"IMEI:8675309",nil);
	
	BOOL success = [configuration saveInstance];
	
	configuration = [Configuration getInstance];
	NSLog(@"ServerId: %@",configuration.serverId);
	NSLog(@"DeviceId: %@",configuration.deviceId);
	STAssertTrue(configuration.serverId == @"http://openmobster.googlecode.com",nil);
	STAssertTrue(configuration.deviceId == @"IMEI:8675309",nil);
	
	configuration.serverId = @"updated://serverId";
	configuration.deviceId = @"updated://deviceId";
	success = [configuration saveInstance];
	
	configuration = [Configuration getInstance];
	NSLog(@"ServerId: %@",configuration.serverId);
	NSLog(@"DeviceId: %@",configuration.deviceId);
	STAssertTrue(configuration.serverId == @"updated://serverId",nil);
	STAssertTrue(configuration.deviceId == @"updated://deviceId",nil);
	STAssertTrue(success,nil);
	
	//Now test AppChannels
	[configuration addAppChannel:@"email"];
	[configuration addAppChannel:@"twitter"];
	[configuration addAppChannel:@"facebook"];
	[configuration saveInstance];
	STAssertTrue([configuration isChannelRegistered:@"email"],nil);
	STAssertTrue([configuration isChannelRegistered:@"twitter"],nil);
	STAssertTrue([configuration isChannelRegistered:@"facebook"],nil);
	
	//Now test system channels
	Channel *emailOwner = [Channel withInit:@"email" :owner];
	Channel *twitterOwner = [Channel withInit:@"twitter" :owner];
	BOOL emailStatus = [configuration establishOwnership:emailOwner :NO];
	BOOL twitterStatus = [configuration establishOwnership:twitterOwner :NO];
	STAssertTrue(emailStatus,nil);
	STAssertTrue(twitterStatus,nil);
	emailStatus = [configuration establishOwnership:emailOwner :NO];
	twitterStatus = [configuration establishOwnership:twitterOwner :NO];
	STAssertTrue(emailStatus,nil);
	STAssertTrue(twitterStatus,nil);
	[configuration saveInstance];
	configuration = [Configuration getInstance];
	
	//isWritable
	Channel *facebookOwner = [Channel withInit:@"facebook" :owner];
	BOOL emailWritable = [configuration isChannelWritable:emailOwner];
	BOOL twitterWritable = [configuration isChannelWritable:twitterOwner];
	BOOL facebookWritable = [configuration isChannelWritable:facebookOwner];
	STAssertTrue(emailWritable,nil);
	STAssertTrue(twitterWritable,nil);
	STAssertFalse(facebookWritable,nil);
	
	
	//Now test the boolean and ints
	NSLog(@"IsActive: %@", [configuration.active stringValue]);
	NSLog(@"IsSSLActive: %@", [configuration.sslActive stringValue]);
	NSLog(@"MaxPacketSize: %@", [configuration.maxPacketSize stringValue]);
	
	configuration.active = [NSNumber numberWithBool:YES];
	configuration.sslActive = [NSNumber numberWithBool:YES];
	configuration.maxPacketSize = [NSNumber numberWithInt:100];
	[configuration saveInstance];
	
	configuration = [Configuration getInstance];
	NSLog(@"IsActive: %@", [configuration.active stringValue]);
	NSLog(@"IsSSLActive: %@", [configuration.sslActive stringValue]);
	NSLog(@"MaxPacketSize: %@", [configuration.maxPacketSize stringValue]);
}

-(void) testCleanupAndRestoreXML
{
	NSLog(@"Starting testCleanupXML......");
	
	NSString *xml = @"<xml>\n\t<name>myname&is<b>l\"a'h</name>\n\t<value>myvalue&isblah&&<<\"'>></value>\n</xml>";
	
	NSString *cleanXml = [XMLUtil cleanupXML:xml];
	NSString *restored = [XMLUtil restoreXML:cleanXml];
	
	NSLog(@"%@",cleanXml);
	NSLog(@"%@",restored);
	STAssertTrue([xml isEqualToString:restored],nil);
}

-(void) testXMLCData
{
	NSLog(@"Starting testXMLCData......");
	
	NSString *xml = @"<xml><name>blah</name></xml>";
	
	NSString *cdata = [XMLUtil addCData:xml];
	NSString *nocdata = [XMLUtil removeCData:cdata];
	
	NSLog(@"With CDATA: %@",cdata);
	NSLog(@"NO CDATA: %@",nocdata);
	STAssertTrue([xml isEqualToString:nocdata],nil);
}

-(void) testLastIndexOf
{
	NSLog(@"Starting testLastIndexOf....");
	
	NSString *path = @"/blah1/blah2/blah3";
	
	int lastIndexOf = [StringUtil lastIndexOf:path :@"/"];
	int notFound = [StringUtil lastIndexOf:@"blah1blah2blah3" :@"/"];
	
	NSLog(@"LastIndex: %d",lastIndexOf);
	NSLog(@"NotFound: %d",notFound);
	
	STAssertTrue(lastIndexOf == 12,nil);
	STAssertTrue(notFound == -1,nil);
}

-(void) testBusSynchronizeConf
{
	NSLog(@"Starting testBusSynchronizeConf.....");
	
	Kernel *kernel = [Kernel getInstance];
	[kernel startup];
	
	[Configuration clear];
	Configuration *configuration = [Configuration getInstance];
	
	NSString *owner = @"mockapp";
	
	//NSStrings
	configuration.serverId = @"http://openmobster.googlecode.com";
	configuration.deviceId = @"IMEI:8675309";
	
	//Collections
	Channel *emailOwner = [Channel withInit:@"email" :owner];
	Channel *twitterOwner = [Channel withInit:@"twitter" :owner];
	Channel *facebookOwner = [Channel withInit:@"facebook" :owner];
	[configuration establishOwnership:emailOwner :NO];
	[configuration establishOwnership:twitterOwner :NO];
	
	//NSNumbers
	configuration.active = [NSNumber numberWithBool:YES];
	[configuration saveInstance];
	
	Bus *bus = [Bus getInstance];
	NSString *confXml = [bus confToXml:configuration];
	
	
	[configuration establishOwnership:facebookOwner :NO];
	NSString *laterXml = [bus confToXml:configuration];
	
	configuration = [Configuration getInstance];
	confXml = [bus confToXml:configuration];
	NSLog(@"%@",confXml);
	
	
	NSLog(@"------------------------------------------------------");
	[bus xmlToConf:laterXml];
	configuration = [Configuration getInstance];
	confXml = [bus confToXml:configuration];
	NSLog(@"%@",confXml);
	
	
	[kernel shutdown];
}

-(void) testBase64
{
    NSLog(@"Running testBase64......");
    
    NSString *encodeMe = @"blahblahblahblahblah";
    NSData *encodeData = [encodeMe dataUsingEncoding:NSUTF8StringEncoding];
    
    NSString *encoded = [QSStrings encodeBase64WithData:encodeData];
    
    NSLog(@"%@",encoded);
    
    NSData *decoded = [QSStrings decodeBase64WithString:encoded];
    
    NSString* decodedStr = [NSString stringWithUTF8String:[decoded bytes]];
    
    NSLog(@"%@",decodedStr);
}
@end