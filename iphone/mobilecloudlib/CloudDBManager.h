#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>


@interface CloudDBManager : NSObject 
{
	@private
	NSManagedObjectContext *storageContext;
}

@property (nonatomic,retain,readonly) NSManagedObjectContext *storageContext;

+(CloudDBManager *) getInstance;
+(void)stop;

//For the classes internal-use only
-(NSPersistentStoreCoordinator *)persistentStoreCoordinator;

-(NSManagedObjectModel *)managedModel;

-(NSString *)applicationDocumentsDirectory;

@end
