#import <Foundation/Foundation.h>


@interface Credential : NSObject 
{
	@private
	NSString *type;
	NSString *data;
	NSString *nextNonce;
	NSString *format;
}

@property (assign) NSString *type;
@property (assign) NSString *data;
@property (assign) NSString *nextNonce;
@property (assign) NSString *format;

+(id) withInit;

@end
