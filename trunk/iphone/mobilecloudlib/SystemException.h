#import <Foundation/Foundation.h>


@interface SystemException : NSException 
{
	@private
	NSString *className;
	NSString *method;
	NSMutableArray *parameters;
}

-initWithContext:(NSString *) className method:(NSString *) method parameters:(NSMutableArray *)parameters;
+(id) withContext:(NSString *) className method:(NSString *) method parameters:(NSMutableArray *)parameters;


-(NSString *) getMessage;
@end
