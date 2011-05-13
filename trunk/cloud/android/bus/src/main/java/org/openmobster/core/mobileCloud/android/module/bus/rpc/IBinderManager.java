/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.module.bus.rpc;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import android.os.IBinder;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.Context;

import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.module.bus.BusRegistration;
import org.openmobster.core.mobileCloud.android.service.Service;
import org.openmobster.core.mobileCloud.android.service.Registry;

/**
 * @author openmobster@gmail.com
 *
 */
public final class IBinderManager extends Service
{
	private Map<String,IBinder> liveBinders;
		
	public IBinderManager()
	{	
	}

	public void start()
	{
		try
		{
			this.liveBinders = new HashMap<String,IBinder>();
			
			
			//Bind to all the OpenMobster Buses on the device
			Set<String> allBuses = BusRegistration.allBuses();
			for(String bus:allBuses)
			{
				this.bind(bus);
			}
		}
		catch(Exception e)
		{
			throw new SystemException(this.getClass().getName(),"start",new Object[]{
				"Exception: "+e.toString(),
				"Message: "+e.getMessage()
			});
		}
	}

	public void stop()
	{
		this.liveBinders = null;
	}
	
	public static IBinderManager getInstance()
	{
		return (IBinderManager)Registry.getActiveInstance().lookup(IBinderManager.class);
	}
	//----------------------------------------------------------------------------------
	public void bind(String appPackageName)
	{
		Context context = Registry.getActiveInstance().getContext();
		Intent busIntent = new Intent();
		busIntent.setClassName(
		appPackageName, 
		"org.openmobster.core.mobileCloud.android.module.bus.rpc.BusService");
		
		if(this.liveBinders.get(appPackageName) == null)
		{
			ServiceConnection conn = new IBinderConnection();
			boolean success = context.getApplicationContext().
			bindService(busIntent, conn, 
			Context.BIND_AUTO_CREATE);
		}
	}
	
	public void rebind(String appPackageName)
	{
		Context context = Registry.getActiveInstance().getContext();
		Intent busIntent = new Intent();
		busIntent.setClassName(
		appPackageName, 
		"org.openmobster.core.mobileCloud.android.module.bus.rpc.BusService");
		
		ServiceConnection conn = new IBinderConnection();
		boolean success = context.getApplicationContext().
		bindService(busIntent, conn, 
		Context.BIND_AUTO_CREATE);
	}
	
	public IBinder getBinder(String appPackageName)
	{
		return this.liveBinders.get(appPackageName);
	}
	
	public boolean isConnectedToCloud()
	{
		return this.liveBinders.containsKey("org.openmobster.core.mobileCloud");
	}
	//----------------------------------------------------------------------------------
	private static class IBinderConnection implements ServiceConnection
	{
		public void onServiceConnected(ComponentName componentName, IBinder binder)
		{
			IBinderManager bm = IBinderManager.getInstance();
			String app = componentName.getPackageName();
			
			//System.out.println("Binding To: "+app);
			
			bm.liveBinders.put(app, binder);
		}

		public void onServiceDisconnected(ComponentName componentName)
		{	
			//Nothing to do here
		}
	}
}
