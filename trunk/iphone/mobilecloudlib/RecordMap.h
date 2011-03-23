#import <Foundation/Foundation.h>
#import "MapItem.h"


@interface RecordMap : NSObject 
{
	@private
	NSString *cmdId; //required
	NSString *source; //required
	NSString *target; //required
	NSString *meta; //not-required
	NSMutableArray *mapItems; //one-to-many..'MapItem' instances
}

+(id) withInit;

@property (assign) NSString *cmdId;
@property (assign) NSString *source;
@property (assign) NSString *target;
@property (assign) NSString *meta;
@property (assign) NSMutableArray *mapItems;

@end
