//
//  SyncPlugin.m
//  PhoneGapSyncPlugin
//
//  Created by openmobster on 3/28/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "SyncPlugin.h"
#import "MobileBean.h"
#import "SBJsonWriter.h"
#import "SBJsonParser.h"
#import "BeanList.h"
#import "StringUtil.h"
#import "BeanListEntry.h"

@implementation SyncPlugin

-(void) test:(NSMutableArray *)arguments withDict:(NSMutableDictionary *)options
{
    NSString *callback = [arguments pop];
    NSString *jsString = NULL;
    CDVPluginResult *result = nil;
    
    //spit out arguments
    for(NSString *local in arguments)
    {
        NSLog(@"%@",local);
    }
    
    @try 
     {
         result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:[arguments objectAtIndex:0]];
         jsString = [result toSuccessCallbackString:callback];
     }
     @catch (NSException *exception) 
     {
         result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"Error occurred"];
         jsString = [result toErrorCallbackString:callback];
     }
     @finally 
     {
         [self writeJavascript:jsString];
     }
}

-(void) readall:(NSMutableArray *)arguments withDict:(NSMutableDictionary *)options
{
    NSString *callback = [arguments pop];
    NSString *jsString = NULL;
    CDVPluginResult *result = nil;
    NSString *returnValue = @"0";
    
    NSString *channel = [arguments objectAtIndex:0];
    @try 
    {
        //Read the beans stored in the channel
        NSArray *beans = [MobileBean readAll:channel];
        if(beans == nil || [beans count] == 0)
        {
            returnValue = @"0";
            result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:returnValue];
            jsString = [result toSuccessCallbackString:callback];
            return;
        }
        
        //Get the 'oids' of these beans
        NSMutableArray *oids = [NSMutableArray array];
        for(MobileBean *bean in beans)
        {
            NSString *oid = [bean getId];
            [oids addObject:oid];
            
            //remove this code
            /*NSString *title = [bean getValue:@"title"];
            NSLog(@"Title: %@",title);
            
            //customers
            BeanList *customers = [bean readList:@"customers"];
            int size = [customers size];
            for(int i=0; i<size; i++)
            {
                NSString *uri = [NSString stringWithFormat:@"customers[%d]",i];
                NSString *customerValue = [bean getValue:uri];
                NSLog(@"Customer: %@",customerValue);
            }
            
            //messages
            BeanList *messages = [bean readList:@"messages"];
            size = [messages size];
            for(int i=0; i<size; i++)
            {
                NSString *uri = [NSString stringWithFormat:@"messages[%d].from",i];
                NSString *value = [bean getValue:uri];
                NSLog(@"From: %@",value);
                
                uri = [NSString stringWithFormat:@"messages[%d].to",i];
                value = [bean getValue:uri];
                NSLog(@"To: %@",value);
                
                uri = [NSString stringWithFormat:@"messages[%d].message",i];
                value = [bean getValue:uri];
                NSLog(@"Message: %@",value);
            }*/
        }
        
        //Generate a JSON payload of this array of 'oids'
        SBJsonWriter *jsonWriter = [[[SBJsonWriter alloc] init] autorelease];
        returnValue = [jsonWriter stringWithObject:oids];
        
        result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:returnValue];
        jsString = [result toSuccessCallbackString:callback];
    }
    @catch (NSException *exception) 
    {
        result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[exception reason]];
        jsString = [result toErrorCallbackString:callback];
    }
    @finally 
    {
        [self writeJavascript:jsString];
    }
}

-(void) updateBean:(NSMutableArray *)arguments withDict:(NSMutableDictionary *)options
{
    //spit out arguments
    /*for(NSString *local in arguments)
    {
        NSLog(@"%@",local);
    }*/
    
    NSString *callback = [arguments pop];
    NSString *jsString = NULL;
    CDVPluginResult *result = nil;
    NSString *returnValue = @"0";
    
    NSString *channel = [arguments objectAtIndex:0];
    NSString *oid = [arguments objectAtIndex:1];
    SBJsonParser *jsonParser = [[[SBJsonParser alloc] init] autorelease];
    @try 
    {
        //validation
        if([arguments count]==2)
        {
            result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:returnValue];
            jsString = [result toSuccessCallbackString:callback];
            
            return;
        }

        
        NSString *jsonUpdate = [arguments objectAtIndex:2];
        
        //Bean to be updated
        MobileBean *bean = [MobileBean readById:channel :oid];
        if(bean == nil)
        {
            result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:returnValue];
            jsString = [result toSuccessCallbackString:callback];
            
            return;
        }
        
        //Parse the JSON Update into 
        NSDictionary *values = (NSDictionary *)[jsonParser objectWithString:jsonUpdate];
        if([values count] == 0)
        {
            result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:returnValue];
            jsString = [result toSuccessCallbackString:callback];
            
            return;
        }
        
        //update the fields one at a time
        NSArray *keys = [values allKeys];
        for(NSString *name in keys)
        {
            //validate for array....arrays should be specified by array specific methods
            /*if([StringUtil indexOf:name :@"["] != -1)
            {
                continue;
            }*/
            
            NSString *value = [values objectForKey:name];
            [bean setValue:name :value];
        }
        
        //commit
        [bean save];
        
        result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:returnValue];
        jsString = [result toSuccessCallbackString:callback];
    }
    @catch (NSException *exception) 
    {
        result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[exception reason]];
        jsString = [result toErrorCallbackString:callback];
    }
    @finally 
    {
        [self writeJavascript:jsString];
    }
}

