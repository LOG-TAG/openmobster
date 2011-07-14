#import <SenTestingKit/SenTestingKit.h>
#import <UIKit/UIKit.h>
#import "MobileObject.h"
#import "SyncEngine.h"
#import "AbstractOperation.h"
#import "Status.h"

@interface SyncEngineTests : SenTestCase 
{

}
-(void) setUpMobileDatabase:(SyncEngine *)syncEngine;
-(void) dumpChangeLog:(NSArray *)changelog;
-(MobileObject *) createPOJOWithStrings:(NSString *)value;
-(MobileObject *) createNestedPOJO;
-(MobileObject *) createArrayPOJO:(NSString *)value :(int)size;
-(void) print:(MobileObject *)mobileObject;
-(void) printOperation:(AbstractOperation *)operation;
-(void) printStatus:(Status *)status;
@end
