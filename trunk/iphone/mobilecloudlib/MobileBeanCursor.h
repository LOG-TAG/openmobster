//
//  MobileBeanCursor.h
//  mobilecloudlib
//
//  Created by openmobster on 9/25/13.
//  Copyright (c) 2013 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>
#import "MobileBean.h"

@interface MobileBeanCursor : NSObject
{
    @private
    NSFetchedResultsController *cursor;
    NSString *channel;
}

@property (nonatomic,retain) NSFetchedResultsController *cursor;
@property (nonatomic,retain) NSString *channel;

+(MobileBeanCursor *)withInit:(NSString *)channel :(NSFetchedResultsController *)cursor;

/**
 *
 */
-(int) count;

/**
 *
 */
-(MobileBean *) beanAtIndex:(int)index;


/**
 *
 */
-(NSArray *) all;


/**
 * Query the channel such the results are sorted by the value of the specified property of the bean
 * 
 * @param channel
 * @param property
 * @param ascending
 * @return
 */
+(MobileBeanCursor *) sortByProperty:(NSString *)channel :(NSString *)property :(BOOL) ascending;

/**
 * Query the channel by the value of the specified bean property
 * 
 * @param channel
 * @param property
 * @param value
 * @return
 */
+(MobileBeanCursor *) queryByProperty:(NSString *)channel :(NSString *)property :(NSString *)value;

/**
 * Search beans by criteria made up of name/value pairs to be matched against
 * The query uses the 'AND' expression between each name/value pair to make sure the criteria is fully matched
 * 
 * @param channel
 * @param criteria
 * @return
 */
+(MobileBeanCursor *) searchByMatchAll:(NSString *)channel :(GenericAttributeManager *)criteria;

/**
 * Search beans by criteria made up of name/value pairs to be matched against
 * The query uses the 'OR' expression between each name/value pair to make sure atleast one name/value pair from the criteria is matched
 * 
 * @param channel
 * @param criteria
 * @return
 */
+(MobileBeanCursor *) searchByMatchAtleastOne:(NSString *)channel :(GenericAttributeManager *)criteria;
@end