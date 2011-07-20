#import <Foundation/Foundation.h>
#import "Item.h"

@interface Alert : NSObject 
{
	@private
	NSString *cmdId; //required
	NSString *data; //zero to one
	NSMutableArray *items; //zero to many items..contains 'Item' instances
}

+(id) withInit;

@property (assign) NSString *cmdId;
@property (assign) NSString *data;
@property (assign) NSMutableArray *items;

-(void) addItem:(Item *) item;

@end
