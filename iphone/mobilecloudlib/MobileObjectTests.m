#import "MobileObjectTests.h"
#import "Field.h"
#import "ArrayMetaData.h"
#import "MobileObject.h"
#import "DeviceSerializer.h"
#import "PersistentMobileObject.h"
#import "Field.h"
#import "ArrayMetaData.h"
#import "MobileObjectDatabase.h"
#import "LogicExpression.h"
#import "LogicChain.h"


@implementation MobileObjectTests

- (void) testFieldEquals 
{
	NSLog(@"Starting testFieldEquals............");
	
	Field *field1 = [Field withInit:@"/test" name:@"test1" value:@"value1"];
	Field *field2 = [Field withInit:@"/test2" name:@"test2" value:@"value2"];
	Field *field3 = [Field withInit:@"/test" name:@"test11" value:@"value11"];
	Field *field4 = [Field withInit:@"/test" name:@"test1" value:@"value1"];
	
	STAssertFalse([field1 isEqual:field3], nil);
	STAssertFalse([field1 isEqual:field2], nil);
	STAssertFalse([field2 isEqual:field3], nil);
	STAssertTrue([field1 isEqual:field4], nil);
}

-(void) testSimpleSerialization
{
	NSLog(@"Starting testSimpleSerialization");
	
	MobileObject *mobileObject = [self createPOJOWithStrings:@"top-level"];
	
	//Assert mobileObject state
	NSString *value = [mobileObject getValue:@"value"];
	NSLog(@"Value: %@",value);
	
	STAssertTrue([value isEqualToString:@"top-level"],nil);
}

-(void) testNestedSerialization
{
	NSLog(@"Starting testNestedSerialization");
	
	MobileObject *mobileObject = [self createNestedPOJO];
	
	//Assert mobileObject state
	NSString *root = [mobileObject getValue:@"root"];
	NSLog(@"Root: %@",root);
	STAssertTrue([root isEqualToString:@"root"],nil);
	
	NSString *child1 = [mobileObject getValue:@"root.child1"];
	NSLog(@"Child1: %@",child1);
	STAssertTrue([child1 isEqualToString:@"child1"],nil);
}


-(void) testCalculateArrayIndex
{
	NSLog(@"Starting testCalculateArrayIndex...");
	
	MobileObject *mo = [MobileObject withInit];
	NSString *uri1 = @"/blah[1]";
	NSString *uri2 = @"/blah[0]/blah2[3]";
	
	int arrayIndex1 = [mo calculateArrayIndex:uri1];
	int arrayIndex2 = [mo calculateArrayIndex:uri2];
	
	NSLog(@"ArrayIndex1: %d",arrayIndex1);
	NSLog(@"ArrayIndex2: %d",arrayIndex2);
	STAssertTrue(arrayIndex1==1,nil);
	STAssertTrue(arrayIndex2==3,nil);
}

-(void) testArraySettingAndReading
{
	NSLog(@"Starting testArraySettingAndReading");
	
	int size = 2;
	MobileObject *mobileObject = [self createArrayPOJO:@"arrayPOJO" :size];
	//[self print:mobileObject];
	
	//Assert mobileObject state
	NSString *value = [mobileObject getValue:@"value"];
	NSLog(@"Value: %@",value);
	STAssertTrue([value isEqualToString:@"arrayPOJO"],nil);
	
	//Assert array
	int arrayLength = [mobileObject getArrayLength:@"child.children"];
	STAssertTrue(arrayLength == size, nil);
	for(int i=0; i<arrayLength; i++)
	{
		NSDictionary *arrayElement = [mobileObject getArrayElement:@"child.children" :i];
		
		NSString *childName = (NSString *)[arrayElement objectForKey:@"/childName"];
		NSString *attachmentName = (NSString *)[arrayElement objectForKey:@"/attachment/name"];
		NSString *attachmentSize = (NSString *)[arrayElement objectForKey:@"/attachment/size"];
		NSLog(@"ChildName: %@",childName);
		NSLog(@"AttachmentName: %@",attachmentName);
		NSLog(@"AttachmentSize: %@",attachmentSize);
		NSLog(@"------------------------------------");
		
		NSString *childNameExpected = [NSString stringWithFormat:@"child://%d",i];
		NSString *attachmentNameExpected = [NSString stringWithFormat:@"attachment://%d",i];
		NSString *attachmentSizeExpected = [NSString stringWithFormat:@"%dK",i];
		STAssertTrue([childName isEqualToString:childNameExpected],nil);
		STAssertTrue([attachmentName isEqualToString:attachmentNameExpected],nil);
		STAssertTrue([attachmentSize isEqualToString:attachmentSizeExpected],nil);
	}
}

