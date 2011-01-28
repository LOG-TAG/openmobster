/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.invocation;

import java.util.Vector;

import org.openmobster.core.mobileCloud.rimos.module.bus.Bus;
import org.openmobster.core.mobileCloud.rimos.module.bus.Invocation;
import org.openmobster.core.mobileCloud.rimos.module.bus.InvocationHandler;
import org.openmobster.core.mobileCloud.rimos.module.bus.InvocationResponse;
import org.openmobster.core.mobileCloud.rimos.module.connection.NotificationListener;
import org.openmobster.core.mobileCloud.rimos.module.mobileObject.MobileObjectDatabase;
import org.openmobster.core.mobileCloud.rimos.module.sync.SyncException;
import org.openmobster.core.mobileCloud.rimos.module.sync.SyncService;


import org.openmobster.core.mobileCloud.rimos.service.Service;
import org.openmobster.core.mobileCloud.rimos.configuration.Configuration;
import org.openmobster.core.mobileCloud.rimos.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.rimos.errors.SystemException;

/**
 * @author openmobster@gmail
 *
 */
public final class ChannelBootupHandler extends Service implements InvocationHandler 
{
	public ChannelBootupHandler()
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
			//Bootup any nonn-booted registered channels
			this.bootupChannels();
			
			//Recycle the Comet Daemon so the newly added channels will start receiving comet push
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
	
	private void bootupChannels() throws SyncException
	{
		Configuration conf = Configuration.getInstance();
		Vector myChannels = conf.getMyChannels();
		
		if(myChannels != null && !myChannels.isEmpty())
		{
			int size = myChannels.size();
			for(int i=0;i<size; i++)
			{
				String channel = (String)myChannels.elementAt(i);
				if(!this.isBooted(channel))
				{
					//false because it does not need to send any push related notifications
					//just load the data silently and be done with it
					SyncService.getInstance().performBootSync(channel, channel, false);
				}
			}
		}
	}
	
	private boolean isBooted(String channel)
	{
		MobileObjectDatabase deviceDB = MobileObjectDatabase.getInstance();
		Vector allObjects = deviceDB.readAll(channel);
		
		return (allObjects !=null && !allObjects.isEmpty());
	}
}
