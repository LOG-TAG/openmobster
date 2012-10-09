/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.app;

import java.lang.reflect.Field;

import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;

import org.openmobster.core.mobileCloud.android.module.bus.Bus;
import org.openmobster.core.mobileCloud.android.module.bus.Invocation;
import org.openmobster.core.mobileCloud.android.module.bus.InvocationResponse;
import org.openmobster.core.mobileCloud.android_native.framework.BaseCloudActivity;
import org.openmobster.core.mobileCloud.mgr.AppActivation;
import org.openmobster.core.mobileCloud.mgr.CloudManagerOptions;

/**
 * @author openmobster@gmail.com
 * 
 */
public class MainActivity extends BaseCloudActivity
{
	public MainActivity()
	{
		
	}
	
	@Override
	public void displayMainScreen()
	{
		try
		{
			//render the main screen
			String layoutClass = this.getPackageName()+".R$layout";
			String main = "main";
			Class clazz = Class.forName(layoutClass);
			Field field = clazz.getField(main);
			int screenId = field.getInt(clazz);
			this.setContentView(screenId);
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}
	}
	
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		if(menu.hasVisibleItems())
		{
			return true;
		}
		
		MenuItem item1 = menu.add(Menu.NONE, Menu.NONE, 0, "App Activation");
		item1.setOnMenuItemClickListener(new OnMenuItemClickListener()
		{
			public boolean onMenuItemClick(MenuItem clickedItem)
			{	
				AppActivation appActivation = AppActivation.getInstance(MainActivity.this);
				appActivation.start();
				return true;
			}
		});
		
		MenuItem item2 = menu.add(Menu.NONE, Menu.NONE, 1, "Stop Push");
		item2.setOnMenuItemClickListener(new OnMenuItemClickListener()
		{
			public boolean onMenuItemClick(MenuItem clickedItem)
			{
				try
				{
					Invocation invocation = new Invocation("org.openmobster.core.mobileCloud.android.invocation.StopCometDaemon");
					InvocationResponse response = Bus.getInstance().invokeService(invocation);
					
					return true;
				}
				catch(Exception e)
				{
					e.printStackTrace(System.out);
					return true;
				}
			}
		});
		
		MenuItem item3 = menu.add(Menu.NONE, Menu.NONE, 2, "Check Push");
		item3.setOnMenuItemClickListener(new OnMenuItemClickListener()
		{
			public boolean onMenuItemClick(MenuItem clickedItem)
			{
				try
				{
					Invocation invocation = new Invocation("org.openmobster.core.mobileCloud.android.invocation.CometStatusHandler");
					InvocationResponse response = Bus.getInstance().invokeService(invocation);
					
					String status = response.getValue("status");
					
					System.out.println("Push Status: "+status);
					
					return true;
				}
				catch(Exception e)
				{
					e.printStackTrace(System.out);
					return true;
				}
			}
		});
		
		MenuItem item4 = menu.add(Menu.NONE, Menu.NONE, 2, "Cloud Manager");
		item4.setOnMenuItemClickListener(new OnMenuItemClickListener()
		{
			public boolean onMenuItemClick(MenuItem clickedItem)
			{
				try
				{
					CloudManagerOptions cm = CloudManagerOptions.getInstance(MainActivity.this);
					cm.start();
					return true;
				}
				catch(Exception e)
				{
					e.printStackTrace(System.out);
					return true;
				}
			}
		});
		
		
		return true;
	}
}
