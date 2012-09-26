/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.manager.gui.screens;

import java.lang.reflect.Field;

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

import android.app.Activity;
import android.app.ListActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.ArrayAdapter;


/**
 * @author openmobster@gmail.com
 */
public class SecurityScreen extends Screen
{	
	private Integer screenId;
			
	public SecurityScreen()
	{										
	}
	//-------------------------------------------------------------------------------------------------------------------------------------------------
	public void render()
	{
		try
		{
			final Activity currentActivity = Services.getInstance().getCurrentActivity();
			
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
		ListActivity listApp = (ListActivity)Services.getInstance().getCurrentActivity();
		Configuration conf = Configuration.getInstance(listApp);
		
		boolean isSSLActive = conf.isSSLActivated();
		
		if(isSSLActive)
		{
			listApp.setTitle("Current Mode: SSL");
			
			listApp.setListAdapter(new ArrayAdapter(listApp, 
				    android.R.layout.simple_list_item_1, 
				    new String[]{"Switch to non-SSL mode"}));
		}
		else
		{
			listApp.setTitle("Current Mode: non-SSL");
			
			listApp.setListAdapter(new ArrayAdapter(listApp, 
				    android.R.layout.simple_list_item_1, 
				    new String[]{"Switch to SSL mode"}));
		}
		
		
		
		//Add a List click listener
		ListItemClickListener clickListener = new ListItemClickListener()
		{
			public void onClick(ListItemClickEvent clickEvent)
			{
				CommandContext commandContext = new CommandContext();
				commandContext.setTarget("switchSSLMode");		
				Services.getInstance().getCommandService().execute(commandContext);
			}
		};
		NavigationContext.getInstance().addClickListener(clickListener);
		
		//Setup Menu
		this.setupMenu();
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
