#import <Foundation/Foundation.h>


@interface MapItem : NSObject 
{
	@private 
	NSString *source;
	NSString *target;
}

+(id) withInit;

@property (assign) NSString *source;
@property (assign) NSString *target;

@end
