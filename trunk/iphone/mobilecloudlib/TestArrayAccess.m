/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "TestArrayAccess.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation TestArrayAccess

+(id)withInit
{
	return [[[TestArrayAccess alloc] init] autorelease];
}

-(void) runTest
{
	SyncService *sync = [SyncService getInstance];
	
	//Add testcase
	[self setUp:@"add"];
	[sync performTwoWaySync:channel :NO];
	[self assertRecordPresence:@"unique-1" :@"/TestSimpleAccess/add"];
	[self assertRecordPresence:@"unique-2" :@"/TestSimpleAccess/add"];
	[self assertRecordPresence:@"unique-3" :@"/TestSimpleAccess/add"];
	[self assertRecordPresence:@"unique-4" :@"/TestSimpleAccess/add"];
	
	NSArray *beans = nil;
	BeanList *emails = nil;
    
    NSAutoreleasePool * pool = [[NSAutoreleasePool alloc] init];
    
    NSLog(@"******************Inside the Pool*****************");
    
    beans = [MobileBean readAll:channel];
    [beans retain];
    
    for(MobileBean *local in beans)
    {
        emails = [local readList:@"emails"];
        
        if([emails size] > 0)
        {
            break;
        }
    }
    [emails retain];
    
	[self accessArray:emails];
    
    [pool drain];
	
    NSLog(@"******************Outside the Pool*****************");
    
    [self accessArrays:beans];
    [self accessArray:emails];
	
	[self tearDown];
}

-(NSString *) getInfo
{
	NSString *info = [NSString stringWithFormat:@"%@%@",[[self class] description],@"TwoWaySync"];
	return info;
}

-(void) accessArrays:(NSArray *)beans
{
    for(MobileBean *bean in beans)
	{
		NSString *beanChannel = [bean getChannel];
		NSString *oid = [bean getId];
		
		NSLog(@"****************************************************");
		NSLog(@"Channel: %@",beanChannel);
		NSLog(@"OID: %@",oid);
		
		BeanList *emails = [bean readList:@"emails"];
		int size = [emails size];
		for(int i=0; i<size; i++)
		{
			BeanListEntry *local = [emails entryAt:i];
			NSString *from = [local getProperty:@"from"];
			NSString *to = [local getProperty:@"to"];
			NSString *subject = [local getProperty:@"subject"];
			NSString *message = [local getProperty:@"message"];
			
			NSLog(@"UID: %@",[local getProperty:@"uid"]);
			NSLog(@"From: %@",from);
			NSLog(@"To: %@",to);
			NSLog(@"Subject: %@",subject);
			NSLog(@"Message: %@",message);
			
		}
		NSLog(@"****************************************************");
		
		BeanList *fruits = [bean readList:@"fruits"];
		size = [fruits size];
		for(int i=0; i<size; i++)
		{
			BeanListEntry *local = [fruits entryAt:i];
			NSLog(@"Fruit of the Day: %@",[local getProperty:@"fruits"]);
		}
		NSLog(@"****************************************************");
	}	
}

-(void) accessArray:(BeanList *)emails
{
    int size = [emails size];
    for(int i=0; i<size; i++)
    {
        BeanListEntry *local = [emails entryAt:i];
        NSString *from = [local getProperty:@"from"];
        NSString *to = [local getProperty:@"to"];
        NSString *subject = [local getProperty:@"subject"];
        NSString *message = [local getProperty:@"message"];
        
        NSLog(@"UID: %@",[local getProperty:@"uid"]);
        NSLog(@"From: %@",from);
        NSLog(@"To: %@",to);
        NSLog(@"Subject: %@",subject);
        NSLog(@"Message: %@",message);
    }
}
@end
