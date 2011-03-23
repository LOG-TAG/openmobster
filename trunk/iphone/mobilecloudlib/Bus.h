/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "Registry.h"
#import "Service.h"
#import "Configuration.h"
#import "StringUtil.h"

/**
 * 
 * @author openmobster@gmail.com
 */
@interface Bus : Service<NSXMLParserDelegate> 
{
	@private
	UIPasteboard *sharedConf;
	
	Configuration *sharedInstance;
	Channel *sharedChannel;
	NSMutableString *fullPath;
	NSMutableString *dataBuffer;
}

+(Bus *)getInstance;

-(void)synchronizeConf;

//Used internally
-(void)replaceWithSharedConf:(NSString *)confXml;
-(void)postSharedConf:(Configuration *)conf;
-(NSString *)confToXml:(Configuration *)conf;
-(void)xmlToConf:(NSString *)xml;
@end
