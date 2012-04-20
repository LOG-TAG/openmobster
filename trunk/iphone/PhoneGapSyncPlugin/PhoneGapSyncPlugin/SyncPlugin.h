//
//  SyncPlugin.h
//  PhoneGapSyncPlugin
//
//  Created by openmobster on 3/28/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <Cordova/CDVPlugin.h>

@interface SyncPlugin : CDVPlugin

-(void) test:(NSMutableArray *)arguments withDict:(NSMutableDictionary *)options;

-(void) readall:(NSMutableArray *)arguments withDict:(NSMutableDictionary *)options;

-(void) updateBean:(NSMutableArray *)arguments withDict:(NSMutableDictionary *)options;

-(void) addNewBean:(NSMutableArray *)arguments withDict:(NSMutableDictionary *)options;

-(void) deleteBean:(NSMutableArray *)arguments withDict:(NSMutableDictionary *)options;

-(void) insertIntoArray:(NSMutableArray *)arguments withDict:(NSMutableDictionary *)options;

-(void) arrayLength:(NSMutableArray *)arguments withDict:(NSMutableDictionary *)options;

-(void) clearArray:(NSMutableArray *)arguments withDict:(NSMutableDictionary *)options;

-(void) commit:(NSMutableArray *)arguments withDict:(NSMutableDictionary *)options;
@end
