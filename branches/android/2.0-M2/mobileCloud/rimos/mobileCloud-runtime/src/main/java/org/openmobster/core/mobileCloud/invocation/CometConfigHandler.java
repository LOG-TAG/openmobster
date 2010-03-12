/**
 * Copyright (c) {2003,2009} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.invocation;

import org.openmobster.core.mobileCloud.rimos.module.bus.Bus;
import org.openmobster.core.mobileCloud.rimos.module.bus.Invocation;
import org.openmobster.core.mobileCloud.rimos.module.bus.InvocationHandler;
import org.openmobster.core.mobileCloud.rimos.module.bus.InvocationResponse;
import org.openmobster.core.mobileCloud.rimos.module.connection.NotificationListener;
import org.openmobster.core.mobileCloud.rimos.configuration.Configuration;

import org.openmobster.core.mobileCloud.rimos.service.Service;
import org.openmobster.core.mobileCloud.rimos.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.rimos.errors.SystemException;

/**
 * @author openmobster@gmail
 *
 */
public final class CometConfigHandler extends Service implements InvocationHandler 
{
	public CometConfigHandler()
	{
		
	}
	
	public void start()
	{
		try
		{
			Bus.getInstance().register(this);
		}
		catch(Exception e)
		{
			throw new SystemException(this.getClass().getName(), "start", new Object[]{e.getMessage()});
		}
	}
	
	public void stop()
	{
		
	}
	//-----------------------------------------------------------------------------------------------------------------------
	public String getUri()
	{
		return this.getClass().getName();
	}
	
	public InvocationResponse handleInvocation(Invocation invocation)
	{
		try
		{
			Configuration configuration = Configuration.getInstance();
			
			String cometMode = invocation.getValue("mode");
			String poll_interval = invocation.getValue("poll_interval");
			
			if(cometMode.equalsIgnoreCase("push"))
			{
				//Push Mode
				configuration.setCometMode("push");
			}
			else if(cometMode.equalsIgnoreCase("poll"))
			{
				//Poll Mode
				configuration.setCometMode("poll");
				
				if(poll_interval != null && poll_interval.trim().length()>0)
				{
					configuration.setCometPollInterval(Long.parseLong(poll_interval));
				}
				else
				{
					configuration.setCometPollInterval(5000); //some system default value should be used
					//for now, just use every 15 minutes
				}
			}
			configuration.save();
			
			//Restart the NotificationListener
			NotificationListener notify = NotificationListener.getInstance();
			if(notify != null)
			{
				notify.restart();
			}
			
			return null;
		}
		catch(Exception e)
		{
			ErrorHandler.getInstance().handle(new SystemException(
					this.getClass().getName(), "handleInvocation", new Object[]{
						"Comet Mode to Switch To="+invocation.getValue("mode"),												
						"Exception="+e.toString(),
						"Message="+e.getMessage()
					} 
			));
			return null;
		}
	}
}
