/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.device.comet;

import org.apache.log4j.Logger;

import org.openmobster.cloud.api.ExecutionContext;
import org.openmobster.cloud.api.push.PushService;
import org.openmobster.cloud.api.service.MobileServiceBean;
import org.openmobster.cloud.api.service.Request;
import org.openmobster.cloud.api.service.Response;
import org.openmobster.cloud.api.service.ServiceInfo;
import org.openmobster.core.security.device.Device;


/**
 * Service Bean that will be invoked from the device. It returns the "Email" selected for viewing by the user
 * 
 * @author openmobster@gmail.com
 */
@ServiceInfo(uri="/test/start/push")
public class StartPush implements MobileServiceBean
{
	private static Logger log = Logger.getLogger(StartPush.class);
	
	public StartPush()
	{
		
	}
	
	public void start()
	{
		log.info("--------------------------------------------------------------------------");
		log.info("/test/start/push: was successfully started................................");
		log.info("--------------------------------------------------------------------------");
	}
	
	public Response invoke(Request request) 
	{	
		Response response = new Response();
		
		Device device = ExecutionContext.getInstance().getDevice();
		PushService pushService = PushService.getInstance();
		pushService.push(device.getIdentity().getPrincipal(), null, "Hello From Push", "Title", "Details");
		
		return response;
	}
}
