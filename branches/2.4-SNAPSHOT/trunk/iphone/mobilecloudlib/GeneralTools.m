#import "GeneralTools.h"
#import "UIDevice+IdentifierAddition.h"

@implementation GeneralTools

+(NSString *) generateUniqueId
{
	CFUUIDRef theUUID = CFUUIDCreate(NULL);
	CFStringRef uuid = CFUUIDCreateString(NULL, theUUID);
	
	CFRelease(theUUID);
	
	return [(NSString *)uuid autorelease];
}

+(NSString *) getDeviceIdentifier
{
	NSString * deviceIdentifier = [[UIDevice currentDevice] uniqueGlobalDeviceIdentifier];
	return deviceIdentifier;
}

@end
