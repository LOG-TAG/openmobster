//
//  TestPayloadHandler.m
//  mobilecloudlib
//
//  Created by openmobster on 11/20/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

#import "TestPayloadHandler.h"
#import "PayloadHandler.h"
#import "LocationRequest.h"
#import "LocationContext.h"
#import "Place.h"
#import "Address.h"


@implementation TestPayloadHandler

-(void)testSerializeRequest
{
    NSLog(@"Starting testSerializeRequest......");
    
    LocationRequest *request = [LocationRequest withInit:@"coupons"];
    LocationContext *locationContext = [LocationContext withInit];
    
    [locationContext setRequest:request];
    
    //Populate the request object
    NSMutableArray *list = [NSMutableArray array];
    [list addObject:@"value://0"];
    [list addObject:@"value://1"];
    [list addObject:@"value://2"];
    [request setListAttribute:@"mylist" :list];
    
    //add a map
    NSMutableDictionary *map = [NSMutableDictionary dictionary];
    [map setValue:@"value1" forKey:@"name1"];
    [map setValue:@"value2" forKey:@"name2"];
    [map setValue:@"value3" forKey:@"name3"];
    [request setMapAttribute:@"myMap" :map];
    
    for(int i=0; i<5; i++)
    {
        NSString *name = [NSString stringWithFormat:@"param%d",i];
        NSString *value = [NSString stringWithFormat:@"value%d",i];
        [request setAttribute:name :value];
    }
    
    //setup the locationContext
    [locationContext setLatitude:@"-100"];
    [locationContext setLongitude:@"-200"];
    [locationContext setPlaceReference:@"123456789"];
    
    NSMutableArray *placeTypes = [NSMutableArray array];
    [placeTypes addObject:@"food"];
    [placeTypes addObject:@"restaurant"];
    [placeTypes addObject:@"grocery"];
    [locationContext setPlaceTypes:placeTypes];
    
    for(int i=0; i<5; i++)
    {
        NSString *name = [NSString stringWithFormat:@"param%d",i];
        NSString *value = [NSString stringWithFormat:@"value%d",i];
        [locationContext setAttribute:name :value];
    }

    
    PayloadHandler *payloadHandler = [PayloadHandler withInit];
    NSString *xml = [payloadHandler serializeRequest:locationContext];
    
    NSLog(@"%@",xml);
}

-(void)testDeserializeResponse
{
    NSString *response = @"<location-response><response-payload><![CDATA[{\"list2\":[\"listAttribute:0\",\"listAttribute:1\",\"listAttribute:2\",\"listAttribute:3\",\"listAttribute:4\"],\"param0\":\"value0\",\"param1\":\"value1\",\"param2\":\"value2\",\"status\":\"200\",\"list1\":[\"listAttribute:0\",\"listAttribute:1\",\"listAttribute:2\",\"listAttribute:3\",\"listAttribute:4\"],\"param3\":\"value3\",\"param4\":\"value4\",\"map2\":{\"key4\":\"value4\",\"key3\":\"value3\",\"key0\":\"value0\",\"key2\":\"value2\",\"key1\":\"value1\"},\"map1\":{\"key4\":\"value4\",\"key3\":\"value3\",\"key0\":\"value0\",\"key2\":\"value2\",\"key1\":\"value1\"},\"statusMsg\":\"OK\"}]]></response-payload><location-payload><![CDATA[{\"param0\":\"value0\",\"param1\":\"value1\",\"param2\":\"value2\",\"address\":{\"street\":\"1782 Stillwind Lane\"},\"param3\":\"value3\",\"param4\":\"value4\",\"placeTypes\":[\"restaurant\",\"airport\"],\"longitude\":\"-200\",\"latitude\":\"-100\",\"places\":[{\"phone\":\"867-5309\",\"address\":\"2046 Dogwood Gardens Dr:0\",\"reference\":\"Reference:0\"},{\"phone\":\"867-5309\",\"address\":\"2046 Dogwood Gardens Dr:1\",\"reference\":\"Reference:1\"},{\"phone\":\"867-5309\",\"address\":\"2046 Dogwood Gardens Dr:2\",\"reference\":\"Reference:2\"},{\"phone\":\"867-5309\",\"address\":\"2046 Dogwood Gardens Dr:3\",\"reference\":\"Reference:3\"},{\"phone\":\"867-5309\",\"address\":\"2046 Dogwood Gardens Dr:4\",\"reference\":\"Reference:4\"}]}]]></location-payload></location-response>"; 
    
    PayloadHandler *payloadHandler = [PayloadHandler withInit];
    LocationContext *locationContext = [payloadHandler deserializeResponse:response];
    
    //Nearby Places
    NSArray *places = [locationContext getNearbyPlaces];
    for(Place *place in places)
    {
        NSLog(@"Place Address : %@",place.address);
    }
    
    //Place Types
    NSArray *placeTypes = [locationContext getPlaceTypes];
    for(NSString *placeType in placeTypes)
    {
        NSLog(@"Place Type: %@",placeType);
    }
    
    //Address
    Address *address = [locationContext getAddress];
    NSLog(@"Address Street: %@",address.street);
    
    //rest of the context
    NSString *value = [locationContext getAttribute:@"param0"];
    STAssertTrue([value isEqualToString:@"value0"], nil);
    
    value = [locationContext getAttribute:@"param1"];
    STAssertTrue([value isEqualToString:@"value1"], nil);
    
    value = [locationContext getAttribute:@"param2"];
    STAssertTrue([value isEqualToString:@"value2"], nil);
    
    value = [locationContext getAttribute:@"param3"];
    STAssertTrue([value isEqualToString:@"value3"], nil);
    
    value = [locationContext getAttribute:@"param4"];
    STAssertTrue([value isEqualToString:@"value4"], nil);
    
    //Assert the response object
    LocationResponse *locationResponse = [locationContext getResponse];
    
    NSArray *list1 = [locationResponse getListAttribute:@"list1"];
    NSArray *list2 = [locationResponse getListAttribute:@"list2"];
    NSDictionary *map1 = [locationResponse getMapAttribute:@"map1"];
    NSDictionary *map2 = [locationResponse getMapAttribute:@"map2"];
    
    STAssertTrue(list1 != nil && [list1 count]>0, nil);
    STAssertTrue(list2 != nil && [list2 count]>0, nil);
    STAssertTrue(map1 != nil && [map1 count]>0, nil);
    STAssertTrue(map2 != nil && [map2 count]>0, nil);
}

@end
