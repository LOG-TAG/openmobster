/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.push;

import org.openmobster.core.mobileCloud.android.kernel.DeviceContainer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 *
 * @author openmobster@gmail.com
 */
public class NetworkStartupBroadcastReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{
		try
		{
			NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
			
			if(info.isConnected())
			{
				DeviceContainer container = DeviceContainer.getInstance(context);
				boolean isActive = container.isContainerActive();
				
				if(isActive)
				{
					//Grab a WakeLock
					StartNetwork.acquireWakeLock(context);
					
					//Now invoke the service and leave
					Intent serviceIntent = new Intent(context,StartNetwork.class);
					context.startService(serviceIntent);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}
	}
}
