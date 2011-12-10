/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.api.ui.framework.push;

import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.util.GeneralTools;
import org.openmobster.core.mobileCloud.android.configuration.AppSystemConfig;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;

/**
 *
 * @author openmobster@gmail.com
 */
public class PushBroadcastReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{	
		try
		{
			//Some Bootstrapping
			if(!AppSystemConfig.getInstance().isActive())
			{
				AppSystemConfig.getInstance().start();
			}
			
			String message = intent.getStringExtra("message");
			String title = intent.getStringExtra("title");
			String detail = intent.getStringExtra("detail");
			
			//This should be part of configuring the Push Service, in openmobster-app.xml 
			String launchActivity = AppSystemConfig.getInstance().getPushLaunchActivityClassName();
			String pushIcon =  AppSystemConfig.getInstance().getPushIconName();
			
			Class activityClass = null;
			if(launchActivity != null && launchActivity.trim().length() > 0)
			{
				try
				{
					activityClass = Class.forName(launchActivity);
				}
				catch(Exception e)
				{
					activityClass = null;
				}
			}
			
			if(pushIcon == null || pushIcon.trim().length() == 0)
			{
				pushIcon = "appicon";
			}
			
			PackageManager pm = context.getPackageManager();
			CharSequence appName = pm.getApplicationLabel(context.getApplicationInfo());
			
			//Get the NotificationManager service
			String ns = Context.NOTIFICATION_SERVICE;
			NotificationManager notificationManager = (NotificationManager)context.getSystemService(ns);
			
			//Setup the Notification instance
			int icon = this.findDrawableId(context, pushIcon);
			long when = System.currentTimeMillis();
			Notification notification = new Notification(icon, appName, when);
			
			//Setup the intent for this notification
			CharSequence contentText = message;
			
			Intent notificationIntent = null;
			if(activityClass != null)
			{
				notificationIntent = new Intent(context, activityClass);
				notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				notificationIntent.setAction(Intent.ACTION_VIEW);
				notificationIntent.putExtra("detail", detail);
			}
			
			PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
			notification.setLatestEventInfo(context, appName, contentText, contentIntent);
			
			//Notification Flags
			notification.flags = Notification.FLAG_AUTO_CANCEL;
			notification.defaults |= Notification.DEFAULT_LIGHTS;
			notification.defaults |= Notification.DEFAULT_SOUND;
			
			notificationManager.notify(GeneralTools.generateUniqueId().hashCode(), 
			notification);
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			ErrorHandler.getInstance().handle(new SystemException(
					this.getClass().getName(), "onReceive", new Object[]{						
						"Exception="+e.toString(),
						"Message="+e.getMessage()
					} 
			));
		}
	}
	
	private int findDrawableId(Context context, String variable)
	{
		try
		{
			Resources resources = context.getResources();
			int resourceId = resources.getIdentifier(variable, "drawable", context.getPackageName());
			
			return resourceId;
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			return 0;
		}
	}
}
