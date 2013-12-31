//
//  QueryTests.m
//  mobilecloudlib
//
//  Created by openmobster on 9/20/13.
//  Copyright (c) 2013 __MyCompanyName__. All rights reserved.
//

#import "QueryTests.h"
#import <UIKit/UIKit.h>
#import "MobileObjectDatabase.h"
#import "MobileObject.h"
#import "GenericAttributeManager.h"
#import "MetaData.h"

@implementation QueryTests


- (void)testSearchExactMatchAND
{
    MobileObjectDatabase *database = [[MobileObjectDatabase alloc] init];
	[database autorelease];
    
    [database deleteAll:@"myChannel"];
    
    //Create and store mobileobjects
    for(int i=0; i<5; i++)
    {
        MobileObject *mobileObject = [MobileObject withInit];
        mobileObject.service = @"myChannel";
        
        NSString *fromName = @"from";
        NSString *fromValue = [NSString stringWithFormat:@"%d/from/value",i];
        [mobileObject setValue:fromName value:fromValue];
        
        NSString *toName = @"to";
        NSString *toValue = [NSString stringWithFormat:@"%d/to/value",i];
        [mobileObject setValue:toName value:toValue];
        
        
        NSString *oid = [database create:mobileObject];
        NSLog(@"OID (Generated): %@",oid);
        STAssertTrue(oid != nil,nil);
        
        //Now update the mobileObject
        mobileObject = [database read:@"myChannel" :oid];
        NSString *value = [NSString stringWithFormat:@"%d/message/value",i];
        [mobileObject setValue:@"message.value" value:value];
        [database update:mobileObject];
    }
    
    GenericAttributeManager *criteria = [[GenericAttributeManager alloc] initWithRetention];
    criteria = [criteria autorelease];
    [criteria setAttribute:@"to" :@"0/to/value"];
    [criteria setAttribute:@"from" :@"0/from/value"];

    NSFetchedResultsController *cursor = [database searchExactMatchAND:@"myChannel" :criteria];
    if(cursor != nil)
    {
        NSArray *fetchedObjects = [cursor fetchedObjects];
        int count = [fetchedObjects count];
        STAssertTrue(count==1,nil);
        for(int i=0; i<count; i++)
        {
            PersistentMobileObject *local = [fetchedObjects objectAtIndex:i];
            MobileObject *mo = [local parseMobileObject];
            
            NSLog(@"OID: %@",local.oid);
            NSLog(@"NameValuePairs: %@",local.nameValuePairs);
            NSSet *metadata = local.metadata;
            for(MetaData *nameValuePair in metadata)
            {
                NSLog(@"SyncId: %@",nameValuePair.parent.oid);
                NSLog(@"Name: %@",nameValuePair.name);
                NSLog(@"Value: %@",nameValuePair.value);
                NSLog(@"******************************");
            }
            
            [self print:mo];
        }
    }
}

- (void)testSearchExactMatchOR
{
    MobileObjectDatabase *database = [[MobileObjectDatabase alloc] init];
	[database autorelease];
    
    [database deleteAll:@"myChannel"];
    
    //Create and store mobileobjects
    for(int i=0; i<10; i++)
    {
        MobileObject *mobileObject = [MobileObject withInit];
        mobileObject.service = @"myChannel";
        
        NSString *fromName = @"from";
        NSString *fromValue = [NSString stringWithFormat:@"%d/from/value",i];
        [mobileObject setValue:fromName value:fromValue];
        
        NSString *toName = @"to";
        NSString *toValue = [NSString stringWithFormat:@"%d/to/value",i];
        [mobileObject setValue:toName value:toValue];
        
        
        NSString *oid = [database create:mobileObject];
        NSLog(@"OID (Generated): %@",oid);
        STAssertTrue(oid != nil,nil);
    }
    
    GenericAttributeManager *criteria = [[GenericAttributeManager alloc] initWithRetention];
    criteria = [criteria autorelease];
    [criteria setAttribute:@"to" :@"0/to/value"];
    [criteria setAttribute:@"from" :@"1/from/value"];
    
    NSFetchedResultsController *cursor = [database searchExactMatchOR:@"myChannel" :criteria];
    if(cursor != nil)
    {
        NSArray *fetchedObjects = [cursor fetchedObjects];
        int count = [fetchedObjects count];
        STAssertTrue(count==2,nil);
        for(int i=0; i<count; i++)
        {
            PersistentMobileObject *local = [fetchedObjects objectAtIndex:i];
            
            NSLog(@"OID: %@",local.oid);
            NSLog(@"NameValuePairs: %@",local.nameValuePairs);
        }
    }
}

