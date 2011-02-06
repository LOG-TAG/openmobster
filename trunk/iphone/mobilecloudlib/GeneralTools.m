#import <UIKit/UIDevice.h>

#import "GeneralTools.h"

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
	NSString * deviceIdentifier = [[UIDevice currentDevice] uniqueIdentifier];
	return deviceIdentifier;
}

@end
