#import <Foundation/Foundation.h>
#import "Item.h"


@interface AbstractOperation : NSObject 
{
	@protected
	NSString *cmdId; //required
	NSString *meta; //nullable
	NSMutableArray *items; //one or many "Item" instances
}

-(id) init;

@property (assign) NSString *cmdId;
@property (assign) NSString *meta;
@property (assign) NSMutableArray *items;

-(void) addItem:(Item *) item;
-(BOOL) isChunked;

@end
