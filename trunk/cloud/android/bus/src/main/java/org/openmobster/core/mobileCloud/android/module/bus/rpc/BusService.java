/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.module.bus.rpc;

import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android.util.GeneralTools;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.IBinder;

/**
 * @author openmobster@gmail.com
 *
 */
public class BusService extends Service
{
	private IBinder binder;
	
	public BusService()
	{
		
	}
	
	
		
	@Override
	public void onCreate()
	{
		super.onCreate();
		//this.startForegroundRegistration();
		
		/*try
		{
			super.onCreate();		
			
			if(!Registry.isActive())
			{
				Object deviceContainer = this.getDeviceContainer();			
				Method startup = deviceContainer.getClass().getDeclaredMethod("startup", 
				null);
				startup.invoke(deviceContainer, null);
			}
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}*/
	}
	
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		/*try
		{						
			if(Registry.isActive())
			{
				Context context = Registry.getActiveInstance().getContext();
				
				if(context == this)
				{
					Object deviceContainer = this.getDeviceContainer();			
					Method shutdown = deviceContainer.getClass().getDeclaredMethod("shutdown", 
					null);
					shutdown.invoke(deviceContainer, null);
				}
			}
			
			super.onDestroy();
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}*/
	}



	@Override
	public IBinder onBind(Intent intent)
	{
		try
		{
			if(this.binder == null)
			{
				this.binder = new BusBinder();
			}
			return this.binder;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/*private Object getDeviceContainer() throws Exception
	{
		Object deviceContainer = null;
		Class deviceContainerClass = Class.forName("org.openmobster.core.mobileCloud.android.kernel.DeviceContainer");
		
		Method getInstance = deviceContainerClass.getDeclaredMethod("getInstance", 
		new Class[]{Context.class});
		deviceContainer = getInstance.invoke(null, new Object[]{this});
		
		return deviceContainer;
	}*/
	
	
	/*private void startForegroundRegistration()
	{
		Activity activity = Registry.getActiveInstance().getContext();
		Context context = Registry.getActiveInstance().getContext();
		PackageManager pm = context.getPackageManager();
		CharSequence appName = pm.getApplicationLabel(context.getApplicationInfo());
		
		//Setup the Notification instance
		int icon = this.findDrawableId(this, "icon");
		long when = System.currentTimeMillis();
		Notification notification = new Notification(icon, appName, when);
		
		Intent notificationIntent = new Intent(this, activity.getClass());
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
		
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		notification.setLatestEventInfo(this, appName, "OpenMobster Cloud Manager", contentIntent);
		
		notification.flags|=Notification.FLAG_NO_CLEAR;
		
		int id = GeneralTools.generateUniqueId().hashCode();
		
		this.startForeground(id, notification);
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
	}*/
}
