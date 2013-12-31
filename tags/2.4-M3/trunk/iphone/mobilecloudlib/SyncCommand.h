#import <Foundation/Foundation.h>
#import "Add.h"
#import "Replace.h"
#import "Delete.h"

@interface SyncCommand : NSObject 
{
	@private
	NSString *cmdId; //required
	NSString *target; //nullable
	NSString *source; //nullable 
	NSString *meta; //nullable
	NSString *numberOfChanges; //nullable
	NSMutableArray *addCommands; //zero or many (Add instance)
	NSMutableArray *replaceCommands; //zero or many (Replace instance)
	NSMutableArray *deleteCommands; //zero or many} (Delete instance)
}

+(id) withInit;

@property (assign) NSString *cmdId;
@property (assign) NSString *target;
@property (assign) NSString *source;
@property (assign) NSString *meta;
@property (assign) NSString *numberOfChanges;
@property (assign) NSMutableArray *addCommands;
@property (assign) NSMutableArray *replaceCommands;
@property (assign) NSMutableArray *deleteCommands;

-(void) addOperation:(AbstractOperation *) operation;
-(NSMutableArray *) allOperations;

-(void)clear;

@end
