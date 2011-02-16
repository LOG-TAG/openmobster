#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>
#import "GenericAttributeManager.h"
#import "Channel.h"

@interface Configuration : NSManagedObject 

@property (nonatomic,retain) NSString *deviceId;
@property (nonatomic,retain) NSString *serverId;
@property (nonatomic,retain) NSString *serverIp;
@property (nonatomic,retain) NSString *plainServerPort;
@property (nonatomic,retain) NSString *secureServerPort;
@property (nonatomic,retain) NSString *httpPort;
@property (nonatomic,retain) NSString *authenticationHash;
@property (nonatomic,retain) NSString *authenticationNonce;
@property (nonatomic,retain) NSString *email;

@property (nonatomic,retain) NSNumber *active;
@property (nonatomic,retain) NSNumber *sslActive;
@property (nonatomic,retain) NSNumber *maxPacketSize;
@property (nonatomic,retain) NSNumber *pollInterval;
@property (nonatomic,retain) NSNumber *pushMode;

@property (nonatomic,retain) NSMutableDictionary *channels;
@property (nonatomic,retain) NSMutableSet *appChannels;

//Persistence related
+(Configuration *) getInstance;
+(BOOL)clear;
-(BOOL) saveInstance;

//App related
-(NSString *) authHash;
-(NSString *) serverPort;
-(BOOL) isInPushMode;
-(BOOL) isActivated;

//System-wide channel operations
-(BOOL)establishOwnership:(Channel *)channel :(BOOL)force;
-(NSDictionary *)getChannelRegistry;

//App-level channel operations
-(void)addAppChannel:(NSString *)appChannel;
-(BOOL)isChannelWritable:(Channel *)appChannel;
-(BOOL)isChannelRegistered:(NSString *)appChannel;
@end
