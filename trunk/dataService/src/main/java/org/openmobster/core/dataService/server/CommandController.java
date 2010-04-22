/**
 * Copyright (c) {2003,2009} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.dataService.server;

import java.util.StringTokenizer;

import org.apache.mina.common.IoSession;

import org.openmobster.core.dataService.Constants;
import org.openmobster.core.dataService.comet.CometSessionManager;
import org.openmobster.core.dataService.comet.CometSession;

import org.openmobster.core.services.subscription.SubscriptionManager;

/**
 * @author openmobster@gmail.com
 */
public class CommandController 
{
	private CometSessionManager cometSessionManager;
	
	public CommandController()
	{
		
	}
	
	public void start()
	{
		
	}
	
	public void stop()
	{
		
	}
			
	public CometSessionManager getCometSessionManager() 
	{
		return cometSessionManager;
	}

	public void setCometSessionManager(CometSessionManager cometSessionManager) 
	{
		this.cometSessionManager = cometSessionManager;
	}	
	//---------------------------------------------------------------------------------------------------------------------
	public void execute(IoSession session, String payload) throws Exception
	{						
		StringTokenizer st = new StringTokenizer(payload, Constants.separator);
		
		while(st.hasMoreTokens())
		{
			String token = st.nextToken();
			
			int index = token.indexOf('=');
			String name = token.substring(0, index).trim();
			String value = token.substring(index+1).trim();
			
			session.setAttribute(name, value);
			
			if(name.equals(Constants.command) && value.equals(Constants.notify))
			{
				session.setAttribute(Constants.notify, Boolean.TRUE);				
				
				//Activate a Comet Session associated with this device
				SubscriptionManager subscriptionMgr = (SubscriptionManager)session.getAttribute(Constants.subscription);
				cometSessionManager.activate(subscriptionMgr.getSubscription().getClientId(), 
				session);
			}			
			else if(name.equals("channel"))
			{
				StringTokenizer channels = new StringTokenizer(value, "|");
				SubscriptionManager manager = (SubscriptionManager)session.getAttribute(Constants.subscription);
				while(channels.hasMoreTokens())
				{
					String channel = channels.nextToken().trim();
					manager.addMyChannel(channel);
				}
			}
			else if(name.equals("platform"))
			{
				session.setAttribute("platform", value);
				
			}
			else if(name.equals("device"))
			{
				session.setAttribute("device", value);
				
				String platform = (String)session.getAttribute("platform");
				long keepAliveInterval = this.computeKeepAliveInterval(platform, value);
				
				//Start the KeepAliveDaemon for the active push session
				SubscriptionManager subscriptionMgr = (SubscriptionManager)session.getAttribute(Constants.subscription);
				String deviceId = subscriptionMgr.getSubscription().getClientId();
				CometSession cometSession = cometSessionManager.findCometSession(deviceId);
				
				if(cometSession != null)
				{
					cometSession.startKeepAliveDaemon(keepAliveInterval);
				}
			}
		}
	}
	//-----------------------------------------------------------------------------------------------------------------------------------------
	private long computeKeepAliveInterval(String platform, String device)
	{
		long keepAliveInterval = 90000; //waits 90 seconds...through experimentation this has very little impact on the device battery
		
		//This customization is needed because due to a TCP stack issue shipped with
		//the 833x blackberry devices, the default READ_WRITE timeout of 2 minutes does
		//not in fact hold. After lots of trial and error, 55 seconds turns out to be 
		//the best option for keep alive interval. After running it on an actual device
		//even with 55 seconds, the device's battery is not impacted too much
		if(platform.equals("blackberry") && device.startsWith("833"))
		{
			keepAliveInterval = 55000;
		}
		
		return keepAliveInterval;
	}
}
