/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "MobileObject.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation MobileObject

@synthesize service;
@synthesize recordId;
@synthesize serverRecordId;
@synthesize proxy;
@synthesize createdOnDevice;
@synthesize locked;
@synthesize dirtyStatus;
@synthesize fields;
@synthesize arrayMetaData;

+(id) withInit
{
	MobileObject *mo = [[[MobileObject alloc] init] autorelease];
	
	mo.fields = [NSMutableArray array];
	mo.arrayMetaData = [NSMutableArray array];
	
	return mo;
}

-(void)dealloc
{
	[service release];
	[recordId release];
	[serverRecordId release];
	[dirtyStatus release];
	[fields release];
	[arrayMetaData release];
	
	[super dealloc];
}

-(NSString *) getValue:(NSString *)uri
{
	if(![uri hasPrefix:@"/"])
	{
		uri = [NSString stringWithFormat:@"%@%@",@"/",uri];
	}
	
	NSString *fieldUri = [StringUtil replaceAll:uri :@"." :@"/"];
	Field *field = [self findField:fieldUri];
	
	if(field != nil)
	{
		return field.value;
	}
	
	return nil;
}

-(void) setValue:(NSString *)uri value:(NSString *)value
{
	if([self.fields count]==0 || self.createdOnDevice)
	{
		self.createdOnDevice = YES;
		
		NSString *localUri = [NSString stringWithFormat:@"%@%@",@"/",uri];
		localUri = [StringUtil replaceAll:localUri :@"." :@"/"];
		
		BOOL createField = YES;
		for(Field *localField in self.fields)
		{
			if([localField.uri isEqualToString:localUri])
			{
				localField.value = value;
				createField = NO;
			}
		}
		
		if(createField)
		{
			Field *field = [Field withInit:localUri name:uri value:value];
			[(NSMutableArray *)self.fields addObject:field];
		}
		
		return;
	}
	
	if(![uri hasPrefix:@"/"])
	{
		uri = [NSString stringWithFormat:@"%@%@",@"/",uri];
	}
	
	NSString *fieldUri = [StringUtil replaceAll:uri :@"." :@"/"];
	Field *field = [self findField:fieldUri];
	if(field != nil)
	{
		field.value = value;
	}
}

-(Field *) findField:(NSString *)inputUri
{
	if(self.fields != nil)
	{
		for(Field *local in self.fields)
		{
			BOOL doesUriMatch = [self doesUriMatch:inputUri :local.uri];
			if(doesUriMatch)
			{
				return local;
			}
		}
	}
	return nil;
}

-(BOOL) doesUriMatch:(NSString *)inputUri :(NSString *)fieldUri
{
	NSMutableString *buffer = [NSMutableString stringWithString:@"/"];
	NSArray *st = [StringUtil tokenize:fieldUri :@"/"];
	if(st != nil)
	{
		for(NSString *token in st)
		{
			if([StringUtil isEmpty:token])
			{
				continue;
			}
			
			NSRange range = [token rangeOfString:@"."];
			if(range.location == NSNotFound)
			{
				[buffer appendFormat:@"%@%@",token,@"/"];
			}
		}
	}
	
	if([buffer hasSuffix:@"/"])
	{
		NSRange deleteRange = NSMakeRange([buffer length]-1, 1);
		[buffer deleteCharactersInRange:deleteRange];
	}
	
	if([buffer isEqualToString:inputUri])
	{
		return YES;
	}
	
	NSRange range = [buffer rangeOfString:@"[0]"];
	if(range.location != NSNotFound)
	{
		inputUri = [StringUtil replaceAll:inputUri :@"[0]" :@""];
		fieldUri = [StringUtil replaceAll:buffer :@"[0]" :@""];
		
		if([fieldUri isEqualToString:inputUri])
		{
			return YES;
		}
	}
	
	return NO;
}