-(void) testSortByName
{
    MobileObjectDatabase *database = [[MobileObjectDatabase alloc] init];
	[database autorelease];
    
    [database deleteAll:@"myChannel"];
    [database deleteAll:@"anotherChannel"];
    
    //Create and store mobileobjects
    for(int i=0; i<5; i++)
    {
        MobileObject *mobileObject = [MobileObject withInit];
        mobileObject.service = @"myChannel";
        
        NSString *fromName = @"from";
        NSString *fromValue = [NSString stringWithFormat:@"%d/from/value",i];
        [mobileObject setValue:fromName value:fromValue];
        
        NSString *toName = @"to";
        NSString *toValue = [NSString stringWithFormat:@"%d/to/value",i];
        [mobileObject setValue:toName value:toValue];
        
        
        NSString *oid = [database create:mobileObject];
        NSLog(@"OID (Generated): %@",oid);
        STAssertTrue(oid != nil,nil);
        
        //Now update the mobileObject
        mobileObject = [database read:@"myChannel" :oid];
        NSString *value = [NSString stringWithFormat:@"%d/message/value",i];
        [mobileObject setValue:@"message.value" value:value];
        [database update:mobileObject];
    } 
    
    for(int i=0; i<5; i++)
    {
        MobileObject *mobileObject = [MobileObject withInit];
        mobileObject.service = @"anotherChannel";
        
        NSString *fromName = @"from";
        NSString *fromValue = [NSString stringWithFormat:@"%d/from/value",i];
        [mobileObject setValue:fromName value:fromValue];
        
        NSString *toName = @"to";
        NSString *toValue = [NSString stringWithFormat:@"%d/to/value",i];
        [mobileObject setValue:toName value:toValue];
        
        
        NSString *oid = [database create:mobileObject];
        NSLog(@"OID (Generated): %@",oid);
        STAssertTrue(oid != nil,nil);
        
        //Now update the mobileObject
        mobileObject = [database read:@"anotherChannel" :oid];
        NSString *value = [NSString stringWithFormat:@"%d/message/value",i];
        [mobileObject setValue:@"message.value" value:value];
        [database update:mobileObject];
    } 
    
    NSFetchedResultsController *cursor = [database readByName:@"myChannel" :@"to" :NO];
    if(cursor != nil)
    {
        NSArray *fetchedObjects = [cursor fetchedObjects];
        int count = [fetchedObjects count];
        NSLog(@"# of MetaData objects: %d",count);
        STAssertTrue(count==5,nil);
        for(int i=0; i<count; i++)
        {
            MetaData *metadata = [fetchedObjects objectAtIndex:i];
            PersistentMobileObject *local = metadata.parent;
            
            NSLog(@"OID: %@",local.oid);
            NSLog(@"NameValuePairs: %@",local.nameValuePairs);
        }
    }
}