-(void) testArrayRemove
{
	NSLog(@"Starting testArrayRemove");
	
	int size = 5;
	MobileObject *mobileObject = [self createArrayPOJO:@"arrayPOJO" :size];
	
	//Assert array
	int arrayLength = [mobileObject getArrayLength:@"child.children"];
	STAssertTrue(arrayLength == size, nil);
	for(int i=0; i<arrayLength; i++)
	{
		NSDictionary *arrayElement = [mobileObject getArrayElement:@"child.children" :i];
		
		NSString *childName = (NSString *)[arrayElement objectForKey:@"/childName"];
		NSString *attachmentName = (NSString *)[arrayElement objectForKey:@"/attachment/name"];
		NSString *attachmentSize = (NSString *)[arrayElement objectForKey:@"/attachment/size"];
		NSLog(@"ChildName: %@",childName);
		NSLog(@"AttachmentName: %@",attachmentName);
		NSLog(@"AttachmentSize: %@",attachmentSize);
		NSLog(@"------------------------------------");
		
		NSString *childNameExpected = [NSString stringWithFormat:@"child://%d",i];
		NSString *attachmentNameExpected = [NSString stringWithFormat:@"attachment://%d",i];
		NSString *attachmentSizeExpected = [NSString stringWithFormat:@"%dK",i];
		STAssertTrue([childName isEqualToString:childNameExpected],nil);
		STAssertTrue([attachmentName isEqualToString:attachmentNameExpected],nil);
		STAssertTrue([attachmentSize isEqualToString:attachmentSizeExpected],nil);
	}
	
	//Remove Array Elements from the middle
	[mobileObject removeArrayElement:@"child.children" :1];
	arrayLength = [mobileObject getArrayLength:@"child.children"];
	for(int i=0; i<arrayLength; i++)
	{
		int expected = i;
		NSDictionary *arrayElement = [mobileObject getArrayElement:@"child.children" :i];
		
		NSString *childName = (NSString *)[arrayElement objectForKey:@"/childName"];
		NSString *attachmentName = (NSString *)[arrayElement objectForKey:@"/attachment/name"];
		NSString *attachmentSize = (NSString *)[arrayElement objectForKey:@"/attachment/size"];
		NSLog(@"ChildName: %@",childName);
		NSLog(@"AttachmentName: %@",attachmentName);
		NSLog(@"AttachmentSize: %@",attachmentSize);
		NSLog(@"------------------------------------");
		
		if(i > 0)
		{
			expected++;
		}
		
		NSString *childNameExpected = [NSString stringWithFormat:@"child://%d",expected];
		NSString *attachmentNameExpected = [NSString stringWithFormat:@"attachment://%d",expected];
		NSString *attachmentSizeExpected = [NSString stringWithFormat:@"%dK",expected];
		
		STAssertTrue([childName isEqualToString:childNameExpected],nil);
		STAssertTrue([attachmentName isEqualToString:attachmentNameExpected],nil);
		STAssertTrue([attachmentSize isEqualToString:attachmentSizeExpected],nil);
	}
	
	//Remove from the top
	[mobileObject removeArrayElement:@"child.children" :0];
	arrayLength = [mobileObject getArrayLength:@"child.children"];
	for(int i=0; i<arrayLength; i++)
	{
		int expected = i+2;
		NSDictionary *arrayElement = [mobileObject getArrayElement:@"child.children" :i];
		
		NSString *childName = (NSString *)[arrayElement objectForKey:@"/childName"];
		NSString *attachmentName = (NSString *)[arrayElement objectForKey:@"/attachment/name"];
		NSString *attachmentSize = (NSString *)[arrayElement objectForKey:@"/attachment/size"];
		NSLog(@"ChildName: %@",childName);
		NSLog(@"AttachmentName: %@",attachmentName);
		NSLog(@"AttachmentSize: %@",attachmentSize);
		NSLog(@"------------------------------------");
		
		
		NSString *childNameExpected = [NSString stringWithFormat:@"child://%d",expected];
		NSString *attachmentNameExpected = [NSString stringWithFormat:@"attachment://%d",expected];
		NSString *attachmentSizeExpected = [NSString stringWithFormat:@"%dK",expected];
		
		STAssertTrue([childName isEqualToString:childNameExpected],nil);
		STAssertTrue([attachmentName isEqualToString:attachmentNameExpected],nil);
		STAssertTrue([attachmentSize isEqualToString:attachmentSizeExpected],nil);
	}
	
	//Remove from the bottom
	[mobileObject removeArrayElement:@"child.children" :arrayLength-1];
	arrayLength = [mobileObject getArrayLength:@"child.children"];
	for(int i=0; i<arrayLength; i++)
	{
		int expected = i+2;
		NSDictionary *arrayElement = [mobileObject getArrayElement:@"child.children" :i];
		
		NSString *childName = (NSString *)[arrayElement objectForKey:@"/childName"];
		NSString *attachmentName = (NSString *)[arrayElement objectForKey:@"/attachment/name"];
		NSString *attachmentSize = (NSString *)[arrayElement objectForKey:@"/attachment/size"];
		NSLog(@"ChildName: %@",childName);
		NSLog(@"AttachmentName: %@",attachmentName);
		NSLog(@"AttachmentSize: %@",attachmentSize);
		NSLog(@"------------------------------------");
		
		NSString *childNameExpected = [NSString stringWithFormat:@"child://%d",expected];
		NSString *attachmentNameExpected = [NSString stringWithFormat:@"attachment://%d",expected];
		NSString *attachmentSizeExpected = [NSString stringWithFormat:@"%dK",expected];
		
		STAssertTrue([childName isEqualToString:childNameExpected],nil);
		STAssertTrue([attachmentName isEqualToString:attachmentNameExpected],nil);
		STAssertTrue([attachmentSize isEqualToString:attachmentSizeExpected],nil);
	}
}

