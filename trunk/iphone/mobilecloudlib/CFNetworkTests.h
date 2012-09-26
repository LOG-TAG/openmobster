#import <SenTestingKit/SenTestingKit.h>
#import <UIKit/UIKit.h>
#import <Foundation/Foundation.h>
#import <CFNetwork/CFNetwork.h>
#import <CoreData/CoreData.h>
#import "Configuration.h"

@interface CFNetworkTests : SenTestCase 
{

}

-(void) runNetSessionTest:(NSString *)handshake :(NSString *)payload;
-(void) runNetworkConnectorTest:(BOOL) secure :(NSString *)handshake :(NSString *)payload;
-(void) writeToStream:(CFWriteStreamRef) writeStream :(UInt8 *)data;
-(NSString *)readFromStream:(CFReadStreamRef) readStream;

@end
