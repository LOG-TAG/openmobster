/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.android.module.connection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;


/**
 *
 * @author openmobster@gmail.com
 */
public final class DeadSocketDetector extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{
		try
		{
			//check if network is out. if that has happened, clear the alarm and leave,when the network comes back on
			//the event will be processed and a push socket will be re-established
			if(!this.isOnline(context))
			{
				DeadSocketScheduler.getInstance().clear();
				return;
			}
			
			//Grab a WakeLock
			DeadSocketService.acquireWakeLock(context);
			
			//Now invoke the service and leave
			Intent serviceIntent = new Intent(context,DeadSocketService.class);
			context.startService(serviceIntent);
		}
		catch(Exception e)
		{
			Log.e("org.openmobster.android", e.getMessage(), e);
		}
	}
	
	private boolean isOnline(Context context)
	{
		ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) 
	    {
	        return true;
	    }
		return false;
	}
}
