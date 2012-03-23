/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.manager;

import android.content.Intent;

import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.kernel.DeviceContainer;
import org.openmobster.core.mobileCloud.android.module.connection.PolicyManager;

import org.openmobster.core.mobileCloud.android_native.framework.ListApp;

/**
 * @author openmobster@gmail.com
 *
 */
public class CloudManagerApp extends ListApp
{
	//private NetworkBroadcastReceiver networkReceiver;
	
	/*@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		//Start the NetworkBroadcast Receiver
		if(this.networkReceiver == null)
		{
			this.networkReceiver  = new NetworkBroadcastReceiver();
			this.registerReceiver(networkReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
		}
    	
    	super.onCreate(savedInstanceState);
	}*/

	
	/*@Override
	protected void onDestroy()
	{
		this.unregisterReceiver(networkReceiver);
		super.onDestroy();
	}*/

	
	@Override
	protected void onStart()
	{
		try
		{									
			this.bootstrapContainer();
			
			//Initialize the Device Administration Policy Manager
	    	PolicyManager.getInstance().showAdminScreen(this);
			
			super.onStart();
		} 
		catch (Exception e)
		{
			ErrorHandler.getInstance().handle(new SystemException(this.getClass().getName(), "onStart", new Object[]{
				"Message:"+e.getMessage(),
				"Exception:"+e.toString()
			}));
			this.showError();
		}
	}
	
	private void bootstrapContainer() throws Exception
	{
		DeviceContainer container = DeviceContainer.getInstance(this.getApplicationContext());
		boolean isActive = container.isContainerActive();
		
		//start the kernel
		container.propagateNewContext(this.getApplicationContext());
    	container.startup(); 
    	
    	//Start the Keep Alive service
    	if(!isActive)
    	{
    		Intent start = new Intent("org.openmobster.core.mobileCloud.manager.KeepAliveService");
    		start.putExtra("activityClassName", this.getClass().getName());
    		this.startService(start);
    	}
	}
}
