#import <Foundation/Foundation.h>


@interface Item : NSObject 
{
	@protected
	NSString *target; //zero to one
	NSString *source; //zero to one
	NSString *data; //zero to one
	NSString *meta; //zero to one
	BOOL moreData; //zero to one
}

@property (assign) NSString *target;
@property (assign) NSString *source;
@property (assign) NSString *data;
@property (assign) NSString *meta;
@property (assign) BOOL moreData;

+(id) withInit;

@end