-(void) testClearArray
{
	NSLog(@"Starting testClearArray");
	
	int size = 5;
	MobileObject *mobileObject = [self createArrayPOJO:@"arrayPOJO" :size];
	
	//Assert array
	int arrayLength = [mobileObject getArrayLength:@"child.children"];
	STAssertTrue(arrayLength == size, nil);
	for(int i=0; i<arrayLength; i++)
	{
		NSDictionary *arrayElement = [mobileObject getArrayElement:@"child.children" :i];
		
		NSString *childName = (NSString *)[arrayElement objectForKey:@"/childName"];
		NSString *attachmentName = (NSString *)[arrayElement objectForKey:@"/attachment/name"];
		NSString *attachmentSize = (NSString *)[arrayElement objectForKey:@"/attachment/size"];
		NSLog(@"ChildName: %@",childName);
		NSLog(@"AttachmentName: %@",attachmentName);
		NSLog(@"AttachmentSize: %@",attachmentSize);
		NSLog(@"------------------------------------");
		
		NSString *childNameExpected = [NSString stringWithFormat:@"child://%d",i];
		NSString *attachmentNameExpected = [NSString stringWithFormat:@"attachment://%d",i];
		NSString *attachmentSizeExpected = [NSString stringWithFormat:@"%dK",i];
		STAssertTrue([childName isEqualToString:childNameExpected],nil);
		STAssertTrue([attachmentName isEqualToString:attachmentNameExpected],nil);
		STAssertTrue([attachmentSize isEqualToString:attachmentSizeExpected],nil);
	}
	
	[mobileObject clearArray:@"child.children"];
	[self print:mobileObject];
	arrayLength = [mobileObject getArrayLength:@"child.children"];
	NSLog(@"ArrayLength: %d",arrayLength);
	STAssertTrue(arrayLength == 0,nil);
}

