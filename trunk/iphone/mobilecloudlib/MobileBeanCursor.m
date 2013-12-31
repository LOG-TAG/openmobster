//
//  MobileBeanCursor.m
//  mobilecloudlib
//
//  Created by openmobster on 9/25/13.
//  Copyright (c) 2013 __MyCompanyName__. All rights reserved.
//

#import "MobileBeanCursor.h"
#import "MetaData.h"
#import "MobileObjectDatabase.h"

@implementation MobileBeanCursor

@synthesize cursor;
@synthesize channel;

+(MobileBeanCursor *)withInit:(NSString *)channel :(NSFetchedResultsController *)cursor
{
    MobileBeanCursor *mobileBeanCursor = [[MobileBeanCursor alloc] init];
    
    mobileBeanCursor.channel = channel;
    mobileBeanCursor.cursor = cursor;
    
    mobileBeanCursor = [mobileBeanCursor autorelease];
    
    return mobileBeanCursor;
}

-(void) dealloc
{
    [channel release];
    [cursor release];
    [super dealloc];
}

-(int) count
{
    NSArray *fetchedObjects = [self.cursor fetchedObjects];
    return [fetchedObjects count];
}

-(MobileBean *) beanAtIndex:(int)index;
{
    NSArray *fetchedObjects = [self.cursor fetchedObjects];
    NSManagedObject *object = [fetchedObjects objectAtIndex:index];
    
    MobileObject *mobileObject;
    if([object isMemberOfClass:[PersistentMobileObject class]])
    {
        PersistentMobileObject *pmo = (PersistentMobileObject *)object;
        mobileObject = [pmo parseMobileObject];
    }
    else if([object isMemberOfClass:[MetaData class]])
    {
        MetaData *metadata = (MetaData *)object;
        PersistentMobileObject *pmo = (PersistentMobileObject *)metadata.parent;
        mobileObject = [pmo parseMobileObject];
    }
    else
    {
        //This should not happen....something went wrong
        return nil;
    }
    
    MobileBean *mobileBean = [MobileBean withInit:mobileObject];
    
    return mobileBean;
}

-(NSArray *) all
{
    NSMutableArray *beans = [NSMutableArray array];
    
    NSArray *fetchedObjects = [self.cursor fetchedObjects];
    int count = [fetchedObjects count];
    
    for(int i=0; i<count; i++)
    {
        MobileBean *bean = [self beanAtIndex:i];
        if(bean != nil)
        {
            [beans addObject:bean];
        }
    }
    
    return [NSArray arrayWithArray:beans];
}

+(MobileBeanCursor *) sortByProperty:(NSString *)channel :(NSString *)property :(BOOL) ascending
{
    MobileObjectDatabase *mdb = [MobileObjectDatabase getInstance];
    
    NSFetchedResultsController *systemCursor = [mdb readByName:channel :property :ascending];
    
    MobileBeanCursor *cursor = [MobileBeanCursor withInit:channel :systemCursor];
    
    return cursor;
}

+(MobileBeanCursor *) queryByProperty:(NSString *)channel :(NSString *)property :(NSString *)value
{
    MobileObjectDatabase *mdb = [MobileObjectDatabase getInstance];
    
    NSFetchedResultsController *systemCursor = [mdb readByNameValuePair:channel :property :value];
    
    MobileBeanCursor *cursor = [MobileBeanCursor withInit:channel :systemCursor];
    
    return cursor;
}

+(MobileBeanCursor *) searchByMatchAll:(NSString *)channel :(GenericAttributeManager *)criteria
{
    MobileObjectDatabase *mdb = [MobileObjectDatabase getInstance];
    
    NSFetchedResultsController *systemCursor = [mdb searchExactMatchAND:channel :criteria];
    
    MobileBeanCursor *cursor = [MobileBeanCursor withInit:channel :systemCursor];
    
    return cursor;
}

+(MobileBeanCursor *) searchByMatchAtleastOne:(NSString *)channel :(GenericAttributeManager *)criteria
{
    MobileObjectDatabase *mdb = [MobileObjectDatabase getInstance];
    
    NSFetchedResultsController *systemCursor = [mdb searchExactMatchOR:channel :criteria];
    
    MobileBeanCursor *cursor = [MobileBeanCursor withInit:channel :systemCursor];
    
    return cursor;
}
@end