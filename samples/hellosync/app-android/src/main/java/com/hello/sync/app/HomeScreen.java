/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.hello.sync.app;

import java.lang.reflect.Field;

import android.app.Activity;
import android.app.ListActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.ArrayAdapter;

import org.openmobster.core.mobileCloud.android.configuration.Configuration;
import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;

import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.NavigationContext;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.Screen;
import org.openmobster.core.mobileCloud.api.ui.framework.resources.AppResources;

import org.openmobster.android.api.sync.MobileBean;


/**
 * @author openmobster@gmail.com
 */
public class HomeScreen extends Screen
{
	private Integer screenId;
	
	@Override
	public void render()
	{
		try
		{
			//Simply laying out the Home Screen for display
			final Activity currentActivity = Services.getInstance().getCurrentActivity();
			
			String layoutClass = currentActivity.getPackageName()+".R$layout";
			String home = "home";
			Class clazz = Class.forName(layoutClass);
			Field field = clazz.getField(home);
			
			this.screenId = field.getInt(clazz);						
		}
		catch(Exception e)
		{
			SystemException se = new SystemException(this.getClass().getName(), "render", new Object[]{
				"Message:"+e.getMessage(),
				"Exception:"+e.toString()
			});
			ErrorHandler.getInstance().handle(se);
			throw se;
		}
	}
	
	@Override
	public Object getContentPane()
	{
		return this.screenId;
	}
	
	@Override
	public void postRender()
	{
		ListActivity listApp = (ListActivity)Services.getInstance().getCurrentActivity();
		
		//Checking to make sure the Device is activated with the Cloud...
		//I just realized this call should be moved into the MVC Framework bootstrapping process
		//Next release!!!
		AppResources res = Services.getInstance().getResources();
		Configuration configuration = Configuration.getInstance(listApp);
		
		if(!configuration.isActive())
		{
			ViewHelper.getOkModalWithCloseApp(listApp, "App Error", res.localize("inactive_message","inactive_message")).
			show();
			
			return;
		}
		
		//Show the List of the "HelloSyncBeans" automatically synced and stored on the device
		//As a developer you only deal with the MobileBean component...No low-level sync stuff to worry about
		if(MobileBean.isBooted("hellosync"))
		{
			MobileBean[] helloBeans = MobileBean.readAll("hellosync");
			
			//Preparing the ui with data stored in the beans..in the message field
			String[] ui = new String[helloBeans.length];
			for(int i=0,size=ui.length;i<size;i++)
			{
				ui[i] = helloBeans[i].getValue("message");
			}
			
			//Showing the data in the list
			listApp.setListAdapter(new ArrayAdapter(listApp, 
		    android.R.layout.simple_list_item_1, 
		    ui));
		}
		
		//Adding a Menu for user interaction to the screen
		this.setMenuItems();
	}
	
	private void setMenuItems()
	{
		Menu menu = (Menu)NavigationContext.getInstance().
		getAttribute("options-menu");
		
		if(menu != null)
		{
			MenuItem resetChannel = menu.add(Menu.NONE, Menu.NONE, 0, "Reset Channel");
			resetChannel.setOnMenuItemClickListener(new OnMenuItemClickListener()
			{
				public boolean onMenuItemClick(MenuItem clickedItem)
				{
					//UserInteraction/Event Processing...this is where the Commands can be executed
					CommandContext commandContext = new CommandContext();
					commandContext.setTarget("/hellosync/reset");
					Services.getInstance().getCommandService().execute(commandContext);
					return true;
				}
			});
		}
	}
}