-(int) calculateArrayIndex:(NSString *)fieldUri
{
	NSRange startRange = [StringUtil lastRangeOf:fieldUri :@"["];
	if(startRange.location != NSNotFound)
	{
		NSRange searchRange = NSMakeRange(startRange.location, 
							  [fieldUri length]-startRange.location);
		NSRange endRange = [fieldUri rangeOfString:@"]" options:NSLiteralSearch range:searchRange];
		
		int from = startRange.location+1;
		int to = endRange.location;
		
		NSString *indexStr = [StringUtil substring:fieldUri :from :to];
		
		return [indexStr intValue];
	}
	
	return 0;
}

-(NSString *) calculateArrayUri:(NSString *)fieldUri
{
	int lastIndex = [StringUtil lastIndexOf:fieldUri :@"["];
	if(lastIndex != -1)
	{
		return [fieldUri substringToIndex:lastIndex];
	}
	return nil;
}

-(ArrayMetaData *) findArrayMetaData:(NSString *)arrayUri
{
	if(self.arrayMetaData != nil)
	{
		for(ArrayMetaData *local in self.arrayMetaData)
		{
			if([local.arrayUri isEqualToString:arrayUri])
			{
				return local;
			}
		}
	}
	return nil;
}

-(NSArray *) findArrayFields:(NSString *) arrayUri
{
	NSMutableArray *buffer = [NSMutableArray array];
	
	if(self.fields != nil)
	{
		for(Field *local in self.fields)
		{
			NSString *localUri = local.uri;
			NSString *localArrayUri = [self calculateArrayUri:localUri];
			if(![StringUtil isEmpty:localArrayUri])
			{
				if([self doesUriMatch:arrayUri :localArrayUri])
				{
					[buffer addObject:local];
				}
			}
		}
	}
	
	return [NSArray arrayWithArray:buffer];
}

-(NSArray *) findArrayElementFields:(NSString *) arrayUri :(int) elementIndex
{
	NSMutableArray *buffer = [NSMutableArray array];
	
	if(self.fields != nil)
	{
		for(Field *local in self.fields)
		{
			NSString *localUri = local.uri;
			NSString *localArrayUri = [self calculateArrayUri:localUri];
			if(![StringUtil isEmpty:localArrayUri])
			{
				int localElementIndex = [self calculateArrayIndex:localUri];
				if(localElementIndex == elementIndex &&
				   [self doesUriMatch:arrayUri :localArrayUri]
				)
				{
					[buffer addObject:local];
				}
			}
		}
	}
	
	return [NSArray arrayWithArray:buffer];
}

-(int) findIndexValueInsertionPoint:(NSString *)indexedPropertyName
{
	int matchedIndex = -1; //-1 indicates the array does not exist
	if(self.fields != nil)
	{
		int i=0;
		for(Field *local in self.fields)
		{
			NSString *localUri = local.uri;
			int openIndex = [StringUtil lastIndexOf:localUri :@"["];
			if(openIndex != -1)
			{
				NSString *localIndexedPropertyName = [localUri substringToIndex:openIndex];
				if([self doesUriMatch:indexedPropertyName :localIndexedPropertyName])
				{
					matchedIndex = i;
				}
			}
			i++;
		}
	}
	
	return matchedIndex; 
}

-(int) getArrayLength:(NSString *) arrayUri
{
	NSString *uri = [NSString stringWithFormat:@"%@%@",@"/",arrayUri];
	arrayUri = [StringUtil replaceAll:uri :@"." :@"/"];
	
	ArrayMetaData *local = [self findArrayMetaData:arrayUri];
	if(local != nil)
	{
		return [local.arrayLength intValue];
	}
	
	return 0;
}

-(void) clearArray:(NSString *) indexedPropertyName
{
	if(![indexedPropertyName hasPrefix:@"/"])
	{
		indexedPropertyName = [NSString stringWithFormat:@"%@%@",@"/",indexedPropertyName];
	}	
	indexedPropertyName = [StringUtil replaceAll:indexedPropertyName 
												:@"." :@"/"];
	
	NSArray *fieldsToDelete = [self findArrayFields:indexedPropertyName];
	if(fieldsToDelete != nil)
	{
		for(Field *local in fieldsToDelete)
		{
			[self.fields removeObject:local];
		}
		
		//Decrement the ArrayMetaData
		ArrayMetaData *local = [self findArrayMetaData:indexedPropertyName];
		if(local != nil)
		{
			[self.arrayMetaData removeObject:local];
		}
	}
}

