#import "AbstractOperation.h"


@implementation AbstractOperation

@synthesize cmdId;
@synthesize meta;
@synthesize items;

-(id) init
{
	if(self = [super init])
	{
		self.items = [NSMutableArray array];
	}
	
	return self;
}

-(void) addItem:(Item *) item
{
	[self.items addObject:item];
}

-(BOOL) isChunked
{
	if(self.items == nil)
	{
		return NO;
	}
	
	for(Item *item in items)
	{
		if(item.moreData)
		{
			return YES;
		}
	}
	
	return NO;
}

-(int) totalSize
{
    int totalSize = 0;
    
    for(Item *item in self.items)
    {
        NSString *data = item.data;
        if(data != nil)
        {
            totalSize += [data length];
        }
    }
    
    return totalSize;
}
@end
