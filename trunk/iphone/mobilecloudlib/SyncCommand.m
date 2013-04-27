#import "SyncCommand.h"


@implementation SyncCommand

@synthesize cmdId;
@synthesize target;
@synthesize source;
@synthesize meta;
@synthesize numberOfChanges;
@synthesize addCommands;
@synthesize replaceCommands;
@synthesize deleteCommands;

+(id) withInit
{
	SyncCommand *command = [[[SyncCommand alloc] init] autorelease];
	
	if(command.addCommands == nil)
	{
		command.addCommands = [NSMutableArray array];
	}
	
	if(command.replaceCommands == nil)
	{
		command.replaceCommands = [NSMutableArray array];
	}
	
	if(command.deleteCommands == nil)
	{
		command.deleteCommands = [NSMutableArray array];
	}
	
	return command;
}

-(void) addOperation:(AbstractOperation *) operation
{
	if([operation class] == [Add class])
	{
		[self.addCommands addObject:operation];
	}
	else if([operation class] == [Replace class])
	{
		[self.replaceCommands addObject:operation];
	}
	else if([operation class] == [Delete class])
	{
		[self.deleteCommands addObject:operation];
	}
}

-(NSMutableArray *) allOperations
{
	NSMutableArray *all = [NSMutableArray array];
	
	if(self.addCommands != nil)
	{
		[all addObjectsFromArray:self.addCommands];
	}
	
	if(self.replaceCommands != nil)
	{
		[all addObjectsFromArray:self.replaceCommands];	
	}
	
	if(self.deleteCommands != nil)
	{
		[all addObjectsFromArray:self.deleteCommands];
	}
	
	return all;
}

-(void)clear
{
    self.addCommands = [NSMutableArray array];
    self.replaceCommands = [NSMutableArray array];
    self.deleteCommands = [NSMutableArray array];
}

@end