-(void) testSerialization
{
	NSLog(@"Starting testSerialization......");
	int size = 2;
	MobileObject *mobileObject = [self createArrayPOJO:@"arrayPOJO" :size];
	DeviceSerializer *serializer = [DeviceSerializer getInstance];
	
	NSString *xml = [serializer serialize:mobileObject];
	NSLog(@"%@",xml);
	
	MobileObject *deserialized = [serializer deserialize:xml];
	[self print:deserialized];
	[self print:mobileObject];
}
-(void) testMobileObjectPersistence
{
	NSLog(@"Starting testMobileObjectPersistence......");
	
	PersistentMobileObject *pm = [PersistentMobileObject newInstance:@"channel"];
	
	for(int i=0; i<5; i++)
	{
		NSString *uri = [NSString stringWithFormat:@"%d/uri",i];
		NSString *name = [NSString stringWithFormat:@"%d/name",i];
		NSString *value = [NSString stringWithFormat:@"%d/value",i];
		Field *field = [Field withInit:uri name:name value:value];
		[pm addField:field];
	}
	
	for(int i=0; i<5; i++)
	{
		NSString *arrayUri = [NSString stringWithFormat:@"%d/arrayUri",i];
		NSString *arrayLength = [NSString stringWithFormat:@"%d/arrayLength",i];
		NSString *arrayClass = [NSString stringWithFormat:@"%d/arrayClass",i];
		ArrayMetaData *arrayMetaData = [ArrayMetaData withInit:arrayUri 
										   arrayLength:arrayLength arrayClass:arrayClass];
		[pm addArrayMetaData:arrayMetaData];
	}
	
	[pm saveInstance];
	
	//Now read it back
	NSArray *mobileObjects = [PersistentMobileObject findByChannel:@"channel"];
	if(mobileObjects != nil)
	{
		for(PersistentMobileObject *local in mobileObjects)
		{
			NSLog(@"----------------------------------------");
			NSLog(@"Service: %@",local.service);
			STAssertTrue([local.service isEqualToString:@"channel"],nil);
			
			NSArray *fields = [local parseFields];
			if(fields != nil)
			{
				int count = [fields count];
				NSLog(@"# of fields: %d",count);
				STAssertTrue(count == 5,nil);
				for(int i=0; i<count; i++)
				{
					Field *localField = (Field *)[fields objectAtIndex:i];
					NSLog(@"Field URI:%@",localField.uri);
					NSLog(@"Field Name:%@",localField.name);
					NSLog(@"Field Value:%@",localField.value);
				}
			}
			
			NSArray *arrayMetaData = [local parseArrayMetaData];
			if(arrayMetaData != nil)
			{
				int count = [arrayMetaData count];
				NSLog(@"# of arrayMetaData: %d",count);
				STAssertTrue(count == 5,nil);
				for(int i=0; i<count; i++)
				{
					ArrayMetaData *localArrayMetaData = (ArrayMetaData *)[arrayMetaData objectAtIndex:i];
					NSLog(@"Array URI:%@",localArrayMetaData.arrayUri);
					NSLog(@"Array Length:%@",localArrayMetaData.arrayLength);
					NSLog(@"Array Class:%@",localArrayMetaData.arrayClass);
				}
			}
		}
	}
	
	//cleanup
	[PersistentMobileObject deleteAll:@"channel"];
}
 
