#import <Foundation/Foundation.h>


@interface StringUtil : NSObject 

+(BOOL) isEmpty:(NSString *)str;
+(NSString *) trim:(NSString *) me;
+(int) lastIndexOf:(NSString *) parent :(NSString *)search;
+(int) indexOf:(NSString *) parent :(NSString *)search;
+(int) indexOf:(NSString *) parent :(NSString *)search :(int) startIndex;
+(NSString *) replaceAll:(NSString *)parent :(NSString *)replace :(NSString *)with;
+(NSArray *) tokenize:(NSString *)parent :(NSString *)separator;
+(NSRange) lastRangeOf:(NSString *)parent :(NSString *)search;
+(NSString *)substring:(NSString *)parent :(int)from :(int)to;
+(NSString *)substring:(NSString *)parent :(int)from;
@end
