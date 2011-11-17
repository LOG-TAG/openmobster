/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.android.api.location;

import org.openmobster.core.mobileCloud.android.configuration.Configuration;
import org.openmobster.core.mobileCloud.android.module.connection.NetSession;
import org.openmobster.core.mobileCloud.android.module.connection.NetworkConnector;
import org.openmobster.core.mobileCloud.android.service.Registry;

import org.openmobster.api.service.location.PayloadHandler;

import android.content.Context;

/**
 *
 * @author openmobster@gmail.com
 */
public final class LocationService
{
	public LocationContext invoke(Request request,LocationContext locationContext) 
		throws LocationServiceException
	{
		NetSession session = null;
		try
		{
			Context context = Registry.getActiveInstance().getContext();
			Configuration configuration = Configuration.getInstance(context);
			boolean secure = configuration.isSSLActivated();
			session = NetworkConnector.getInstance().openSession(secure);
			
			String sessionInitPayload = null;
			if(configuration.isActive())
			{
				String deviceId = configuration.getDeviceId();
				String authHash = configuration.getAuthenticationHash();
				sessionInitPayload = 
					"<request>" +
						"<header>" +
							"<name>device-id</name>"+
							"<value><![CDATA["+deviceId+"]]></value>"+
						"</header>"+
						"<header>" +
							"<name>nonce</name>"+
							"<value><![CDATA["+authHash+"]]></value>"+
						"</header>"+
						"<header>" +
							"<name>processor</name>"+
							"<value>org.openmobster.core.dataService.processor.LocationProcessor</value>"+
						"</header>"+
					"</request>";
			}
			else
			{
				sessionInitPayload = 
					"<request>" +
						"<header>" +
							"<name>processor</name>"+
							"<value>org.openmobster.core.dataService.processor.LocationProcessor</value>"+
						"</header>"+
					"</request>";
			}
			
			String response = session.sendTwoWay(sessionInitPayload);
			
			LocationContext responseContext = null;
			if(response.indexOf("status=200")!=-1)
			{
				PayloadHandler payloadHandler = new PayloadHandler();
				locationContext.setAttribute("request", request);
				
				String xml = payloadHandler.serializeRequest(locationContext);
				
				response = session.sendPayloadTwoWay(xml);
				
				responseContext = payloadHandler.deserializeResponse(response);
			}
			
			return responseContext;
		}	
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			LocationServiceException exception = new LocationServiceException(this.getClass().getName(),"invoke", new Object[]{
				"Message: "+e.getMessage(),
				"ToString: "+e.toString()
			});
			throw exception;
		}
		finally
		{
			if(session != null)
			{
				session.close();
			}
		}
	}
}
