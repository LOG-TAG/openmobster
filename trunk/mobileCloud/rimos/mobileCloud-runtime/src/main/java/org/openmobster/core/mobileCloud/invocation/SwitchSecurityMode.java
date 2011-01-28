/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
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

import org.openmobster.core.mobileCloud.rimos.service.Service;
import org.openmobster.core.mobileCloud.rimos.configuration.Configuration;
import org.openmobster.core.mobileCloud.rimos.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.rimos.errors.SystemException;

/**
 * @author openmobster@gmail
 *
 */
public final class SwitchSecurityMode extends Service implements InvocationHandler 
{
	public SwitchSecurityMode()
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
			if(configuration.isSSLActivated())
			{
				configuration.deActivateSSL();
			}
			else
			{
				configuration.activateSSL();
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
						"Exception="+e.toString(),
						"Message="+e.getMessage()
					} 
			));
			return null;
		}
	}
}