-(void)testMobileObjectDatabase
{
	NSLog(@"Starting testMobileObjectDatabase....");
	
	MobileObjectDatabase *database = [[MobileObjectDatabase alloc] autorelease];
	[database init];
	
	//Create and store mobileobjects
	MobileObject *mobileObject = [MobileObject withInit];
	mobileObject.service = @"myChannel";
	NSString *oid = [database create:mobileObject];
	NSLog(@"OID: %@",oid);
	STAssertTrue(oid != nil,nil);
	
	//Read this mobileObject
	mobileObject = [database read:@"myChannel":oid];
	NSLog(@"OID: %@",mobileObject.recordId);
	STAssertTrue([mobileObject.recordId isEqualToString:oid],nil);
	
	//Update this mobileobject
	NSLog(@"Before Update.................................");
	NSLog(@"ServerRecordId: %@",mobileObject.serverRecordId);
	NSLog(@"IsProxy: %d",(int)mobileObject.proxy);
	NSLog(@"DirtyStatus: %@",mobileObject.dirtyStatus);
	mobileObject.serverRecordId = @"serverRecordId";
	mobileObject.proxy = YES;
	mobileObject.createdOnDevice = YES;
	mobileObject.locked = YES;
	//mobileObject.dirtyStatus = @"true";
	
	for(int i=0; i<5; i++)
	{
		NSString *uri = [NSString stringWithFormat:@"%d/uri",i];
		NSString *name = [NSString stringWithFormat:@"%d/name",i];
		NSString *value = [NSString stringWithFormat:@"%d/value",i];
		Field *field = [Field withInit:uri name:name value:value];
		[mobileObject addField:field];
	}
	
	for(int i=0; i<5; i++)
	{
		NSString *arrayUri = [NSString stringWithFormat:@"%d/arrayUri",i];
		NSString *arrayLength = [NSString stringWithFormat:@"%d/arrayLength",i];
		NSString *arrayClass = [NSString stringWithFormat:@"%d/arrayClass",i];
		ArrayMetaData *arrayMetaData = [ArrayMetaData withInit:arrayUri 
												   arrayLength:arrayLength arrayClass:arrayClass];
		[mobileObject addArrayMetaData:arrayMetaData];
	}
	
	[database update:mobileObject];
	

	mobileObject = [database read:@"myChannel":oid];
	NSLog(@"After Update.................................");
	NSLog(@"ServerRecordId: %@",mobileObject.serverRecordId);
	NSLog(@"IsProxy: %d",(int)mobileObject.proxy);
	NSLog(@"IsCreatedOnDevice: %d",(int)mobileObject.createdOnDevice);
	NSLog(@"IsLocked: %d",(int)mobileObject.locked);
	NSLog(@"DirtyStatus: %@",mobileObject.dirtyStatus);
	NSLog(@"Field Count: %d",[mobileObject.fields count]);
	NSLog(@"ArrayMetaData Count: %d",[mobileObject.arrayMetaData count]);
	
	STAssertTrue([mobileObject.serverRecordId isEqualToString:@"serverRecordId"],nil);
	STAssertTrue(mobileObject.proxy,nil);
	STAssertTrue(mobileObject.createdOnDevice,nil);
	STAssertTrue(mobileObject.locked,nil);
	
	NSArray *fields = [mobileObject fields];
	if(fields != nil)
	{
		int count = [fields count];
		NSLog(@"# of fields: %d",count);
		STAssertTrue(count == 5,nil);
		for(int i=0; i<count; i++)
		{
			Field *localField = (Field *)[fields objectAtIndex:i];
			NSLog(@"Field URI:%@",localField.uri);
			NSLog(@"Field Name:%@",localField.name);
			NSLog(@"Field Value:%@",localField.value);
		}
	}
	
	NSArray *arrayMetaData = [mobileObject arrayMetaData];
	if(arrayMetaData != nil)
	{
		int count = [arrayMetaData count];
		NSLog(@"# of arrayMetaData: %d",count);
		STAssertTrue(count == 5,nil);
		for(int i=0; i<count; i++)
		{
			ArrayMetaData *localArrayMetaData = (ArrayMetaData *)[arrayMetaData objectAtIndex:i];
			NSLog(@"Array URI:%@",localArrayMetaData.arrayUri);
			NSLog(@"Array Length:%@",localArrayMetaData.arrayLength);
			NSLog(@"Array Class:%@",localArrayMetaData.arrayClass);
		}
	}
	
	//delete this object and make sure its gone
	[database delete:mobileObject];
	mobileObject = [database read:@"myChannel":oid];
	STAssertTrue(mobileObject == nil,nil);
	
	//Test cleanup
	[database deleteAll:@"myChannel"];
	NSArray *all = [database readAll:@"myChannel"];
	STAssertTrue(all == nil || [all count] == 0,nil);
}
 