-(void) removeArrayElement:(NSString *) arrayUri :(int) elementAt
{
	NSString *indexedFieldUri = [NSString stringWithFormat:@"%@[%d]",arrayUri,elementAt];
	
	if(![indexedFieldUri hasPrefix:@"/"])
	{
		indexedFieldUri = [NSString stringWithFormat:@"%@%@",@"/",indexedFieldUri];
	}	
	indexedFieldUri = [StringUtil replaceAll:indexedFieldUri 
												:@"." :@"/"];
	
	NSMutableArray *fieldsToDelete = [NSMutableArray array];
	
	//Get a list of indexed properties whose uri must now be modified
	int openIndex = [StringUtil lastIndexOf:indexedFieldUri :@"["];
	NSString *indexedPropertyName = [indexedFieldUri substringToIndex:openIndex];
	if(self.fields != nil)
	{
		for(Field *local in self.fields)
		{
			NSString *localUri = local.uri;
			if([localUri hasPrefix:indexedPropertyName])
			{
				int localOpenIndex = [StringUtil lastIndexOf:localUri :@"["];
				int elementIndex = [self calculateArrayIndex:localUri];
				NSString *localIndexedPropertyName = [localUri substringToIndex:localOpenIndex];
				if(elementIndex > elementAt)
				{
					//Rename the uri
					int diffIndex = [indexedPropertyName length]+3;
					NSString *diff = [localUri substringFromIndex:diffIndex];
					NSString *uri = nil;
					
					if(![StringUtil isEmpty:diff])
					{
						int cour = elementIndex -1;
						NSString *newIndex = [NSString stringWithFormat:@"%d",cour];
						uri = [NSString stringWithFormat:@"%@[%@]%@",localIndexedPropertyName,
							   newIndex,diff];
					}
					else 
					{
						int cour = elementIndex -1;
						NSString *newIndex = [NSString stringWithFormat:@"%d",cour];
						uri = [NSString stringWithFormat:@"%@[%@]",localIndexedPropertyName,
							  newIndex];
					}
					
					local.uri = uri;
					
					int uriFromIndex = [StringUtil lastIndexOf:uri :@"/"]+1;
					local.name = [uri substringFromIndex:uriFromIndex];
				}
				else if(elementIndex == elementAt)
				{
					[fieldsToDelete addObject:local];
				}
				
			}
		}
	}
	
	if(fieldsToDelete != nil)
	{
		for(Field *local in fieldsToDelete)
		{
			[self.fields removeObject:local];
		}
		
		//Decrement the ArrayMetaData
		ArrayMetaData *local = [self findArrayMetaData:indexedPropertyName];
		int arrayLength = [local.arrayLength intValue];
		arrayLength--;
		local.arrayLength = [NSString stringWithFormat:@"%d",arrayLength];
	}	
}

-(NSDictionary *) getArrayElement:(NSString *)arrayUri :(int) elementIndex
{
	NSMutableDictionary *arrayElement = [NSMutableDictionary dictionary];
	
	arrayUri = [NSString stringWithFormat:@"%@%@",@"/",arrayUri];
	arrayUri = [StringUtil replaceAll:arrayUri :@"." :@"/"];
	NSArray *arrayElementFields = [self findArrayElementFields:arrayUri :elementIndex];
	
	if(arrayElementFields != nil)
	{
		for(Field *local in arrayElementFields)
		{
			NSString *localUri = local.uri;
			NSString *localName = local.name;
			
			//Strip out the arrayUri and just use the property value
			NSString *localArrayUri = [self calculateArrayUri:localUri];
			NSString *propertyUri = [localUri substringFromIndex:[localArrayUri length]];
			int index = [StringUtil indexOf:propertyUri :@"/"];			
			if(index != -1)
			{
				propertyUri = [propertyUri substringFromIndex:index];
			}
			else 
			{
				index = [StringUtil indexOf:localName :@"["];
				NSString *cour = [localName substringToIndex:index];
				propertyUri = [NSString stringWithFormat:@"/%@",cour];
			}
			[arrayElement setObject:local.value forKey:propertyUri];
		}
	}
	
	return [NSDictionary dictionaryWithDictionary:arrayElement];
}

