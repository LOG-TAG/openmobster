/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.api.system;

import java.util.Vector;

import org.openmobster.core.mobileCloud.api.ui.framework.AppConfig;
import org.openmobster.core.mobileCloud.rimos.configuration.Configuration;
import org.openmobster.core.mobileCloud.rimos.module.bus.Bus;
import org.openmobster.core.mobileCloud.rimos.module.bus.BusException;
import org.openmobster.core.mobileCloud.rimos.module.bus.Invocation;

/**
 * @author openmobster@gmail
 *
 */
public final class CometUtil 
{
	public static boolean subscribeChannels() throws BusException
	{
		boolean wasChannelBootupStarted = false;
		
		Configuration configuration = Configuration.getInstance();
		if(!configuration.isActive())
		{
			return false;
		}
		
		Vector channels = AppConfig.getInstance().getChannels();
		boolean newAdded = false;
		if(channels != null && !channels.isEmpty())
		{
			int size = channels.size();
			for(int i=0; i<size; i++)
			{
				String channel = (String)channels.elementAt(i);
				boolean cour = configuration.addMyChannel(channel);
				if(!newAdded && cour)
				{
					newAdded = true;
				}
			}
			configuration.save();
			
			//If this channel is newly subscribed on this device, then recycle the comet system
			if(newAdded)
			{
				CometUtil.performChannelBootup(configuration);
				wasChannelBootupStarted = true;
			}
		}				
		
		return wasChannelBootupStarted;
	}
	
	private static synchronized void performChannelBootup(Configuration configuration)
	{
		//Execute this in a background thread...holds up the App launch
		Thread thread = new Thread(new Runnable(){
			public void run()
			{
				try
				{					
					Invocation invocation = new Invocation("org.openmobster.core.mobileCloud.invocation.ChannelBootupHandler");
					Bus.getInstance().invokeService(invocation);				
				}
				catch(Exception be)
				{
					//nothing you can do here
				}
			}
		});
		thread.start();
	}
}
