#import <Foundation/Foundation.h>
#import "AbstractOperation.h"


@interface Delete : AbstractOperation 
{
	@private
	BOOL archive;
	BOOL softDelete;
}

+(id) withInit;

@property (assign) BOOL archive;
@property (assign) BOOL softDelete;

@end
