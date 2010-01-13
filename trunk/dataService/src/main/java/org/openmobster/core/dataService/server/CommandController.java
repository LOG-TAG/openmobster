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
		}
	}		
}
