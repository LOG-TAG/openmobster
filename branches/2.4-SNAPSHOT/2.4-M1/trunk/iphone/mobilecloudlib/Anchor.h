#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>
#import "CloudDBManager.h"


@interface Anchor : NSManagedObject 

@property (nonatomic,retain) NSString *oid;
@property (nonatomic,retain) NSString *target;
@property (nonatomic,retain) NSString *lastSync;
@property (nonatomic,retain) NSString *nextSync;

//Persistence related
+(Anchor *) getInstance:(NSString *)channel;
-(BOOL) saveInstance;
@end
