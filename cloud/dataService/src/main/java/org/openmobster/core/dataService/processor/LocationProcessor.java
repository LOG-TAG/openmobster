/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.dataService.processor;

import org.apache.log4j.Logger;

import org.openmobster.core.mobileContainer.Invocation;
import org.openmobster.core.mobileContainer.InvocationResponse;
import org.openmobster.core.mobileContainer.MobileContainer;

import org.openmobster.cloud.api.location.Response;
import org.openmobster.cloud.api.location.Request;

/**
 *
 * @author openmobster@gmail.com
 */
public class LocationProcessor implements Processor
{
	private static Logger log = Logger.getLogger(LocationProcessor.class);
	
	private String id;
	private MobileContainer mobileContainer;
	
	public LocationProcessor()
	{
		
	}
	
	public void start()
	{
		
	}
	
	public void stop()
	{
		
	}
	
	public void setId(String id)
	{
		this.id = id;
	}
	
	
	public MobileContainer getMobileContainer()
	{
		return mobileContainer;
	}

	public void setMobileContainer(MobileContainer mobileContainer)
	{
		this.mobileContainer = mobileContainer;
	}
	//----------------------------------------------------------------------------------------------------------
	@Override
	public String getId()
	{
		return this.id;
	}

	@Override
	public String process(Input input) throws ProcessorException
	{
		try
		{
			String payload = input.getMessage().trim();
			Request locationRequest = this.parseRequest(payload);
			
			Invocation invocation = Invocation.getInstance();
			invocation.setServiceUrl("/service/location/invoke");
			invocation.setLocationRequest(locationRequest);
			
			InvocationResponse response = this.mobileContainer.invoke(invocation);
			if(response == null)
			{
				throw new ProcessorException("LocationServiceBean Invocation Failure");
			}
			
			Response locationResponse = response.getLocationResponse();
			if(!response.getStatus().trim().equals(InvocationResponse.STATUS_SUCCESS))
			{
				throw new ProcessorException("LocationServiceBean Invocation Status="+response.getStatus());
			}
			
			String jsonResponse = this.prepareResponse(locationResponse);
			
			return jsonResponse;
		}
		catch(Exception e)
		{
			log.error(this, e);
			throw new ProcessorException(e);
		}
	}
	
	private Request parseRequest(String payload)
	{
		return null;
	}
	
	private String prepareResponse(Response response)
	{
		return null;
	}
	//----------------------------------------------------------------------------------------------
}
