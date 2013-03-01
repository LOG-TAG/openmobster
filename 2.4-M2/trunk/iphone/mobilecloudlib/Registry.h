#import <Foundation/Foundation.h>
#import "Service.h"

@interface Registry : NSObject 
{
	@private
	NSMutableArray *services;
	BOOL isStarted;
}

+(Registry *) getInstance;

+(BOOL) isActive;

-(BOOL) isStarted;

-(void)start;
-(void)stop;

-(Service *) lookup: (Class) serviceClass;
-(void)addService:(Service *)service;

@end
