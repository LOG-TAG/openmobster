#import "CloudDBManager.h"
#import "SystemException.h"

static CloudDBManager *singleton = nil;


@implementation CloudDBManager

+(CloudDBManager *) getInstance
{
	if(singleton)
	{
		return singleton;
	}
	
	@synchronized([CloudDBManager class])
	{
		if(singleton == nil)
		{
			singleton = [[CloudDBManager alloc] init];
		}
	}
	
	return singleton;
}

+(void)stop
{
	@synchronized([CloudDBManager class])
	{
		if(singleton != nil)
		{
			[singleton release];
			singleton = nil;
		}
	}
}

-(void) dealloc
{
	[super dealloc];
	[storageContext release];
}


-(NSManagedObjectContext *) storageContext
{
	if(storageContext != nil)
	{
		return storageContext;
	}
	
	NSPersistentStoreCoordinator *coordinator = nil;
	@try
	{
		coordinator = [self persistentStoreCoordinator];
	}
	@catch(SystemException *se)
	{
		//Establishing peristent storage crashed. The App must not be allowed
		//to keep going
		NSLog(@"%@",[se getMessage]);
		
		//TODO: may be show some error message..Find the best way to exit the App
		exit(-1);
	}
	
	storageContext = [[NSManagedObjectContext alloc] init];
	[storageContext setPersistentStoreCoordinator: coordinator];
	
	return storageContext;
}

//For the classes internal-use only
-(NSPersistentStoreCoordinator *)persistentStoreCoordinator
{
	NSString *appDir = [self applicationDocumentsDirectory];
    NSString *storePath = [appDir stringByAppendingPathComponent: @"cloudb.sqlite"];
	
	NSFileManager *fileManager = [NSFileManager defaultManager];
    if(![fileManager fileExistsAtPath:appDir]) 
	{
        //Recursively create the app dir
		[fileManager createDirectoryAtPath:appDir withIntermediateDirectories:YES attributes:nil error:NULL];
    }
	if(![fileManager fileExistsAtPath:storePath])
	{
		[fileManager createFileAtPath:storePath contents:nil attributes:nil];
	}
	
    NSURL *storeUrl = [NSURL fileURLWithPath:storePath];
	
    NSDictionary *options = [NSDictionary dictionaryWithObjectsAndKeys:[NSNumber numberWithBool:YES], NSMigratePersistentStoresAutomaticallyOption, [NSNumber numberWithBool:YES], NSInferMappingModelAutomaticallyOption, nil]; 
	NSManagedObjectModel *myModel = [self managedModel];
    NSPersistentStoreCoordinator *persistentStoreCoordinator = [[NSPersistentStoreCoordinator alloc] initWithManagedObjectModel:myModel];
	persistentStoreCoordinator = [persistentStoreCoordinator autorelease];
	
    NSError *error;
    if (![persistentStoreCoordinator addPersistentStoreWithType:NSSQLiteStoreType configuration:nil URL:storeUrl options:options error:&error]) 
	{
		//Handle the error by throwing a SystemException
		NSString *errorInfo = [error localizedDescription];
		NSMutableArray *info = [NSMutableArray arrayWithObjects:errorInfo, nil];
		SystemException *exception = [SystemException withContext:@"common/CloudDBManager" method:@"persistentStoreCoordinator" parameters:info];
		@throw exception;
	}    
    
    return persistentStoreCoordinator;
}


-(NSManagedObjectModel *)managedModel
{
	NSArray *allBundles = [NSBundle allBundles];
	
	NSManagedObjectModel *managedObjectModel = [NSManagedObjectModel mergedModelFromBundles:allBundles];
	
    return managedObjectModel;
}

-(NSString *)applicationDocumentsDirectory
{
	//Finds the Document directory for the installed App
	NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
	
    NSString *basePath = ([paths count] > 0) ? [paths objectAtIndex:0] : nil;
	
    return basePath;
}
@end
