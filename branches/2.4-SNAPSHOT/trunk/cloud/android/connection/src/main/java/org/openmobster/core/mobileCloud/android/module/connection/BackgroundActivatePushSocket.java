/**
 * Copyright (c) {2003,2013} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.android.module.connection;

import org.openmobster.core.mobileCloud.android.module.bus.Bus;
import org.openmobster.core.mobileCloud.android.module.bus.Invocation;

import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

/**
 *
 * @author openmobster@gmail.com
 */
final class BackgroundActivatePushSocket
{
	private static BackgroundActivatePushSocket singleton = null;
	private static volatile WakeLock wakeLock;
	
	private boolean busy = false;
	
	private BackgroundActivatePushSocket()
	{
		
	}
	
	static BackgroundActivatePushSocket getInstance()
	{
		if(singleton == null)
		{
			synchronized(BackgroundActivatePushSocket.class)
			{
				if(singleton == null)
				{
					singleton = new BackgroundActivatePushSocket();
				}
			}
		}
		return singleton;
	}
	
	synchronized void execute()
	{
		if(!busy)
		{
			busy = true;
			Thread t = new Thread(new Task());
			t.start();
		}
	}
	
	private class Task implements Runnable
	{
		public void run()
		{
			try
			{
				Invocation invocation = new Invocation("org.openmobster.core.mobileCloud.android.invocation.CometRecycleHandler");
				Bus.getInstance().invokeService(invocation);
				
				//Check to see if the Push Socket is active
				int counter = 10;
				while(counter > 0)
				{
					boolean isActive = NotificationListener.getInstance().isActive();
					if(isActive)
					{
						//clear the alarm
						ActivatePushSocketScheduler.getInstance().clear();
						return;
					}
					
					Thread.sleep(6000);
					counter--;
				}
			}
			catch(Exception e)
			{
				e.printStackTrace(System.out);
			}
			finally
			{
				if(wakeLock != null)
				{
					if(wakeLock.isHeld())
					{
						wakeLock.release();
					}
					wakeLock = null;
				}
				busy = false;
			}
		}
	}
	
	static synchronized void acquireWakeLock(Context context)
	{
		if(wakeLock != null)
		{
			if(!wakeLock.isHeld())
			{
				wakeLock.acquire();
			}
			return;
		}
		
		//Setup a WakeLock
		PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "org.openmobster.core.mobileCloud.android.module.connection.BackgroundActivatePushSocket");
		wakeLock.setReferenceCounted(true);
		
		//acquire the lock
		if(!wakeLock.isHeld())
		{
			wakeLock.acquire();
		}
	}
}