-(void) testReadProxyCursor
{
    MobileObjectDatabase *database = [[MobileObjectDatabase alloc] init];
	[database autorelease];
    
    [database deleteAll:@"myChannel"];
    [database deleteAll:@"anotherChannel"];
    
    //Create and store mobileobjects
    for(int i=0; i<5; i++)
    {
        MobileObject *mobileObject = [MobileObject withInit];
        mobileObject.service = @"myChannel";
        
        if(i==1 || i==3)
        {
            NSString *fromName = @"from";
            NSString *fromValue = [NSString stringWithFormat:@"%d/from/value",i];
            [mobileObject setValue:fromName value:fromValue];
        
            NSString *toName = @"to";
            NSString *toValue = [NSString stringWithFormat:@"%d/to/value",i];
            [mobileObject setValue:toName value:toValue];
        }
        else
        {
            mobileObject.proxy = YES;
        }
        
        
        NSString *oid = [database create:mobileObject];
        NSLog(@"OID (Generated): %@",oid);
        STAssertTrue(oid != nil,nil);
    } 
    
    for(int i=0; i<5; i++)
    {
        MobileObject *mobileObject = [MobileObject withInit];
        mobileObject.service = @"anotherChannel";
        
        NSString *fromName = @"from";
        NSString *fromValue = [NSString stringWithFormat:@"%d/from/value",i];
        [mobileObject setValue:fromName value:fromValue];
        
        NSString *toName = @"to";
        NSString *toValue = [NSString stringWithFormat:@"%d/to/value",i];
        [mobileObject setValue:toName value:toValue];
        
        
        NSString *oid = [database create:mobileObject];
        NSLog(@"OID (Generated): %@",oid);
        STAssertTrue(oid != nil,nil);
    } 
    
    NSFetchedResultsController *cursor = [database readProxyCursor:@"myChannel"];
    if(cursor != nil)
    {
        NSArray *fetchedObjects = [cursor fetchedObjects];
        int count = [fetchedObjects count];
        NSLog(@"# of PersistentObjects: %d",count);
        STAssertTrue(count==3,nil);
    }
}

-(void) testReadByName
{
    MobileObjectDatabase *database = [[MobileObjectDatabase alloc] init];
	[database autorelease];
    
    [database deleteAll:@"myChannel"];
    [database deleteAll:@"anotherChannel"];
    
    //Create and store mobileobjects
    for(int i=0; i<5; i++)
    {
        MobileObject *mobileObject = [MobileObject withInit];
        mobileObject.service = @"myChannel";
        
        if(i==1 || i==3)
        {
            NSString *fromName = @"from";
            NSString *fromValue = [NSString stringWithFormat:@"%d/from/value",i];
            [mobileObject setValue:fromName value:fromValue];
        }
        else
        {
            NSString *fromName = @"from";
            NSString *fromValue = [NSString stringWithFormat:@"%d/from/value",i];
            [mobileObject setValue:fromName value:fromValue];
            
            NSString *toName = @"to";
            NSString *toValue = [NSString stringWithFormat:@"%d/to/value",i];
            [mobileObject setValue:toName value:toValue];
        }
        
        
        NSString *oid = [database create:mobileObject];
        NSLog(@"OID (Generated): %@",oid);
        STAssertTrue(oid != nil,nil);
    } 
    
    for(int i=0; i<5; i++)
    {
        MobileObject *mobileObject = [MobileObject withInit];
        mobileObject.service = @"anotherChannel";
        
        NSString *fromName = @"from";
        NSString *fromValue = [NSString stringWithFormat:@"%d/from/value",i];
        [mobileObject setValue:fromName value:fromValue];
        
        NSString *toName = @"to";
        NSString *toValue = [NSString stringWithFormat:@"%d/to/value",i];
        [mobileObject setValue:toName value:toValue];
        
        
        NSString *oid = [database create:mobileObject];
        NSLog(@"OID (Generated): %@",oid);
        STAssertTrue(oid != nil,nil);
    } 
    
    NSFetchedResultsController *cursor = [database readByName:@"myChannel" :@"to"];
    if(cursor != nil)
    {
        NSArray *fetchedObjects = [cursor fetchedObjects];
        int count = [fetchedObjects count];
        NSLog(@"# of MetaData: %d",count);
        STAssertTrue(count==3,nil);
        
        for(int i=0; i<count; i++)
        {
            MetaData *metadata = [fetchedObjects objectAtIndex:i];
            PersistentMobileObject *local = metadata.parent;
            
            NSLog(@"OID: %@",local.oid);
            NSLog(@"NameValuePairs: %@",local.nameValuePairs);
        }
    }
}

