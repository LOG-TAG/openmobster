#import <Foundation/Foundation.h>

@interface GenericAttributeManager : NSObject 
{
	@private
	NSMutableDictionary *attributes;
}

+(id) withInit;

-(id) getAttribute: (NSString *)name;

-(void) setAttribute: (NSString *)name : (id)value;

-(void) removeAttribute: (NSString *) name;

-(BOOL) isEmpty;

-(NSArray *) getNames;

-(NSArray *) getValues;

@end
