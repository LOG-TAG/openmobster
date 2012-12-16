#import <Foundation/Foundation.h>
#import "GenericAttributeManager.h"


@interface SyncAdapterRequest : NSObject 
{
	@private
	GenericAttributeManager *attributes;
}

+(id) withInit;

-(void) setAttribute:(NSString *) name :(id) value;
-(id) getAttribute:(NSString *) attribute;
-(void) removeAttribute:(NSString *) attribute;

@end