-(void) testReadByNameValuePair
{
    MobileObjectDatabase *database = [[MobileObjectDatabase alloc] init];
	[database autorelease];
    
    [database deleteAll:@"myChannel"];
    [database deleteAll:@"anotherChannel"];
    
    //Create and store mobileobjects
    for(int i=0; i<5; i++)
    {
        MobileObject *mobileObject = [MobileObject withInit];
        mobileObject.service = @"myChannel";
        
        NSString *fromName = @"from";
        NSString *fromValue = [NSString stringWithFormat:@"%d/from/value",i];
        [mobileObject setValue:fromName value:fromValue];
            
        NSString *toName = @"to";
        NSString *toValue = [NSString stringWithFormat:@"%d/to/value",i];
        [mobileObject setValue:toName value:toValue];
        
        
        NSString *oid = [database create:mobileObject];
        NSLog(@"OID (Generated): %@",oid);
        STAssertTrue(oid != nil,nil);
    } 
    
    for(int i=0; i<5; i++)
    {
        MobileObject *mobileObject = [MobileObject withInit];
        mobileObject.service = @"anotherChannel";
        
        NSString *fromName = @"from";
        NSString *fromValue = [NSString stringWithFormat:@"%d/from/value",i];
        [mobileObject setValue:fromName value:fromValue];
        
        NSString *toName = @"to";
        NSString *toValue = [NSString stringWithFormat:@"%d/to/value",i];
        [mobileObject setValue:toName value:toValue];
        
        
        NSString *oid = [database create:mobileObject];
        NSLog(@"OID (Generated): %@",oid);
        STAssertTrue(oid != nil,nil);
    } 
    
    NSFetchedResultsController *cursor = [database readByNameValuePair:@"myChannel" :@"to" :@"4/to/value"];
    if(cursor != nil)
    {
        NSArray *fetchedObjects = [cursor fetchedObjects];
        int count = [fetchedObjects count];
        NSLog(@"# of MetaData: %d",count);
        STAssertTrue(count==1,nil);
        
        for(int i=0; i<count; i++)
        {
            MetaData *metadata = [fetchedObjects objectAtIndex:i];
            PersistentMobileObject *local = metadata.parent;
            
            NSLog(@"OID: %@",local.oid);
            NSLog(@"NameValuePairs: %@",local.nameValuePairs);
        }
    }
}
//--------------------------------------------------------------------------------------------------------------------
-(void) print:(MobileObject *)mobileObject
{
	NSArray *fields = mobileObject.fields;
	if(fields != nil)
	{
		for(Field *local in fields)
		{
			NSLog(@"Uri: %@",local.uri);
			NSLog(@"Name: %@",local.name);
			NSLog(@"Value: %@",local.value);
			NSLog(@"----------------------------------");
		}
	}
	else 
	{
		NSLog(@"Fields Not Found!!!");
	}
    
	NSArray *arrayMetaData = mobileObject.arrayMetaData;
	if(arrayMetaData != nil)
	{
		for(ArrayMetaData *local in arrayMetaData)
		{
			NSLog(@"ArrayUri: %@",local.arrayUri);
			NSLog(@"ArrayLength: %@",local.arrayLength);
			NSLog(@"ArrayClass: %@",local.arrayClass);
			NSLog(@"----------------------------------");
		}
	}
	else 
	{
		NSLog(@"Fields Not Found!!!");
	}
}
@end
