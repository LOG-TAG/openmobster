#import <Foundation/Foundation.h>

@interface Service : NSObject 
{
	@private
	NSString *oid;
}

@property (nonatomic,retain) NSString *oid;

@end
