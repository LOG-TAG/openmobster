#import "Anchor.h"


@implementation Anchor

@dynamic oid;
@dynamic target;
@dynamic lastSync;
@dynamic nextSync;

+(Anchor *) getInstance:(NSString *)channel
{
	//Get the Storage Context
	NSManagedObjectContext *managedContext = [CloudDBManager getInstance].storageContext;
	NSEntityDescription *entity = [NSEntityDescription entityForName:@"Anchor" 
											  inManagedObjectContext:managedContext];
	
	//Get an instance if its already been provisioned
	NSFetchRequest *request = [[[NSFetchRequest alloc] init] autorelease];
	[request setEntity:entity];
	
	NSArray *all = [managedContext executeFetchRequest:request error:NULL];
	
	if(all != nil && [all count] > 0)
	{
		for(Anchor *local in all)
		{
			if([local.target isEqualToString:channel])
			{
				return local;
			}
		}
	}
	
	//Creates a new instance and returns it
	Anchor *anchor = [NSEntityDescription insertNewObjectForEntityForName:@"Anchor" 
												   inManagedObjectContext:managedContext];
	anchor.oid = @"";
	anchor.target = channel;
	anchor.lastSync = @"";
	anchor.nextSync = @"";
	[managedContext save:NULL];
	
	return anchor;
}

-(BOOL) saveInstance
{
	//Get the Storage Context
	NSManagedObjectContext *managedContext = [CloudDBManager getInstance].storageContext;
	
	NSError* error;
	BOOL success = [managedContext save:&error];
	
	//Used for debugging
	/*if(!success)
	{
		NSLog(@"Error during save!!!");
		NSLog(@"SaveError: %@, %@", error, [error userInfo]);
	}
	else 
	{
		NSLog(@"Save was a success!!!");
	}*/
	
	
	return success;	
}
@end