-(void) deleteBean:(NSMutableArray *)arguments withDict:(NSMutableDictionary *)options
{
    //spit out arguments
    /*for(NSString *local in arguments)
     {
     NSLog(@"%@",local);
     }*/
    
    NSString *callback = [arguments pop];
    NSString *jsString = NULL;
    CDVPluginResult *result = nil;
    NSString *returnValue = @"0";
    
    NSString *channel = [arguments objectAtIndex:0];
    NSString *oid = [arguments objectAtIndex:1];
    @try 
    {
        
        //Bean to be deleted
        MobileBean *bean = [MobileBean readById:channel :oid];
        if(bean == nil)
        {
            result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:returnValue];
            jsString = [result toSuccessCallbackString:callback];
            
            return;
        }
        
        returnValue = [bean getId];
                
        //delete the bean
        [bean delete];
        
        result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:returnValue];
        jsString = [result toSuccessCallbackString:callback];
    }
    @catch (NSException *exception) 
    {
        result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[exception reason]];
        jsString = [result toErrorCallbackString:callback];
    }
    @finally 
    {
        [self writeJavascript:jsString];
    }
}

-(void) addNewBean:(NSMutableArray *)arguments withDict:(NSMutableDictionary *)options
{
    //spit out arguments
    /*for(NSString *local in arguments)
     {
     NSLog(@"%@",local);
     }*/
    
    NSString *callback = [arguments pop];
    NSString *jsString = NULL;
    CDVPluginResult *result = nil;
    NSString *returnValue = @"0";
    
    NSString *channel = [arguments objectAtIndex:0];
    SBJsonParser *jsonParser = [[[SBJsonParser alloc] init] autorelease];
    @try 
    {
        //validation
        if([arguments count]==1)
        {
            result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:returnValue];
            jsString = [result toSuccessCallbackString:callback];
            
            return;
        }
        
        NSString *jsonAdd = [arguments objectAtIndex:1];
        
        //Bean to be updated
        MobileBean *bean = [MobileBean newInstance:channel];
        
        //Parse the JSON Update into 
        NSDictionary *values = (NSDictionary *)[jsonParser objectWithString:jsonAdd];
        
        //update the fields one at a time
        if([values count] > 0)
        {
            NSArray *keys = [values allKeys];
            for(NSString *name in keys)
            {
                //validate for array...arrays should be specified by array specific methods
                if([StringUtil indexOf:name :@"["] != -1)
                {
                    continue;
                }
                
                
                NSString *value = [values objectForKey:name];
                [bean setValue:name :value];
            }
        
            //commit
            [bean save];
        
            //return the new oid
            NSString *newoid = [bean getId];
            returnValue = newoid;
        }
        
        result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:returnValue];
        jsString = [result toSuccessCallbackString:callback];
    }
    @catch (NSException *exception) 
    {
        result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[exception reason]];
        jsString = [result toErrorCallbackString:callback];
    }
    @finally 
    {
        [self writeJavascript:jsString];
    }
}