-(void) testQueryByEqualsAND
{
    NSLog(@"Starting testQueryByEqualsAND.........");
    
    MobileObjectDatabase *database = [[MobileObjectDatabase alloc] init];
	[database autorelease];
    
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
    
    //Setup the criteria
    GenericAttributeManager *criteria = [GenericAttributeManager withInit];
    
    //Logic Link
    [criteria setAttribute:@"logicLink" :[NSNumber numberWithInt:AND]];
    
    //Create expressions
    NSMutableArray *expressions = [NSMutableArray array];
    LogicExpression *expression1 = [LogicExpression withInit:@"from" :@"0/from/value" :OP_EQUALS];
    LogicExpression *expression2 = [LogicExpression withInit:@"to" :@"0/to/value" :OP_EQUALS];
    [expressions addObject:expression1];
    [expressions addObject:expression2];
    [criteria setAttribute:@"expressions" :expressions];
    
    //Query
    NSSet *result = [database query:@"myChannel" :criteria];
    
    //Assert
    NSLog(@"%d number of objects found",[result count]);
    STAssertTrue([result count]==1,nil);
    
    for(MobileObject *found in result)
    {
        NSString *from = [found getValue:@"from"];
        NSString *to = [found getValue:@"to"];
        
        NSLog(@"From: %@",from);
        NSLog(@"To: %@",to);
        
        STAssertTrue([from isEqualToString:@"0/from/value"],nil);
        STAssertTrue([to isEqualToString:@"0/to/value"],nil);
    }
}
-(void) testQueryByEqualsOR
{
    NSLog(@"Starting testQueryByOR.........");
    
    MobileObjectDatabase *database = [[MobileObjectDatabase alloc] init];
	[database autorelease];
    
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
    
    //Setup the criteria
    GenericAttributeManager *criteria = [GenericAttributeManager withInit];
    
    //Logic Link
    [criteria setAttribute:@"logicLink" :[NSNumber numberWithInt:OR]];
    
    //Create expressions
    NSMutableArray *expressions = [NSMutableArray array];
    LogicExpression *expression1 = [LogicExpression withInit:@"from" :@"0/from/value" :OP_EQUALS];
    LogicExpression *expression2 = [LogicExpression withInit:@"to" :@"1/to/value" :OP_EQUALS];
    [expressions addObject:expression1];
    [expressions addObject:expression2];
    [criteria setAttribute:@"expressions" :expressions];
    
    //Query
    NSSet *result = [database query:@"myChannel" :criteria];
    
    //Assert
    NSLog(@"%d number of objects found",[result count]);
    STAssertTrue([result count]==2,nil);
    
    for(MobileObject *found in result)
    {
        NSString *from = [found getValue:@"from"];
        NSString *to = [found getValue:@"to"];
        
        NSLog(@"From: %@",from);
        NSLog(@"To: %@",to);
        
        STAssertTrue([from isEqualToString:@"0/from/value"] || [from isEqualToString:@"1/from/value"],nil);
        STAssertTrue([to isEqualToString:@"0/to/value"] || [to isEqualToString:@"1/to/value"],nil);
    }
}

