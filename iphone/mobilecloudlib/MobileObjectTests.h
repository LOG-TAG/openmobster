#import <SenTestingKit/SenTestingKit.h>
#import <UIKit/UIKit.h>
#import "MobileObject.h"

@interface MobileObjectTests : SenTestCase 
{

}
-(MobileObject *) createPOJOWithStrings:(NSString *)value;
-(MobileObject *) createNestedPOJO;
-(MobileObject *) createArrayPOJO:(NSString *)value :(int)size;
-(void) print:(MobileObject *)mobileObject;
@end
