//
//  MobileBeanTest.m
//  mobilecloudlib
//
//  Created by openmobster on 8/14/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import "MobileBeanTest.h"
#import "MobileObjectDatabase.h"
#import "MobileBean.h"
#import "CloudService.h"
#import "MobileBeanCursor.h"


@implementation MobileBeanTest

-(void) testQueryByEqualsAND
{
    NSLog(@"Starting testQueryByEqualsAND.........");
    
    CloudService *cloud = [CloudService getInstance];
    [cloud startup];
    
    [self seedData];
    
    //setup the criteria
    GenericAttributeManager *criteria = [GenericAttributeManager withInit];
    [criteria setAttribute:@"from" :@"0/from/value"];
    [criteria setAttribute:@"to" :@"0/to/value"];
    
    //Find the beans
    NSArray * beans = [MobileBean queryByEqualsAll:@"myChannel" :criteria];
    
    //Assert
    NSLog(@"Number of Beans Found: %d",[beans count]);
    STAssertTrue([beans count]==1,nil);
    
    for(MobileBean *found in beans)
    {
        NSString *from = [found getValue:@"from"];
        NSString *to = [found getValue:@"to"];
        
        NSLog(@"From: %@",from);
        NSLog(@"To: %@",to);
        
        STAssertTrue([from isEqualToString:@"0/from/value"],nil);
        STAssertTrue([to isEqualToString:@"0/to/value"],nil);
    }
}

-(void) testQueryByEqualsAtleastOne
{
    NSLog(@"Starting testQueryByEqualsAtleastOne.........");
    
    CloudService *cloud = [CloudService getInstance];
    [cloud startup];
    
    [self seedData];
    
    //setup the criteria
    GenericAttributeManager *criteria = [GenericAttributeManager withInit];
    [criteria setAttribute:@"from" :@"0/from/value"];
    [criteria setAttribute:@"to" :@"1/to/value"];
    
    //Find the beans
    NSArray * beans = [MobileBean queryByEqualsAtleastOne :@"myChannel" :criteria];
    
    //Assert
    NSLog(@"Number of Beans Found: %d",[beans count]);
    STAssertTrue([beans count]==2,nil);
    
    for(MobileBean *found in beans)
    {
        NSString *from = [found getValue:@"from"];
        NSString *to = [found getValue:@"to"];
        
        NSLog(@"From: %@",from);
        NSLog(@"To: %@",to);
        
        STAssertTrue([from isEqualToString:@"0/from/value"] || [from isEqualToString:@"1/from/value"],nil);
        STAssertTrue([to isEqualToString:@"0/to/value"] || [to isEqualToString:@"1/to/value"],nil);
    }
}

-(void) testQueryByNotEqualsAll
{
    NSLog(@"Starting testQueryByNotEqualsAll.........");
    
    CloudService *cloud = [CloudService getInstance];
    [cloud startup];
    
    [self seedData];
    
    //setup the criteria
    GenericAttributeManager *criteria = [GenericAttributeManager withInit];
    [criteria setAttribute:@"from" :@"0/from/value"];
    [criteria setAttribute:@"to" :@"1/to/value"];
    
    //Find the beans
    NSArray * beans = [MobileBean queryByNotEqualsAll :@"myChannel" :criteria];
    
    //Assert
    NSLog(@"%d number of objects found",[beans count]);
    STAssertTrue([beans count]==3,nil);
    
    for(MobileObject *found in beans)
    {
        NSString *from = [found getValue:@"from"];
        NSString *to = [found getValue:@"to"];
        
        NSLog(@"From: %@",from);
        NSLog(@"To: %@",to);
        
        STAssertTrue(![from isEqualToString:@"0/from/value"] && ![from isEqualToString:@"1/from/value"],nil);
        STAssertTrue(![to isEqualToString:@"0/to/value"] && ![to isEqualToString:@"1/to/value"],nil);
    }

}

