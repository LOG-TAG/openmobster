/**
 * Copyright (c) {2003,2009} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.manager.gui.screens;

import java.lang.reflect.Field;
import java.util.List;

import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android_native.framework.events.ListItemClickEvent;
import org.openmobster.core.mobileCloud.android_native.framework.events.ListItemClickListener;
import org.openmobster.core.mobileCloud.android.configuration.Configuration;

import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.NavigationContext;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.Screen;
import org.openmobster.core.mobileCloud.api.ui.framework.resources.AppResources;
import org.openmobster.core.mobileCloud.manager.gui.LocaleKeys;

import android.app.Activity;
import android.app.ListActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.ArrayAdapter;


/**
 * @author openmobster@gmail.com
 */
public class ManualSyncScreen extends Screen
{	
	private Integer screenId;
			
	public ManualSyncScreen()
	{										
	}
	//-------------------------------------------------------------------------------------------------------------------------------------------------
	public void render()
	{
		try
		{
			final Activity currentActivity = (Activity)Registry.getActiveInstance().
			getContext();
			
			String layoutClass = currentActivity.getPackageName()+".R$layout";
			String main = "main";
			Class clazz = Class.forName(layoutClass);
			Field field = clazz.getField(main);
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
	
	public Object getContentPane() 
	{		
		return this.screenId;
	}
	
	public void postRender()
	{		
		//render the list		
		ListActivity listApp = (ListActivity)Registry.getActiveInstance().
		getContext();
		final Configuration conf = Configuration.getInstance(listApp);
		AppResources appResources = Services.getInstance().getResources();	
		
		listApp.setTitle(appResources.localize(LocaleKeys.manual_sync, LocaleKeys.manual_sync));
		
		String[] adapterArray = null;
		List<String> myChannels = conf.getMyChannels();
		final boolean channelsFound;
		if(myChannels != null && !myChannels.isEmpty())
		{
			channelsFound = true;
			adapterArray = myChannels.toArray(new String[0]);
			listApp.setListAdapter(new ArrayAdapter(listApp, 
					android.R.layout.simple_list_item_1, 
					adapterArray));
		}
		else
		{
			channelsFound = false;
			adapterArray = new String[]{appResources.localize(LocaleKeys.channels_not_found, LocaleKeys.channels_not_found)};
			listApp.setListAdapter(new ArrayAdapter(listApp, 
					android.R.layout.simple_list_item_1, 
					adapterArray));
		}
		
		//Setup Menu
		this.setupMenu();
		
		//Add a List click listener
		ListItemClickListener clickListener = new ListItemClickListener()
		{
			public void onClick(ListItemClickEvent clickEvent)
			{
				if(!channelsFound)
				{
					Services.getInstance().getNavigationContext().back();
					return;
				}
				
				//FIXME: Perform channel-oriented functions
			}
		};
		NavigationContext.getInstance().addClickListener(clickListener);
	}
	
	private void setupMenu()
	{
		//setup menu for this screen
		Menu menu = (Menu)NavigationContext.getInstance().
		getAttribute("options-menu");
		
		if(menu != null)
		{			
			MenuItem backItem = menu.add(0, 0, 0, "Back");
			backItem.setOnMenuItemClickListener(new OnMenuItemClickListener()
			{
				public boolean onMenuItemClick(MenuItem clickedItem)
				{
					Services.getInstance().getNavigationContext().back();
					return true;
				}
			});
		}
	}
}
