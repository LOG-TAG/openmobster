#import <Foundation/Foundation.h>
#import "GenericAttributeManager.h"


@interface SyncAdapterResponse : NSObject 
{
	@private
	GenericAttributeManager *attributes;
	int status;
}

+(id) withInit;

@property (assign) int status;

-(void) setAttribute:(NSString *) name :(id) value;
-(id) getAttribute:(NSString *) attribute;
-(void) removeAttribute:(NSString *) attribute;

@end
