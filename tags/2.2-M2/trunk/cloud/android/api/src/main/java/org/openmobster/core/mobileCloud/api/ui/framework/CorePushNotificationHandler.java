/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.api.ui.framework;

import java.lang.reflect.Field;

import org.openmobster.core.mobileCloud.api.push.MobilePush;
import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.service.Registry;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

/**
 * @author openmobster@gmail
 *
 */
public final class CorePushNotificationHandler implements PushNotificationHandler
{
	public CorePushNotificationHandler()
	{
	}
	
	public void start()
	{
	}
	
	public void receiveNotification(final MobilePush newPushInstance)
	{					
		try
		{
			//System.out.println("App Level Push--------------------------------------");
			//System.out.println("Push received.........................");
			//System.out.println("----------------------------------------------------");
			
			this.handleSystemNotification(newPushInstance);
		}
		catch(Exception e)
		{
			//e.printStackTrace(System.out);
			
			//if this fails...app should not fail...Life still goes on
			ErrorHandler.getInstance().handle(new SystemException(this.getClass().getName(), "receiveNotification", new Object[]{
				"Message:"+e.getMessage(),
				"Exception:"+e.toString()
			}));
		}
	}
	
	public void clearNotification()
	{
		//Not needed in Android...one of several awesomeness of Android over BlackBerry platform!!!
	}
	
	private void handleSystemNotification(final MobilePush newPushInstance) throws Exception
	{
		Activity activity = (Activity)Registry.getActiveInstance().getContext();
		String appPackage = activity.getPackageName();
		PackageManager pm = activity.getPackageManager();
		CharSequence appName = pm.getApplicationLabel(activity.getApplicationInfo());
		
		//Get the NotificationManager service
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager notificationManager = (NotificationManager)activity.getSystemService(ns);
		
		//Setup the Notification instance
		int icon = this.findDrawableId((Activity)activity, "push");
		long when = System.currentTimeMillis();
		Notification notification = new Notification(icon, appName, when);
		
		//Setup the intent for this notification
		CharSequence contentText = ""+ newPushInstance.getNumberOfUpdates()+" Updates";
		
		Intent notificationIntent = new Intent(activity, activity.getClass());
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		notificationIntent.setAction(Intent.ACTION_VIEW);
		notificationIntent.putExtra("push", Boolean.TRUE);
		
		PendingIntent contentIntent = PendingIntent.getActivity(activity, 0, notificationIntent, 0);
		notification.setLatestEventInfo(activity, appName, contentText, contentIntent);
		
		//Notification Flags
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.defaults |= Notification.DEFAULT_LIGHTS;
		notification.defaults |= Notification.DEFAULT_SOUND;
		
		notificationManager.notify(appPackage.hashCode(), 
		notification);
		
		//System.out.println("Starting Notification----------------------------------------");
		//System.out.println("Push Updates: "+newPushInstance.getNumberOfUpdates());
		//MobileBeanMetaData[] updates = newPushInstance.getPushData();
		//if(updates != null)
		//{
		//	for(MobileBeanMetaData update:updates)
		//	{
		//		System.out.println("Bean: "+update.getId());
		//	}
		//}
		//System.out.println("--------------------------------------------------------------");
	}
	//---------------------------------------------------------------------------------------------------------------------------------------------------
	private int findDrawableId(Activity activity, String variable)
	{
		try
		{
			String idClass = activity.getPackageName()+".R$drawable";
			Class clazz = Class.forName(idClass);
			Field field = clazz.getField(variable);
			
			return field.getInt(clazz);
		}
		catch(Exception e)
		{
			return -1;
		}
	}
}
