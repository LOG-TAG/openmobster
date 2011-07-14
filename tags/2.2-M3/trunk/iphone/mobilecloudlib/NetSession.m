/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "NetSession.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation NetSession

+(id) withInit:(BOOL) secure :(NSString *)host :(int)port
{
	NetSession *session = [[[NetSession alloc] init] autorelease];
	
	if(secure)
	{
		//Open a socket
		CFStreamCreatePairWithSocketToHost(kCFAllocatorDefault, (CFStringRef)host, port, &(session->is), &(session->os));

		//Setup SSL properties
		NSDictionary *settings = [[NSDictionary alloc] initWithObjectsAndKeys:
								  [NSNumber numberWithBool:YES], kCFStreamSSLAllowsExpiredCertificates,
								  [NSNumber numberWithBool:YES], kCFStreamSSLAllowsAnyRoot,
								  [NSNumber numberWithBool:NO], kCFStreamSSLValidatesCertificateChain,
								  kCFNull,kCFStreamSSLPeerName,
								  nil];
		settings = [settings autorelease];
		
		CFReadStreamSetProperty(session->is, kCFStreamPropertySSLSettings, (CFTypeRef)settings);
		CFWriteStreamSetProperty(session->os, kCFStreamPropertySSLSettings, (CFTypeRef)settings);
	}
	else 
	{
		//Open a socket
		CFStreamCreatePairWithSocketToHost(kCFAllocatorDefault, (CFStringRef)host, port, &(session->is), &(session->os));
	}

	//Open an input stream
	CFReadStreamOpen(session->is);
	
	//Open an output stream
	CFWriteStreamOpen(session->os);
	
	return session;
}

-(NSString *) performHandshake:(NSString *) request
{
	NSString *handshake = [StringUtil trim:request];
	[self write:handshake];
	return [self read];
}

-(NSString *) sendPayload:(NSString *) request
{
	[self write:request];
	return [self read];
}

-(void) close
{
	//Close the readstream
	CFReadStreamClose(is);
	CFRelease(is);
	is = NULL;
	
	//Close the writestream
	CFWriteStreamClose(os);
	CFRelease(os);
	os = NULL;
}

//used internally only
-(NSString *) read
{
	int received = 0;
	NSMutableData *bos = [NSMutableData dataWithLength:0];
	BOOL carriageFound = NO;
	
	while(YES)
	{
		received = [self readFromStream];
		
		if(received == -1)
		{
			//throw exeception that input stream is closed
			NSMutableArray *parameters = [NSMutableArray arrayWithObjects:@"inputstream_isclosed",nil];
			SystemException *se = [SystemException withContext:@"NetSession" method:@"read" parameters:parameters];
			@throw se;
		}
		
		if(carriageFound)
		{
			carriageFound = NO;
			if(received == '\n')
			{
				break;
			}
		}
		
		if(received == '\r')
		{
			carriageFound = YES;
		}
		
		UInt8 bytes[1];
		bytes[0] = received;
		[bos appendBytes:bytes length:1];
	}
	
	//Must append a 'null' terminating character since this will be processed as a C-String
	UInt8 bytes[1];
	bytes[0] = '\0';
	[bos appendBytes:bytes length:1];
	
	//Debug related
	//NSLog(@"ByteArray: %d",[bos length]);
	//NSLog(@"Description: %@",[bos description]);
	
	
	NSString *packet = [NSString stringWithCString:[bos bytes] encoding:NSUTF8StringEncoding];
	
	return [StringUtil trim:packet];
}

-(void) write:(NSString *)payload
{
	int startIndex = 0;
	int endIndex = 0;
	int quitIndex = [payload length]-1;
	NSString *eof = @"EOF\n\0";
	NSData *eofBytes = [eof dataUsingEncoding:NSUTF8StringEncoding];
	BOOL eofSent = NO;
	while((endIndex=[StringUtil indexOf:payload :@"\n" :startIndex])!=-1)
	{
		NSString *packet = [StringUtil substring:payload :startIndex :endIndex];
		
		NSMutableData *localBuffer = [NSMutableData dataWithLength:0];
		[localBuffer appendData:[packet dataUsingEncoding:NSUTF8StringEncoding]];
		[localBuffer appendData:[@"\n\0" dataUsingEncoding:NSUTF8StringEncoding]];
		[self writeToStream:localBuffer];
		
		startIndex = endIndex + 1;
		
		if(startIndex >= quitIndex)
		{
			NSMutableData *eofBuffer = [NSMutableData dataWithLength:0];
			[eofBuffer appendData:eofBytes];
			[self writeToStream:eofBuffer];
			eofSent = YES;
			break;
		}
	}
	
	if(!eofSent)
	{
		NSMutableData *buffer = [NSMutableData dataWithLength:0];
		NSString *packet = [StringUtil substring:payload :startIndex];
		[buffer appendData:[packet dataUsingEncoding:NSUTF8StringEncoding]];
		[buffer appendData:eofBytes];
		[self writeToStream:buffer];
	}
}

-(void) writeToStream:(NSData *) packet
{
	UInt8 *buffer = (UInt8 *)[packet bytes];
	int bufferLen = strlen((char *)buffer);
	
	int bytesWritten = CFWriteStreamWrite(os,buffer,bufferLen);
	
	if(bytesWritten != bufferLen)
	{
		//throw exeception that writing to the outputstream failed
		NSMutableArray *parameters = [NSMutableArray arrayWithObjects:@"outputstream_write_failure",nil];
		SystemException *se = [SystemException withContext:@"NetSession" method:@"writeToStream" parameters:parameters];
		@throw se;
	}
}

-(UInt8) readFromStream
{
	CFIndex numBytesRead;
	UInt8 readBuffer[1];
	
	numBytesRead = CFReadStreamRead(is, readBuffer, sizeof(readBuffer));
	if(numBytesRead > 0) 
	{
		return readBuffer[0];
	} 
	else 
	{
		return -1;
	}	
}
@end
