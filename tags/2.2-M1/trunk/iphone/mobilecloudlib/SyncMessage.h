#import <Foundation/Foundation.h>
#import "Credential.h"
#import "Alert.h"
#import "Status.h"
#import "RecordMap.h"
#import "SyncCommand.h"


@interface SyncMessage : NSObject 
{
	@private
	NSString *messageId; //required
	int maxClientSize; //normally used by client side message		
	BOOL final;
	BOOL clientInitiated; //signifies if this message was initiated by the client or not
	NSMutableArray *alerts; //zero or many..'Alert' instances
	NSMutableArray *status; //zero or many..'Status' instances
	NSMutableArray *syncCommands; //zero or many..SyncCommand instances
	RecordMap *recordMap; //not-required
	Credential *credential;
}

+(id) withInit;

@property (assign) NSString *messageId;
@property (assign) int maxClientSize;
@property (assign) BOOL final;
@property (assign) BOOL clientInitiated;
@property (assign) NSMutableArray *alerts;
@property (assign) NSMutableArray *status;
@property (assign) NSMutableArray *syncCommands;
@property (assign) RecordMap *recordMap;
@property (assign) Credential *credential;

-(void) addAlert:(Alert *)alert;
-(void) addStatus:(Status *)incoming;
-(void) addCommand:(SyncCommand *)command;

@end
