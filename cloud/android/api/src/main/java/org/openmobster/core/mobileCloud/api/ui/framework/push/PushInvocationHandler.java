/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.api.ui.framework.push;

import java.util.Map;

import org.openmobster.android.api.rpc.MobileService;
import org.openmobster.android.api.rpc.Request;
import org.openmobster.android.api.rpc.Response;
import org.openmobster.core.mobileCloud.api.ui.framework.AppConfig;

import org.openmobster.core.mobileCloud.android.module.bus.Bus;
import org.openmobster.core.mobileCloud.android.module.bus.Invocation;
import org.openmobster.core.mobileCloud.android.module.bus.InvocationHandler;
import org.openmobster.core.mobileCloud.android.module.bus.InvocationResponse;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android.service.Service;
import org.openmobster.core.mobileCloud.android.util.GeneralTools;
import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.command.AppException;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;



/**
 * @author openmobster@gmail.com
 *
 */
public class PushInvocationHandler extends Service implements InvocationHandler
{
	public PushInvocationHandler()
	{
		
	}	
	
	public void start() 
	{
		try
		{
			Bus.getInstance().register(this);
			
			//make an Android Push Registration callback
			try
			{
				//Populate the Cloud Request
				Request request = new Request("android_push_callback");	
				request.setAttribute("app-id", Registry.getActiveInstance().getContext().getPackageName());
				
				new MobileService().invoke(request);
			}
			catch(Exception e)
			{
				//Record this error in the Cloud Error Log
				ErrorHandler.getInstance().handle(e);
			}
		}
		catch(Exception e)
		{
			throw new SystemException(this.getClass().getName(), "start", new Object[]{e.getMessage()});
		}
	}
	
	public void stop() 
	{		
	}
	//-------------------------------------------------------------------------------------------------------------------------------------------
	public String getUri() 
	{	
		return this.getClass().getName();
	}

	public InvocationResponse handleInvocation(Invocation invocation) 
	{
		try
		{
			String message = invocation.getValue("message");
			String title = invocation.getValue("title");
			String detail = invocation.getValue("detail");
			
			Class activityClass = Services.getInstance().getCurrentActivityClass();
			if(activityClass == null)
			{
				return null;
			}
			
			Context context = Registry.getActiveInstance().getContext();
			PackageManager pm = context.getPackageManager();
			CharSequence appName = pm.getApplicationLabel(context.getApplicationInfo());
			
			//Get the NotificationManager service
			String ns = Context.NOTIFICATION_SERVICE;
			NotificationManager notificationManager = (NotificationManager)context.getSystemService(ns);
			
			//Setup the Notification instance
			int icon = this.findDrawableId(context, "push");
			long when = System.currentTimeMillis();
			Notification notification = new Notification(icon, appName, when);
			
			//Setup the intent for this notification
			CharSequence contentText = message;
			
			Intent notificationIntent = new Intent(context, activityClass);
			notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			notificationIntent.setAction(Intent.ACTION_VIEW);
			notificationIntent.putExtra("detail", detail);
			
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
			ErrorHandler.getInstance().handle(new SystemException(
					this.getClass().getName(), "handleInvocation", new Object[]{						
						"Exception="+e.toString(),
						"Message="+e.getMessage()
					} 
			));
		}
		return null;
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