-(void) addToArray:(NSString *) indexedPropertyName :(NSDictionary *) properties
{
	if(![indexedPropertyName hasPrefix:@"/"])
	{
		indexedPropertyName = [NSString stringWithFormat:@"%@%@",@"/",indexedPropertyName];
	}	
	indexedPropertyName = [StringUtil replaceAll:indexedPropertyName 
												:@"." :@"/"];
	
	if(properties == nil)
	{
		return;
	}
	
	int indexOfLastElement = [self findIndexValueInsertionPoint:indexedPropertyName];
	int index = 0;
	if(indexOfLastElement != -1)
	{
		Field *local = [self.fields objectAtIndex:indexOfLastElement];
		NSString *localUri = local.uri;
		index = [self calculateArrayIndex:localUri];
		index++;
	}
	
	//Insert this field at the bottom of the existing array
	NSArray *names = [properties allKeys];
	BOOL arrayCreated = NO;
	for(NSString *property in names)
	{
		NSString *value = (NSString *)[properties objectForKey:property];
		if([property hasPrefix:@"/"])
		{
			property = [property substringFromIndex:1];
		}
		
		if(index != 0)
		{
			indexOfLastElement = [self findIndexValueInsertionPoint:indexedPropertyName];
			NSString *localIndexedPropertyName = nil;
			Field *local = (Field *)[self.fields objectAtIndex:indexOfLastElement];
			NSString *localUri = local.uri;
			localIndexedPropertyName = [self calculateArrayUri:localUri];
			int insertionIndex = indexOfLastElement+1;
			
			Field *newField = [Field withInit];
			newField.name = property;
			newField.value = value;
			
			NSString *uri = nil;
			if(![StringUtil isEmpty:property])
			{
				uri = [NSString stringWithFormat:@"%@[%d]/%@",localIndexedPropertyName,
					   index,property];
			}
			else 
			{
				uri = [NSString stringWithFormat:@"%@[%d]",localIndexedPropertyName,index];
				int cour = [StringUtil lastIndexOf:uri :@"/"]+1;
				NSString *name = [uri substringFromIndex:cour];
				newField.name = name;
			}
			newField.uri = uri;
			
			if([self.fields count] > insertionIndex)
			{
				[self.fields replaceObjectAtIndex:insertionIndex withObject:newField];
			}
			else 
			{
				[self.fields addObject:newField];
			}
		}
		else 
		{
			//A brand new array is being created within the MobileObject
			Field *field = [Field withInit];
			
			NSString *uri = nil;
			if(![StringUtil isEmpty:property])
			{
				uri = [NSString stringWithFormat:@"%@[0]/%@",indexedPropertyName,
					   property];
			}
			else 
			{
				uri = [NSString stringWithFormat:@"%@[0]",indexedPropertyName];
			}
			int cour = [StringUtil lastIndexOf:uri :@"/"]+1;
			NSString *name = [uri substringFromIndex:cour];
			field.name = name;
			field.uri = uri;
			field.value = value;
			
			[self.fields addObject:field];
			
			if(!arrayCreated)
			{
				ArrayMetaData *metaData = [ArrayMetaData withInit];
				metaData.arrayUri = indexedPropertyName;
				metaData.arrayLength = @"0";
				[self.arrayMetaData addObject:metaData];
				arrayCreated = YES;
			}
		}
	}
	
	//Increment the array meta data length
	ArrayMetaData *local = [self findArrayMetaData:indexedPropertyName];
	int arrayLength = [local.arrayLength intValue];
	arrayLength++;
	local.arrayLength = [NSString stringWithFormat:@"%d",arrayLength];
}

-(void)addField:(Field *)field
{
	if(self.fields == nil)
	{
		self.fields = [NSMutableArray array];
	}
	[self.fields addObject:field];
}

-(void)addArrayMetaData:(ArrayMetaData *)local
{
	if(self.arrayMetaData == nil)
	{
		self.arrayMetaData = [NSMutableArray array];
	}
	[self.arrayMetaData addObject:local];
}
@end