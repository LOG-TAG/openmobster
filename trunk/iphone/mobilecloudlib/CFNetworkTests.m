#import "CFNetworkTests.h"
#import "NetSession.h"
#import "NetworkConnector.h"


@implementation CFNetworkTests
- (void) testSimpleHttpGet 
{
    NSLog(@"Starting testSimpleHttpGet......");  
	
	CFStringRef url = CFSTR("http://www.google.com");
	CFURLRef myURL = CFURLCreateWithString(kCFAllocatorDefault, url, NULL);
	CFStringRef requestMethod = CFSTR("GET");
	CFHTTPMessageRef myRequest = CFHTTPMessageCreateRequest(kCFAllocatorDefault, requestMethod, myURL,kCFHTTPVersion1_1);
	
	CFReadStreamRef myReadStream = CFReadStreamCreateForHTTPRequest(kCFAllocatorDefault,myRequest);
	
	
	//Open the read stream
	if(!CFReadStreamOpen(myReadStream))
	{
		NSLog(@"Stream Open Failed!!!");
		
		CFStreamError myErr = CFReadStreamGetError(myReadStream);
		
        if (myErr.domain == kCFStreamErrorDomainPOSIX) 
		{
			// Interpret myErr.error as a UNIX errno.
			NSLog(@"POSIX Error Code: %d",myErr.error);
        } 
		else if (myErr.domain == kCFStreamErrorDomainMacOSStatus) 
		{
			
			// Interpret myErr.error as a MacOS error code.
            //OSStatus macError = (OSStatus)myErr.error;
			NSLog(@"MacOS Error Code: %d",myErr.error);
		}
		
		return;
	}
	
	NSLog(@"Stream is successfully opened.....");
	
	//Read the Stream
	CFIndex numBytesRead;
	do 
	{
		UInt8 buf[1024];
		
		numBytesRead = CFReadStreamRead(myReadStream, buf, sizeof(buf));
		
		if(numBytesRead > 0) 
		{
			NSString *packet = [NSString alloc];
			packet = [[packet initWithBytes:buf length:numBytesRead encoding:NSUTF8StringEncoding] autorelease];
			
			NSLog(@"%@",packet);
		} 
		else if(numBytesRead < 0) 
		{
			//CFStreamError error = CFReadStreamGetError(myReadStream);
			NSLog(@"Error occured while reading the stream!!!");
		}
		
	} while(numBytesRead > 0);
	
	//Close the stream
	CFReadStreamClose(myReadStream);
	CFRelease(myReadStream);
	myReadStream = NULL;
}

-(void) testSocketStream
{
	NSLog(@"Starting testSocketStream......");
	
	NSString *host = @"192.168.1.101";
	
	CFReadStreamRef readStream = NULL;
	CFWriteStreamRef writeStream = NULL;
	
	CFStreamCreatePairWithSocketToHost(kCFAllocatorDefault, (CFStringRef)host, 1502, &readStream, &writeStream);
	
	//Open the OutputStream
	CFWriteStreamOpen(writeStream);
	UInt8 buf[] = "<request><header><name>processor</name><value>/testdrive/pull</value></header></request>";
	UInt8 eof[] = "EOF\n";
	[self writeToStream:writeStream :buf];
	[self writeToStream:writeStream :eof];
	
	//Read the Stream
	CFReadStreamOpen(readStream);
	NSString *response = [self readFromStream:readStream];
	if(response != nil)
	{
		NSLog(@"%@",response);
	}
	
	UInt8 pull[] = "<pull><caller name='iphone'/></pull>EOF\n";
	[self writeToStream:writeStream :pull];
	
	response = [self readFromStream:readStream];
	if(response != nil)
	{
		NSLog(@"%@",response);
	}
	
	//Close the readstream
	CFReadStreamClose(readStream);
	CFRelease(readStream);
	readStream = NULL;
	
	//Close the writestream
	CFWriteStreamClose(writeStream);
	CFRelease(writeStream);
	writeStream = NULL;
}
-(void) testSecureSocketStream
{
	NSLog(@"Starting testSecureSocketStream......");
	
	NSString *host = @"192.168.1.101";
	
	CFReadStreamRef readStream = NULL;
	CFWriteStreamRef writeStream = NULL;
	
	CFStreamCreatePairWithSocketToHost(kCFAllocatorDefault, (CFStringRef)host, 1500, &readStream, &writeStream);
	
	//Setup SSL properties
	NSDictionary *settings = [[NSDictionary alloc] initWithObjectsAndKeys:
							  [NSNumber numberWithBool:YES], kCFStreamSSLAllowsExpiredCertificates,
							  [NSNumber numberWithBool:YES], kCFStreamSSLAllowsAnyRoot,
							  [NSNumber numberWithBool:NO], kCFStreamSSLValidatesCertificateChain,
							  kCFNull,kCFStreamSSLPeerName,
							  nil];
	settings = [settings autorelease];
	
	CFReadStreamSetProperty(readStream, kCFStreamPropertySSLSettings, (CFTypeRef)settings);
	CFWriteStreamSetProperty(writeStream, kCFStreamPropertySSLSettings, (CFTypeRef)settings);
	
	//Open the OutputStream
	CFWriteStreamOpen(writeStream);
	UInt8 buf[] = "<request><header><name>processor</name><value>/testdrive/pull</value></header></request>EOF\n";
	[self writeToStream:writeStream :buf];
	
	//Read the Stream
	CFReadStreamOpen(readStream);
	NSString *response = [self readFromStream:readStream];
	if(response != nil)
	{
		NSLog(@"%@",response);
	}
	
	UInt8 pull[] = "<pull><caller name='iphone'/></pull>EOF\n";
	[self writeToStream:writeStream :pull];
	
	response = [self readFromStream:readStream];
	if(response != nil)
	{
		NSLog(@"%@",response);
	}
	
	//Close the readstream
	CFReadStreamClose(readStream);
	CFRelease(readStream);
	readStream = NULL;
	
	//Close the writestream
	CFWriteStreamClose(writeStream);
	CFRelease(writeStream);
	writeStream = NULL;
}
//FIXME: This test fails
/*
-(void) testNetSession
{
	NSLog(@"Starting testNetSession.....");
	
	NSString *payload1 = @"<request>\n<header>\n<name>processor</name>\n<value>/testdrive/pull</value>\n</header>\n</request>";
	NSString *payload2 = @"<pull>\n<caller name='iphone'/>\n</pull>";
	
	[self runNetSessionTest:payload1 :payload2];
	
	NSString *payload3 = @"<request><header><name>processor</name><value>/testdrive/pull</value></header></request>";
	NSString *payload4 = @"<pull><caller name='iphone'/></pull>";
	
	[self runNetSessionTest:payload3 :payload4];
}
*/

