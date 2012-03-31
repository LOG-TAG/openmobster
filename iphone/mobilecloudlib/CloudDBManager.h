#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>


@interface CloudDBManager : NSObject 
{
	@private
    NSMutableDictionary *table;
    NSPersistentStoreCoordinator *coordinator;
}

+(CloudDBManager *) getInstance;
+(void)stop;
-(NSManagedObjectContext *) storageContext;

//For the classes internal-use only
-(NSPersistentStoreCoordinator *)persistentStoreCoordinator;

-(NSManagedObjectModel *)managedModel;

-(NSString *)applicationDocumentsDirectory;

@end
