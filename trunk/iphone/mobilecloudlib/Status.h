#import <Foundation/Foundation.h>
#import "Credential.h"
#import "Item.h"

@interface Status : NSObject 
{
	@private
	NSString *cmdId; //required
	NSString *data;	//required
	NSString *msgRef; //required
	NSString *cmdRef; //required
	NSString *cmd; //required
	NSMutableArray *targetRefs; //zero to many target refs..contains NSString instances
	NSMutableArray *sourceRefs; //zero to many source refs..contains NSString instances
	NSMutableArray *items; //zero to many items..contains Item instances
	Credential *credential; //credential info in case of a successful authentication with the server
}

+(id) withInit;

@property (assign) NSString *cmdId;
@property (assign) NSString	*data;
@property (assign) NSString *msgRef;
@property (assign) NSString *cmdRef;
@property (assign) NSString *cmd;
@property (assign) NSMutableArray *targetRefs;
@property (assign) NSMutableArray *sourceRefs;
@property (assign) NSMutableArray *items;
@property (assign) Credential *credential;

-(void) addItem:(Item *) item;
-(void) addSourceRef:(NSString *) sourceRef;
-(void) addTargetRef:(NSString *) targetRef;

@end
