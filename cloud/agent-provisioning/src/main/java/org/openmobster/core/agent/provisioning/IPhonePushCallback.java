/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.agent.provisioning;

import java.util.List;

import org.apache.log4j.Logger;

import org.openmobster.server.api.ExecutionContext;
import org.openmobster.server.api.service.Request;
import org.openmobster.server.api.service.Response;
import org.openmobster.server.api.service.MobileServiceBean;
import org.openmobster.server.api.service.ServiceInfo;

import org.openmobster.core.common.errors.ErrorHandler;
import org.openmobster.core.common.errors.SystemException;
import org.openmobster.core.security.device.Device;
import org.openmobster.core.security.device.DeviceController;
import org.openmobster.core.security.device.DeviceAttribute;

/**
 * @author openmobster@gmail.com
 */
@ServiceInfo(uri="iphone_push_callback")
public class IPhonePushCallback implements MobileServiceBean
{
	private static Logger log = Logger.getLogger(IPhonePushCallback.class);
	
	private DeviceController deviceController;
	
	public IPhonePushCallback()
	{
		
	}
			
	public DeviceController getDeviceController()
	{
		return deviceController;
	}



	public void setDeviceController(DeviceController deviceController)
	{
		this.deviceController = deviceController;
	}


	public void start()
	{
		log.info("-----------------------------------------------------------");
		log.info("IPhone Push Callback successfully started............");
		log.info("-----------------------------------------------------------");				
	}
	
	public Response invoke(Request request) 
	{
		Response response = new Response();
		try
		{
			String os = request.getAttribute("os");
			String deviceToken = request.getAttribute("deviceToken");
			String appId = request.getAttribute("appId");
			List<String> channels = request.getListAttribute("channels");
			
			log.debug("IPhonePushCallback--------------------------");
			log.debug("OS: "+os);
			log.debug("DeviceToken: "+deviceToken);
			log.debug("AppId: "+appId);
			if(channels != null)
			{
				for(String channel:channels)
				{
					log.debug("Channel: "+channel);
				}
			}
			log.debug("--------------------------------------------");
			
			return response;
		}
		catch(Exception e)
		{
			ErrorHandler.getInstance().handle(e);
			throw new SystemException(e.getMessage(), e);
		}
	}
}