-(void) insertIntoArray:(NSMutableArray *)arguments withDict:(NSMutableDictionary *)options
{
    //spit out arguments
    /*NSLog(@"insertIntoArray Invoked.....");
    for(NSString *local in arguments)
    {
     NSLog(@"%@",local);
    }*/
    
    NSString *callback = [arguments pop];
    NSString *jsString = NULL;
    CDVPluginResult *result = nil;
    NSString *returnValue = @"0";
    
    NSString *channel = [arguments objectAtIndex:0];
    NSString *oid = [arguments objectAtIndex:1];
    NSString *fieldUri = [arguments objectAtIndex:2];
    NSString *json = [arguments objectAtIndex:3];
    SBJsonParser *jsonParser = [[[SBJsonParser alloc] init] autorelease];
    @try 
    {
        //Bean to be updated
        MobileBean *bean = [MobileBean readById:channel :oid];
        if(bean == nil)
        {
            result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:returnValue];
            jsString = [result toSuccessCallbackString:callback];
            
            return;
        }

        
        //Parse the JSON Update into
        NSDictionary *values = (NSDictionary *)[jsonParser objectWithString:json];
        
        BeanListEntry *arrayBean = [BeanListEntry withInit:fieldUri];
        if([values count] == 1)
        {
            //string array
            NSArray *keys = [values allKeys];
            for(NSString *name in keys)
            {
                NSString *value = [values objectForKey:name];
                [arrayBean setValue:value];
            }
        }
        else
        {
            //object array
            NSArray *keys = [values allKeys];
            for(NSString *name in keys)
            {
                NSString *value = [values objectForKey:name];
                [arrayBean setProperty:name :value];
            }
        }
        
        [bean addBean:fieldUri :arrayBean];
        
        //save and sync
        [bean save];
        
        //find the array length
        BeanList *array = [bean readList:fieldUri];
        if(array != nil)
        {
            returnValue = [NSString stringWithFormat:@"%d",[array size]];
        }
                
        result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:returnValue];
        jsString = [result toSuccessCallbackString:callback];
    }
    @catch (NSException *exception) 
    {
        result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[exception reason]];
        jsString = [result toErrorCallbackString:callback];
    }
    @finally 
    {
        [self writeJavascript:jsString];
    } 
}

-(void) arrayLength:(NSMutableArray *)arguments withDict:(NSMutableDictionary *)options
{
    //spit out arguments
    /*NSLog(@"arrayLength Invoked.....");
     for(NSString *local in arguments)
     {
     NSLog(@"%@",local);
     }*/
    
    NSString *callback = [arguments pop];
    NSString *jsString = NULL;
    CDVPluginResult *result = nil;
    NSString *returnValue = @"0";
    
    NSString *channel = [arguments objectAtIndex:0];
    NSString *oid = [arguments objectAtIndex:1];
    NSString *fieldUri = [arguments objectAtIndex:2];
    @try 
    {
        //Bean in question
        MobileBean *bean = [MobileBean readById:channel :oid];
        if(bean == nil)
        {
            result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:returnValue];
            jsString = [result toSuccessCallbackString:callback];
            
            return;
        }
        
        //find the array length
        BeanList *array = [bean readList:fieldUri];
        if(array != nil)
        {
            returnValue = [NSString stringWithFormat:@"%d",[array size]];
        }
        
        result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:returnValue];
        jsString = [result toSuccessCallbackString:callback];
    }
    @catch (NSException *exception) 
    {
        result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[exception reason]];
        jsString = [result toErrorCallbackString:callback];
    }
    @finally 
    {
        [self writeJavascript:jsString];
    } 
}

-(void) clearArray:(NSMutableArray *)arguments withDict:(NSMutableDictionary *)options
{
    //spit out arguments
    /*NSLog(@"clearArray Invoked.....");
     for(NSString *local in arguments)
     {
     NSLog(@"%@",local);
     }*/
    
    NSString *callback = [arguments pop];
    NSString *jsString = NULL;
    CDVPluginResult *result = nil;
    NSString *returnValue = @"0";
    
    NSString *channel = [arguments objectAtIndex:0];
    NSString *oid = [arguments objectAtIndex:1];
    NSString *fieldUri = [arguments objectAtIndex:2];
    @try 
    {
        //Bean in question
        MobileBean *bean = [MobileBean readById:channel :oid];
        if(bean == nil)
        {
            result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:returnValue];
            jsString = [result toSuccessCallbackString:callback];
            
            return;
        }
        
        //clear the array
        [bean clearList:fieldUri];
        
        //save and sync
        [bean save];
        
        result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:returnValue];
        jsString = [result toSuccessCallbackString:callback];
    }
    @catch (NSException *exception) 
    {
        result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[exception reason]];
        jsString = [result toErrorCallbackString:callback];
    }
    @finally 
    {
        [self writeJavascript:jsString];
    } 
}

-(void) commit:(NSMutableArray *)arguments withDict:(NSMutableDictionary *)options
{
    //spit out arguments
    /*NSLog(@"commit Invoked.....");
     for(NSString *local in arguments)
     {
     NSLog(@"%@",local);
     }*/
    
    //empty implementation. Due to the Async nature of the plugin on iOS, the commit function is not needed
    //only reason its here is to maintain code compatibility with Android
    
    NSString *callback = [arguments pop];
    NSString *jsString = NULL;
    CDVPluginResult *result = nil;
    NSString *returnValue = @"0";
    @try 
    {
        result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:returnValue];
        jsString = [result toSuccessCallbackString:callback];
    }
    @catch (NSException *exception) 
    {
        result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[exception reason]];
        jsString = [result toErrorCallbackString:callback];
    }
    @finally 
    {
        [self writeJavascript:jsString];
    } 
}
@end