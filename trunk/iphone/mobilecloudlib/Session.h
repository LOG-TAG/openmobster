#import <Foundation/Foundation.h>
#import "Anchor.h"
#import "SyncPackage.h"
#import "SyncMessage.h"
#import "Status.h"
#import "StringUtil.h"
#import "GenericAttributeManager.h"
#import "SyncXMLTags.h"
#import "ChangeLogEntry.h"
#import "DeviceSerializer.h"
#import "MobileObject.h"


@interface Session : NSObject 
{
	@private
	NSString *sessionId; //required
	NSString *target; //required
	NSString *source; //required
	
	Anchor *anchor; 
	
	SyncPackage *clientInitPackage;
	SyncPackage *serverInitPackage;
	
	SyncPackage *clientSyncPackage;
	SyncPackage *serverSyncPackage;
	
	SyncPackage *clientClosePackage;
	SyncPackage *serverClosePackage;
	
	/**
	 * some session related meta data
	 */
	SyncMessage *currentMessage;
	int phaseCode;
	NSString *syncType;
	int maxClientSize;
	
	GenericAttributeManager *state; //some generic session state
	
	/**
	 * Specify if this is a background/push based sync
	 */
	BOOL backgroundSync;
	//MobilePushInvocation pushInvocation; FIXME: integrate this once push stack is ported
	
	//TODO: implement Long Object and Map support...not needed for the 2.2-M1 release
}

+(id) withInit;

@property (assign) NSString *sessionId;
@property (assign) NSString *target;
@property (assign) NSString *source;
@property (assign) Anchor *anchor;

@property (assign) SyncPackage *clientInitPackage;
@property (assign) SyncPackage *serverInitPackage;
@property (assign) SyncPackage *clientSyncPackage;
@property (assign) SyncPackage *serverSyncPackage;
@property (assign) SyncPackage *clientClosePackage;
@property (assign) SyncPackage *serverClosePackage;

@property (assign) SyncMessage *currentMessage;
@property (assign) int phaseCode;
@property (assign) NSString *syncType;
@property (assign) int maxClientSize;

@property (assign) GenericAttributeManager *state;
@property (assign) BOOL backgroundSync;

-(NSString *) findDataSource:(SyncMessage *) message;
-(NSString *) findDataTarget:(SyncMessage *) message;
-(NSString *) findDataSource;
-(NSString *) findDataTarget;

-(AbstractOperation *) findOperationCommand:(Status *) status;

-(ChangeLogEntry *)findClientLogEntry:(Status *)status;

-(void)setAttribute:(NSString *)name :(id)value;
-(id)getAttribute:(NSString *)name;
-(void)removeAttribute:(NSString *)name;
@end
