/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

#import "CloudPayload.h"

NSString *const _SERVICE_PAYLOAD_ACTIVE = @"<request><header><name>device-id</name><value><![CDATA[%@]]></value></header><header><name>nonce</name><value><![CDATA[%@]]></value></header><header><name>processor</name><value>mobileservice</value></header></request>";
NSString *const _SERVICE_PAYLOAD_INACTIVE = @"<request><header><name>processor</name><value>mobileservice</value></header></request>";
NSString *const _SYNC_PAYLOAD = @"<request><header><name>device-id</name><value><![CDATA[%@]]></value></header><header><name>nonce</name><value><![CDATA[%@]]></value></header><header><name>processor</name><value>sync</value></header></request>";


/**
 * 
 * @author openmobster@gmail.com
 */
@implementation CloudPayload

@end
