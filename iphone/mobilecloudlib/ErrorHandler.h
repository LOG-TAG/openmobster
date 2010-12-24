#import <Foundation/Foundation.h>


@interface ErrorHandler : NSObject 

+(void) handleException:(NSException *) exception;

+(NSString *) generateReport;

+(void) clearAll;

@end
