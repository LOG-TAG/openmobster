#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>


@interface CloudDBManager : NSObject 
{
	@private
    NSPersistentStoreCoordinator *coordinator;
    NSManagedObjectContext *mainContext;
}

@property(nonatomic,retain) NSPersistentStoreCoordinator *coordinator;
@property(nonatomic,retain) NSManagedObjectContext *mainContext;

+(CloudDBManager *) getInstance;
+(void)stop;
-(NSManagedObjectContext *) storageContext;

//For the classes internal-use only
-(NSPersistentStoreCoordinator *)persistentStoreCoordinator;

-(NSManagedObjectModel *)managedModel;

-(NSString *)applicationDocumentsDirectory;

-(void) contextDidSave:(NSNotification *)saveNotification;

@end
