#import "StringUtil.h"

@implementation StringUtil

+(BOOL) isEmpty:(NSString *) str
{
	if(str == nil)
	{
		return YES;
	}
	
	if([[StringUtil trim:str] length] == 0)
	{
		return YES;
	}
	
	return NO;
}


+(NSString *) trim:(NSString *) me
{
	if(me == nil)
	{
		return nil;
	}
	
	NSString *trimmed = [me stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]];
	return trimmed;
}

+(int) lastIndexOf:(NSString *) parent :(NSString *)search
{
	NSRange range = [parent rangeOfString:search options:NSBackwardsSearch];
	
	if(range.location != NSNotFound)
	{
		return range.location;
	}
	
	return -1;
}

+(int) indexOf:(NSString *) parent :(NSString *)search
{
	NSRange range = [parent rangeOfString:search];
	
	if(range.location != NSNotFound)
	{
		return range.location;
	}
	
	return -1;
}

+(int) indexOf:(NSString *) parent :(NSString *)search :(int) startIndex
{
	int endIndex = [parent length];
	NSRange searchRange = NSMakeRange(startIndex, endIndex-startIndex);
	NSRange result = [parent rangeOfString:search options:NSLiteralSearch range:searchRange];
	if(result.location != NSNotFound)
	{
		return result.location;
	}
	return -1;
}

+(NSString *) replaceAll:(NSString *)parent :(NSString *)replace :(NSString *)with
{
	NSMutableString *buffer = [NSMutableString stringWithString:parent];
	NSRange searchRange = NSMakeRange(0, [buffer length]);
	[buffer replaceOccurrencesOfString:replace withString:with options:NSLiteralSearch range:searchRange];
	return [NSString stringWithString:buffer];
}

+(NSArray *) tokenize:(NSString *)parent :(NSString *)separator
{
	return [parent componentsSeparatedByString:separator];
}

+(NSRange) lastRangeOf:(NSString *)parent :(NSString *)search
{
	NSRange range = [parent rangeOfString:search options:NSBackwardsSearch];
	return range;
}

+(NSString *)substring:(NSString *)parent :(int)from :(int)to
{
	NSRange range = NSMakeRange(from, to-from);
	return [parent substringWithRange:range];
}

+(NSString *)substring:(NSString *)parent :(int)from
{
	int to = [parent length];
	return [StringUtil substring:parent :from :to];
}
@end