-(void) testQueryByNotEqualsAtleastOne
{
    NSLog(@"Starting testQueryByNotEqualsAtleastOne.........");
    
    CloudService *cloud = [CloudService getInstance];
    [cloud startup];
    
    [self seedData];
    
    //setup the criteria
    GenericAttributeManager *criteria = [GenericAttributeManager withInit];
    [criteria setAttribute:@"from" :@"0/from/value"];
    [criteria setAttribute:@"to" :@"1/to/value"];
    
    //Find the beans
    NSArray * beans = [MobileBean queryByNotEqualsAtleastOne :@"myChannel" :criteria];
    
    //Assert
    NSLog(@"%d number of objects found",[beans count]);
    STAssertTrue([beans count]==5,nil);    
}

-(void) testQueryContainsAll
{
    NSLog(@"Starting testQueryByContainsAll.........");
    
    CloudService *cloud = [CloudService getInstance];
    [cloud startup];
    
    [self seedData];
    
    //setup the criteria
    GenericAttributeManager *criteria = [GenericAttributeManager withInit];
    [criteria setAttribute:@"from" :@"from/value"];
    [criteria setAttribute:@"to" :@"to/value"];
    
    //Find the beans
    NSArray * beans = [MobileBean queryByContainsAll:@"myChannel" :criteria];
    
    //Assert
    NSLog(@"%d number of objects found",[beans count]);
    STAssertTrue([beans count]==5,nil);
}

-(void) testQueryContainsAtleastOne
{
    NSLog(@"Starting testQueryByContainsAtleastOne.........");
    
    CloudService *cloud = [CloudService getInstance];
    [cloud startup];
    
    [self seedData];
    
    //setup the criteria
    GenericAttributeManager *criteria = [GenericAttributeManager withInit];
    [criteria setAttribute:@"from" :@"from/value"];
    [criteria setAttribute:@"to" :@"blahblah"];
    
    //Find the beans
    NSArray * beans = [MobileBean queryByContainsAtleastOne:@"myChannel" :criteria];
    
    //Assert
    NSLog(@"%d number of objects found",[beans count]);
    STAssertTrue([beans count]==5,nil);
}

-(void) testLargeObjects
{
    NSLog(@"Starting testLargeObjects.........");
    
    CloudService *cloud = [CloudService getInstance];
    [cloud startup];
    
    MobileObjectDatabase *database = [MobileObjectDatabase getInstance];
    [database deleteAll:@"myChannel"];
    
    MobileBean *newBean = [MobileBean newInstance:@"myChannel"];
    
    NSMutableString *newAttachment = [NSMutableString string];
    for(int i=0; i<1024; i++)
    {
        for(int j=0; j<10000; j++)
        {
            [newAttachment appendString:@"a"];
        }
    }
    
    NSData *newAttachmentBin = [newAttachment dataUsingEncoding:NSUTF8StringEncoding];
    [newBean setBinaryValue:@"attachment" :newAttachmentBin];
    
    [newBean save];
    
    NSString *oid = [newBean getId];
    NSLog(@"OID: %@",oid);
    
    MobileBean *savedBean = [MobileBean readById:@"myChannel" :oid];
    NSData *attachmentData = [savedBean getBinaryValue:@"attachment"];
}

-(void) testSortByProperty
{
    NSLog(@"Starting testSortByProperty.........");
    
    CloudService *cloud = [CloudService getInstance];
    [cloud startup];
    
    [self seedData];
    
    MobileBeanCursor *cursor = [MobileBeanCursor sortByProperty:@"myChannel" :@"to" :NO];
    
    //iterate one bean at a time
    int count = [cursor count];
    STAssertTrue(count==5, nil);
    for(int i=0; i<count; i++)
    {
        MobileBean *bean = [cursor beanAtIndex:i];
        NSLog(@"To: %@",[bean getValue:@"to"]);
    }
    
    //iterate all
    NSArray *allBeans = [cursor all];
    for(MobileBean *bean in allBeans)
    {
        NSLog(@"From: %@",[bean getValue:@"from"]);
    }
}

