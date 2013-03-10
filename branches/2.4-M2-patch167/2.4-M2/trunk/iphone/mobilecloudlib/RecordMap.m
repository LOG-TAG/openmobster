#import "RecordMap.h"


@implementation RecordMap

@synthesize cmdId;
@synthesize source;
@synthesize target;
@synthesize meta;
@synthesize mapItems;

+(id) withInit
{
	RecordMap *recordMap = [[[RecordMap alloc] init] autorelease];
	
	if(recordMap.mapItems == nil)
	{
		recordMap.mapItems = [NSMutableArray array];
	}
	
	return recordMap;
}

-(void) addMapItem:(MapItem *) mapItem
{
	[self.mapItems addObject:mapItem];
}

@end