-(void) testQueryByNotEqualsAND
{
    NSLog(@"Starting testQueryByNotEqualsAND.........");
    
    MobileObjectDatabase *database = [[MobileObjectDatabase alloc] init];
	[database autorelease];
    
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
    
    //Setup the criteria
    GenericAttributeManager *criteria = [GenericAttributeManager withInit];
    
    //Logic Link
    [criteria setAttribute:@"logicLink" :[NSNumber numberWithInt:AND]];
    
    //Create expressions
    NSMutableArray *expressions = [NSMutableArray array];
    LogicExpression *expression1 = [LogicExpression withInit:@"from" :@"0/from/value" :OP_NOT_EQUALS];
    LogicExpression *expression2 = [LogicExpression withInit:@"to" :@"1/to/value" :OP_NOT_EQUALS];
    [expressions addObject:expression1];
    [expressions addObject:expression2];
    [criteria setAttribute:@"expressions" :expressions];
    
    //Query
    NSSet *result = [database query:@"myChannel" :criteria];
    
    //Assert
    NSLog(@"%d number of objects found",[result count]);
    STAssertTrue([result count]==3,nil);
    
    for(MobileObject *found in result)
    {
        NSString *from = [found getValue:@"from"];
        NSString *to = [found getValue:@"to"];
        
        NSLog(@"From: %@",from);
        NSLog(@"To: %@",to);
        
        STAssertTrue(![from isEqualToString:@"0/from/value"] && ![from isEqualToString:@"1/from/value"],nil);
        STAssertTrue(![to isEqualToString:@"0/to/value"] && ![to isEqualToString:@"1/to/value"],nil);
    }
}
-(void) testQueryByNotEqualsOR
{
    NSLog(@"Starting testQueryByNotEqualsOR.........");
    
    MobileObjectDatabase *database = [[MobileObjectDatabase alloc] init];
	[database autorelease];
    
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
    
    //Setup the criteria
    GenericAttributeManager *criteria = [GenericAttributeManager withInit];
    
    //Logic Link
    [criteria setAttribute:@"logicLink" :[NSNumber numberWithInt:OR]];
    
    //Create expressions
    NSMutableArray *expressions = [NSMutableArray array];
    LogicExpression *expression1 = [LogicExpression withInit:@"from" :@"0/from/value" :OP_NOT_EQUALS];
    LogicExpression *expression2 = [LogicExpression withInit:@"to" :@"1/to/value" :OP_NOT_EQUALS];
    [expressions addObject:expression1];
    [expressions addObject:expression2];
    [criteria setAttribute:@"expressions" :expressions];
    
    //Query
    NSSet *result = [database query:@"myChannel" :criteria];
    
    //Assert
    NSLog(@"%d number of objects found",[result count]);
    STAssertTrue([result count]==5,nil);
}
-(void) testQueryByContainsAND
{
    NSLog(@"Starting testQueryByContainsAND.........");
    
    MobileObjectDatabase *database = [[MobileObjectDatabase alloc] init];
	[database autorelease];
    
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
    
    //Setup the criteria
    GenericAttributeManager *criteria = [GenericAttributeManager withInit];
    
    //Logic Link
    [criteria setAttribute:@"logicLink" :[NSNumber numberWithInt:AND]];
    
    //Create expressions
    NSMutableArray *expressions = [NSMutableArray array];
    LogicExpression *expression1 = [LogicExpression withInit:@"from" :@"from/value" :OP_CONTAINS];
    LogicExpression *expression2 = [LogicExpression withInit:@"to" :@"to/value" :OP_CONTAINS];
    [expressions addObject:expression1];
    [expressions addObject:expression2];
    [criteria setAttribute:@"expressions" :expressions];
    
    //Query
    NSSet *result = [database query:@"myChannel" :criteria];
    
    //Assert
    NSLog(@"%d number of objects found",[result count]);
    STAssertTrue([result count]==5,nil);
}

