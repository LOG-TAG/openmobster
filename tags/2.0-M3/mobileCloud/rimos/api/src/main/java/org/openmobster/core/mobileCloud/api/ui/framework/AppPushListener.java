/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.api.ui.framework;

import org.openmobster.core.mobileCloud.api.push.MobilePush;
import org.openmobster.core.mobileCloud.api.push.PushListener;
import org.openmobster.core.mobileCloud.api.model.MobileBeanMetaData;

/**
 * @author openmobster@gmail
 *
 */
public final class AppPushListener implements PushListener
{
	private static AppPushListener singleton;
	
	private long updateCounter;
	private PushNotificationHandler handler;
	
	private AppPushListener()
	{
		
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
		}
	}
	
	public synchronized void clearNotification()
	{
		this.updateCounter = 0;
		this.handler.clearNotification();
	}
}