-(void) testQueryByProperty
{
    NSLog(@"Starting testQueryByProperty.........");
    
    CloudService *cloud = [CloudService getInstance];
    [cloud startup];
    
    [self seedData];
    
    MobileBeanCursor *cursor = [MobileBeanCursor queryByProperty:@"myChannel" :@"to" :@"3/to/value"];
    
    //iterate one bean at a time
    int count = [cursor count];
    STAssertTrue(count==1, nil);
    for(int i=0; i<count; i++)
    {
        MobileBean *bean = [cursor beanAtIndex:i];
        NSLog(@"To: %@",[bean getValue:@"to"]);
    }
    
    //iterate all
    NSArray *allBeans = [cursor all];
    for(MobileBean *bean in allBeans)
    {
        NSLog(@"From: %@",[bean getValue:@"from"]);
    }
}

-(void) testSearchByMatchAll
{
    NSLog(@"Starting testSearchByMatchAll.........");
    
    CloudService *cloud = [CloudService getInstance];
    [cloud startup];
    
    [self seedData];
    
    GenericAttributeManager *criteria = [[GenericAttributeManager alloc] initWithRetention];
    criteria = [criteria autorelease];
    [criteria setAttribute:@"to" :@"0/to/value"];
    [criteria setAttribute:@"from" :@"0/from/value"];
    
    MobileBeanCursor *cursor = [MobileBeanCursor searchByMatchAll:@"myChannel" :criteria];
    
    //iterate one bean at a time
    int count = [cursor count];
    STAssertTrue(count==1, nil);
    for(int i=0; i<count; i++)
    {
        MobileBean *bean = [cursor beanAtIndex:i];
        NSLog(@"To: %@",[bean getValue:@"to"]);
    }
    
    //iterate all
    NSArray *allBeans = [cursor all];
    for(MobileBean *bean in allBeans)
    {
        NSLog(@"From: %@",[bean getValue:@"from"]);
    }
}

-(void) testSearchByMatchAtleastOne
{
    NSLog(@"Starting testSearchByMatchAtleastOne.........");
    
    CloudService *cloud = [CloudService getInstance];
    [cloud startup];
    
    [self seedData];
    
    GenericAttributeManager *criteria = [[GenericAttributeManager alloc] initWithRetention];
    criteria = [criteria autorelease];
    [criteria setAttribute:@"to" :@"0/to/value"];
    [criteria setAttribute:@"from" :@"1/from/value"];
    
    MobileBeanCursor *cursor = [MobileBeanCursor searchByMatchAtleastOne:@"myChannel" :criteria];
    
    //iterate one bean at a time
    int count = [cursor count];
    STAssertTrue(count==2, nil);
    for(int i=0; i<count; i++)
    {
        MobileBean *bean = [cursor beanAtIndex:i];
        NSLog(@"To: %@",[bean getValue:@"to"]);
    }
    
    //iterate all
    NSArray *allBeans = [cursor all];
    for(MobileBean *bean in allBeans)
    {
        NSLog(@"From: %@",[bean getValue:@"from"]);
    }
}
//------------------------------------------------------------------------------------------------
-(void) seedData
{
    MobileObjectDatabase *database = [MobileObjectDatabase getInstance];
    [database deleteAll:@"myChannel"];
	
	//Create and store mobileobjects
    for(int i=0; i<5; i++)
    {
        MobileObject *mobileObject = [MobileObject withInit];
        mobileObject.service = @"myChannel";
        NSString *oid = [database create:mobileObject];
        NSLog(@"OID (Generated): %@",oid);
        STAssertTrue(oid != nil,nil);
        
        //Read this mobileObject
        mobileObject = [database read:@"myChannel":oid];
        NSLog(@"OID (Stored): %@",mobileObject.recordId);
        STAssertTrue([mobileObject.recordId isEqualToString:oid],nil);
        
        NSString *uri = @"/from";
        NSString *name = @"from";
        NSString *value = [NSString stringWithFormat:@"%d/from/value",i];
        Field *field = [Field withInit:uri name:name value:value];
        [mobileObject addField:field];
        
        NSString *to = @"/to";
        NSString *toName = @"to";
        NSString *toValue = [NSString stringWithFormat:@"%d/to/value",i];
        Field *toField = [Field withInit:to name:toName value:toValue];
        [mobileObject addField:toField];
        
        [database update:mobileObject];
    }
    
    NSArray *all = [MobileBean readAll:@"myChannel"];
    STAssertTrue([all count]==5,nil);
}
@end