-(void) testQueryByContainsOR
{
    NSLog(@"Starting testQueryByContainsOR.........");
    
    MobileObjectDatabase *database = [[MobileObjectDatabase alloc] init];
	[database autorelease];
    
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
    
    //Setup the criteria
    GenericAttributeManager *criteria = [GenericAttributeManager withInit];
    
    //Logic Link
    [criteria setAttribute:@"logicLink" :[NSNumber numberWithInt:OR]];
    
    //Create expressions
    NSMutableArray *expressions = [NSMutableArray array];
    LogicExpression *expression1 = [LogicExpression withInit:@"from" :@"from/value" :OP_CONTAINS];
    LogicExpression *expression2 = [LogicExpression withInit:@"to" :@"blahblah" :OP_CONTAINS];
    [expressions addObject:expression1];
    [expressions addObject:expression2];
    [criteria setAttribute:@"expressions" :expressions];
    
    //Query
    NSSet *result = [database query:@"myChannel" :criteria];
    
    //Assert
    NSLog(@"%d number of objects found",[result count]);
    STAssertTrue([result count]==5,nil);
}

-(void) testNSPredicateBasedFetching
{
	NSLog(@"Starting NSPredicatedBasedFetching......");
    
    MobileObjectDatabase *database = [[MobileObjectDatabase alloc] init];
	[database autorelease];
    
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
    
    //Get the Storage Context
    NSManagedObjectContext *managedContext = [[CloudDBManager getInstance] storageContext];
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"PersistentMobileObject" 
                                              inManagedObjectContext:managedContext];
    
    //Get an instance if its already been provisioned
    NSFetchRequest *request = [[[NSFetchRequest alloc] init] autorelease];
    [request setEntity:entity];
    
    //Filter using a predicate
    //NSPredicate *predicate = [NSPredicate predicateWithFormat:@"service == %@", @"channel"];
    //[request setPredicate:predicate];
    
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"(service == %@) AND (nameValuePairs contains %@) AND (nameValuePairs contains %@)",@"myChannel", @"name=from",@"value=0/from/value"];
    [request setPredicate:predicate];
    
    NSArray *all = [managedContext executeFetchRequest:request error:NULL];
    
    //filter by predicate
    if(all != nil)
    {
        NSLog(@"*******************************");
        for(PersistentMobileObject *local in all)
        {
            NSLog(@"OID: %@",local.oid);
            NSLog(@"NameValuePairs: %@",local.nameValuePairs);
        }
        NSLog(@"*******************************");
    }
}
//-----------------------------------------------------------------------------------------
-(MobileObject *) createPOJOWithStrings:(NSString *)value
{
	MobileObject *mobileObject = [MobileObject withInit];
	mobileObject.recordId = @"recordId";
	mobileObject.serverRecordId = @"serverRecordId";
	
	[mobileObject setValue:@"value" value:value];
	
	return mobileObject;
}

-(MobileObject *) createNestedPOJO
{
	MobileObject *mobileObject = [MobileObject withInit];
	mobileObject.recordId = @"recordId";
	mobileObject.serverRecordId = @"serverRecordId";
	
	[mobileObject setValue:@"root" value:@"root"];
	
	[mobileObject setValue:@"root.child1" value:@"child1"];
	
	return mobileObject;
}
-(MobileObject *) createArrayPOJO:(NSString *)value :(int)size
{
	MobileObject *mobileObject = [self createPOJOWithStrings:value];
	mobileObject.recordId = @"recordId";
	mobileObject.serverRecordId = @"serverRecordId";
	
	for(int i=0; i<size; i++)
	{
		NSMutableDictionary *properties = [NSMutableDictionary dictionary];
		NSString *childName = [NSString stringWithFormat:@"child://%d",i];
		NSString *attachmentName = [NSString stringWithFormat:@"attachment://%d",i];
		NSString *attachmentSize = [NSString stringWithFormat:@"%dK",i];
		
		[properties setObject:childName forKey:@"/childName"];
		[properties setObject:attachmentName forKey:@"/attachment/name"];
		[properties setObject:attachmentSize forKey:@"/attachment/size"];
		
		[mobileObject addToArray:@"child.children" :properties];
	}
	
	return mobileObject;
}

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