/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.mgr;

import java.util.List;
import java.util.Vector;

import org.openmobster.android.api.rpc.MobileService;
import org.openmobster.android.api.rpc.Request;
import org.openmobster.android.api.rpc.Response;
import org.openmobster.core.mobileCloud.android.configuration.Configuration;
import org.openmobster.core.mobileCloud.android.module.bus.Bus;
import org.openmobster.core.mobileCloud.android.module.bus.Invocation;
import org.openmobster.core.mobileCloud.android.module.bus.InvocationResponse;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Context;

/**
 *
 * @author openmobster@gmail.com
 */
public final class ManualSync
{
	private Activity currentActivity;
	
	private ManualSync(Activity currentActivity)
	{
		this.currentActivity = currentActivity;
	}
	
	public static ManualSync getInstance(Activity currentActivity)
	{
		return new ManualSync(currentActivity);
	}
	
	public void start()
	{
		try
		{
			Context context = Registry.getActiveInstance().getContext();
			Configuration conf = Configuration.getInstance(context);
			
			List<String> myChannels = conf.getMyChannels();
			if(myChannels != null && !myChannels.isEmpty())
			{
				String[] items = myChannels.toArray(new String[0]);
				AlertDialog appDialog = new AlertDialog.Builder(currentActivity).
						setItems(items, 
						new ClickListener(currentActivity,items)).
				    	setCancelable(true).
				    	create();
						
				appDialog.setTitle("Manual Sync");
										
				appDialog.show();
			}
			else
			{
				//no channels found
				ViewHelper.getOkModal(currentActivity, "Manual Sync", "Sync Channels not found").show();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			ViewHelper.getOkModal(currentActivity, "System Error", "Loading the Sync Channels failed").show();
		}
	}
	
	private class ClickListener implements DialogInterface.OnClickListener
	{
		private Activity currentActivity;
		private String[] items;
		
		private ClickListener(Activity currentActivity,String[] items)
		{
			this.currentActivity = currentActivity;
			this.items = items;
		}
		
		public void onClick(DialogInterface dialog, int status)
		{
		}
	}
}
