/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.android_native.framework;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.Vector;

import org.openmobster.android.api.sync.MobileBean;
import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.module.bus.Bus;
import org.openmobster.core.mobileCloud.android.module.bus.Invocation;
import org.openmobster.core.mobileCloud.android.module.bus.SyncInvocation;
import org.openmobster.core.mobileCloud.api.ui.framework.AppConfig;

import system.CometUtil;

/**
 *
 * @author openmobster@gmail.com
 */
public final class BackgroundSync extends TimerTask
{
	public void run()
	{
		try
		{
			//Subscribe to channels
	    	CometUtil.subscribeChannels();
	    	
			//Perfom boot-sync on the unbooted channels
			Invocation invocation = new Invocation("org.openmobster.core.mobileCloud.android.invocation.ChannelBootupHandler");
			invocation.setValue("push-restart-cancel", ""+Boolean.FALSE);
			Bus.getInstance().invokeService(invocation);
			
			//Get the non-booted but active channels with a two-way sync
			List<String> channelsToSync = this.findTwoWaySyncChannels();
			if(channelsToSync != null && !channelsToSync.isEmpty())
			{
				for(String channel:channelsToSync)
				{
					//start the app session with a two way sync
					SyncInvocation syncInvocation = new SyncInvocation(
					"org.openmobster.core.mobileCloud.android.invocation.SyncInvocationHandler", 
					SyncInvocation.twoWay, channel);
					syncInvocation.deactivateBackgroundSync(); //so that there are no push notifications...just a quiet sync
					Bus.getInstance().invokeService(syncInvocation);
				}
			}
			
			//Do a proxy sync on all the channels
			SyncInvocation syncInvocation = new SyncInvocation(
					"org.openmobster.core.mobileCloud.android.invocation.SyncInvocationHandler", 
			SyncInvocation.proxySync);
			syncInvocation.deactivateBackgroundSync(); //so that there are no push notifications...just a quiet sync
			Bus.getInstance().invokeService(syncInvocation);
		}
		catch(Throwable t)
		{
			SystemException syse = new SystemException(this.getClass().getName(),"run",new Object[]{
				"Exception: "+t.toString(),
				"Message: "+t.getMessage()
			});
			ErrorHandler.getInstance().handle(syse);
		}
		finally
		{
			//makes sure this task does not execute anymore
			this.cancel();
		}
	}
	
	private List<String> findTwoWaySyncChannels()
	{
		List<String> channelsToSync = new ArrayList<String>();
		
		AppConfig appConfig = AppConfig.getInstance();
		Vector appChannels = appConfig.getChannels();
		if(appChannels != null)
		{
			int size = appChannels.size();
			for(int i=0; i<size; i++)
			{
				String channel = (String)appChannels.get(i);
				
				if(MobileBean.isBooted(channel))
				{
					channelsToSync.add(channel);
				}
			}
		}
		
		return channelsToSync;
	}
}
