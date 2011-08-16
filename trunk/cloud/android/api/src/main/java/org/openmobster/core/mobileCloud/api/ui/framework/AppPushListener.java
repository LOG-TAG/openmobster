/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.api.ui.framework;

import java.util.List;
import java.util.ArrayList;

import org.openmobster.android.api.sync.MobileBeanMetaData;
import org.openmobster.core.mobileCloud.push.MobilePush;
import org.openmobster.core.mobileCloud.push.PushListener;

/**
 * @author openmobster@gmail
 *
 */
public final class AppPushListener implements PushListener
{
	private static AppPushListener singleton;
	
	private long updateCounter;
	private List<MobilePush> pushQueue;
	
	private PushNotificationHandler handler;
	
	private AppPushListener()
	{
		this.pushQueue = new ArrayList<MobilePush>();
	}
	
	public static AppPushListener getInstance()
	{
		if(AppPushListener.singleton == null)
		{
			synchronized(AppPushListener.class)
			{
				if(AppPushListener.singleton == null)
				{
					AppPushListener.singleton = new AppPushListener();
				}
			}
		}
		return AppPushListener.singleton;
	}
	
	public void start()
	{
		MobilePush.registerPushListener(this);
		
		//TODO: in a later release make this pluggable so the push notification behavior can be configured
		//by the App Developer
		((CorePushNotificationHandler)this.handler).start();
	}
	
	public PushNotificationHandler getHandler()
	{
		return handler;
	}

	public void setHandler(PushNotificationHandler handler)
	{
		this.handler = handler;
	}
	//-------------------------------------------------------------------------------------------------------------------------------------------
	public synchronized void receivePush(MobilePush push)
	{	
		MobileBeanMetaData[] pushData = push.getPushData();
		if(pushData != null && pushData.length > 0)
		{
			this.updateCounter += pushData.length;
			push.setNumberOfUpdates(this.updateCounter);
			this.handler.receiveNotification(push);
			this.pushQueue.add(push);
		}
	}
	
	public synchronized void clearNotification()
	{
		this.updateCounter = 0;
		this.handler.clearNotification();
		this.pushQueue.clear();
	}
	
	public MobilePush getPush()
	{
		MobilePush push = null;
		
		if(this.pushQueue != null && !this.pushQueue.isEmpty())
		{
			List<MobileBeanMetaData> pushMetaData = new ArrayList<MobileBeanMetaData>();
			for(MobilePush local:this.pushQueue)
			{
				MobileBeanMetaData[] localMeta = local.getPushData();
				if(localMeta != null)
				{
					for(MobileBeanMetaData cour: localMeta)
					{
						pushMetaData.add(cour);
					}
				}
			}
			
			if(!pushMetaData.isEmpty())
			{
				push = new MobilePush(pushMetaData.toArray(new MobileBeanMetaData[0]));
				push.setNumberOfUpdates(pushMetaData.size());
			}
		}
		
		return push;
	}
}