//FIXME: this test fails
/*
-(void) testNetConnector
{
	NSLog(@"Starting testNetConnector.....");
	
	Configuration *configuration = [Configuration getInstance];
	if(!configuration.serverIp)
	{
		configuration.serverId = @"http://openmobster.googlecode.com";
		configuration.deviceId = @"IMEI:8675309";
		configuration.serverIp = @"192.168.1.102";
		configuration.secureServerPort = @"1500";
		configuration.plainServerPort = @"1502";
		configuration.sslActive = [NSNumber numberWithBool:YES];
		[configuration saveInstance];
	}
	
	NSString *payload1 = @"<request>\n<header>\n<name>processor</name>\n<value>/testdrive/pull</value>\n</header>\n</request>";
	NSString *payload2 = @"<pull>\n<caller name='iphone'/>\n</pull>";
	[self runNetworkConnectorTest:NO :payload1 :payload2];
	[self runNetworkConnectorTest:YES :payload1 :payload2];
}
*/
//----------------------------------------------------------------------------------------------
-(void) runNetSessionTest:(NSString *)handshake :(NSString *)payload
{
	NetSession *session = [NetSession withInit:NO :@"192.168.1.101" :1502];
		
	NSString *response = [session performHandshake:handshake];
	NSLog(@"%@",response);
	STAssertTrue([response isEqualToString:@"status=200"],nil);
	
	response = [session sendPayload:payload];
	NSLog(@"%@",response);
	STAssertTrue([response isEqualToString:@"success"],nil);
	
	//close the session
	[session close];
}

-(void) runNetworkConnectorTest:(BOOL) secure :(NSString *)handshake :(NSString *)payload
{
	NetworkConnector *connector = [[[NetworkConnector alloc] init] autorelease];
	NetSession *session = [connector openSession:secure];
	
	NSString *response = [session performHandshake:handshake];
	NSLog(@"%@",response);
	STAssertTrue([response isEqualToString:@"status=200"],nil);
	
	response = [session sendPayload:payload];
	NSLog(@"%@",response);
	STAssertTrue([response isEqualToString:@"success"],nil);
	
	//close the session
	[session close];
}

-(void) writeToStream:(CFWriteStreamRef) writeStream :(UInt8 *)data
{
	int bufLen = strlen((char *)data);
	int bytesWritten = CFWriteStreamWrite(writeStream,data,bufLen);
	
	if(bytesWritten < 0 || bytesWritten != bufLen)
	{
		NSLog(@"Write Stream Error Occurred!!!");
		return;
	}
}

-(NSString *)readFromStream:(CFReadStreamRef) readStream
{
	CFIndex numBytesRead;
	UInt8 readBuffer[1024];
	
	numBytesRead = CFReadStreamRead(readStream, readBuffer, sizeof(readBuffer));
	if(numBytesRead > 0) 
	{
		NSString *packet = [NSString alloc];
		packet = [[packet initWithBytes:readBuffer length:numBytesRead encoding:NSUTF8StringEncoding] autorelease];
		return packet;
	} 
	else if(numBytesRead < 0) 
	{
		NSLog(@"Error occured while reading the stream!!!");
		return nil;
	}	
	return nil;
}
@end
