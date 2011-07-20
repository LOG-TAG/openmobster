/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "ArrayMetaData.h"


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation ArrayMetaData

@synthesize arrayUri;
@synthesize arrayLength;
@synthesize arrayClass;

-(void)dealloc
{
	[arrayUri release];
	[arrayLength release];
	[arrayClass release];
	[super dealloc];
}

+(id) withInit
{
	return [[[ArrayMetaData alloc] init] autorelease];
}

+(id) withInit:(NSString *)arrayUri arrayLength:(NSString *)arrayLength arrayClass:(NSString *)arrayClass
{
	ArrayMetaData *metadata = [ArrayMetaData withInit];
	
	metadata.arrayUri = arrayUri;
	metadata.arrayLength = arrayLength;
	metadata.arrayClass = arrayClass;
	
	return metadata;
}
@end
