/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "BeanListEntry.h"
#import "StringUtil.h"
#import "QSStrings.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation BeanListEntry

@synthesize index;
@synthesize listProperty;
@synthesize properties;


+(BeanListEntry *)withInit:(int) index :(NSDictionary *)properties :(NSString *)listProperty
{
	BeanListEntry *instance = [[BeanListEntry alloc] init];
	
	instance.index = index;
	instance.properties = [NSMutableDictionary dictionaryWithDictionary:properties];
	instance.listProperty = listProperty;
	
	instance = [instance autorelease];
	
	return instance;
}

+(BeanListEntry *)withInit:(NSString *)listProperty
{
    BeanListEntry *instance = [[BeanListEntry alloc] init];
	
	instance.index = 0;
	instance.properties = [NSMutableDictionary dictionary];
	instance.listProperty = listProperty;
	
	instance = [instance autorelease];
	
	return instance; 
}

-(void)dealloc
{
    [properties release];
    [listProperty release];
    [super dealloc];
}

-(NSString *) getProperty:(NSString *)propertyExpression
{
	NSString *propertyUri = [self calculatePropertyUri:propertyExpression];
	NSString *value = (NSString *)[properties valueForKey:propertyUri];
	
	return value;
}

-(void)setProperty:(NSString *)propertyExpression :(NSString *)value
{
	NSString *propertyUri = [self calculatePropertyUri:propertyExpression];
	[properties setValue:value forKey:propertyUri];
}

-(NSData *) getBinaryProperty:(NSString *)propertyExpression
{
    NSString *propertyUri = [self calculatePropertyUri:propertyExpression];
	NSString *value = (NSString *)[properties valueForKey:propertyUri];
	
    if(![StringUtil isEmpty:value])
    {
        NSData *binary = [QSStrings decodeBase64WithString:value];
        return binary;
    }
    
	return nil; 
}

-(void)setBinaryProperty:(NSString *)propertyExpression :(NSData *)value
{
    NSString *propertyUri = [self calculatePropertyUri:propertyExpression];
    
    NSString *encodedValue = [QSStrings encodeBase64WithData:value];
    
	[properties setValue:encodedValue forKey:propertyUri];
}

-(NSString *)getValue
{
	if([properties count] == 1)
	{
		NSArray *allKeys = [properties allKeys];
		NSString *key = [allKeys objectAtIndex:0];
		NSString *propertyUri = [self calculatePropertyUri:listProperty];
		key = [StringUtil trim:key];
		
		BOOL isEmpty = [StringUtil isEmpty:key];
		
		if(isEmpty || [propertyUri hasSuffix:key])
		{
			NSArray *values = [properties allValues];
			NSString *value = (NSString *)[values objectAtIndex:0];
			return value;
		}
	}
		
	return nil;
}

-(void)setValue:(NSString *)value
{
	[properties setValue:value forKey:@""];
}

-(NSDictionary *)getProperties
{
	return [NSDictionary dictionaryWithDictionary:properties];
}
//-------internal------------------------------------------
-(NSString *)calculatePropertyUri:(NSString *)propertyExpression
{
	NSMutableString *buffer = [NSMutableString stringWithString:@"/"];
	NSString *altered = [StringUtil replaceAll:propertyExpression :@"." :@"/"];
	
	[buffer appendString:altered];
	
	
	return [NSString stringWithString:buffer];
}
@end
