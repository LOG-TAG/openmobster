/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.offlineApp.android.app.screen;

import java.lang.reflect.Field;

import org.openmobster.core.mobileCloud.android.configuration.Configuration;
import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;
import org.openmobster.core.mobileCloud.android_native.framework.events.ListItemClickEvent;
import org.openmobster.core.mobileCloud.android_native.framework.events.ListItemClickListener;
import org.openmobster.core.mobileCloud.api.model.MobileBean;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.NavigationContext;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.Screen;
import org.openmobster.core.mobileCloud.api.ui.framework.resources.AppResources;

import android.app.Activity;
import android.app.ListActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.ArrayAdapter;

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
			final Activity currentActivity = (Activity)Registry.getActiveInstance().
			getContext();
			
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
		ListActivity listApp = (ListActivity)Registry.getActiveInstance().
		getContext();
		
		AppResources res = Services.getInstance().getResources();
		Configuration configuration = Configuration.getInstance(listApp);
		
		if(!configuration.isActive())
		{
			ViewHelper.getOkModalWithCloseApp(listApp, "App Error", res.localize("inactive_message","inactive_message")).
			show();
			
			return;
		}
		
		//Show the List of the "Demo Beans" stored on the device
		if(MobileBean.isBooted("offlineapp_demochannel"))
		{
			MobileBean[] demoBeans = MobileBean.readAll("offlineapp_demochannel");
			String[] ui = new String[demoBeans.length];
			for(int i=0,size=ui.length;i<size;i++)
			{
				ui[i] = demoBeans[i].getValue("demoString");
			}
			listApp.setListAdapter(new ArrayAdapter(listApp, 
		    android.R.layout.simple_list_item_1, 
		    ui));
			
			//List Listener
			ListItemClickListener clickListener = new ClickListener(demoBeans);
			NavigationContext.getInstance().addClickListener(clickListener);
		}
		
		//Setup the App Menu
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
					commandContext.setTarget("/offlineapp/reset");
					Services.getInstance().getCommandService().execute(commandContext);
					return true;
				}
			});
			
			MenuItem pushTrigger = menu.add(Menu.NONE, Menu.NONE, 1, "Push Trigger");
			pushTrigger.setOnMenuItemClickListener(new OnMenuItemClickListener()
			{
				public boolean onMenuItemClick(MenuItem clickedItem)
				{
					//UserInteraction/Event Processing...this is where the Commands can be executed
					CommandContext commandContext = new CommandContext();
					commandContext.setTarget("/offlineapp/pushtrigger");
					Services.getInstance().getCommandService().execute(commandContext);
					return true;
				}
			});
			
			MenuItem rpc = menu.add(Menu.NONE, Menu.NONE, 0, "Make RPC Invocation");
			rpc.setOnMenuItemClickListener(new OnMenuItemClickListener()
			{
				public boolean onMenuItemClick(MenuItem clickedItem)
				{
					//UserInteraction/Event Processing...this is where the Commands can be executed
					CommandContext commandContext = new CommandContext();
					commandContext.setTarget("/offlineapp/rpc");
					Services.getInstance().getCommandService().execute(commandContext);
					return true;
				}
			});
		}
	}
	
	private static class ClickListener implements ListItemClickListener
	{
		private MobileBean[] activeBeans;
		
		private ClickListener(MobileBean[] activeBeans)
		{
			this.activeBeans = activeBeans;
		}
		
		public void onClick(ListItemClickEvent clickEvent)
		{
			int selectedIndex = clickEvent.getPosition();
			MobileBean selectedBean = activeBeans[selectedIndex];
			
			CommandContext commandContext = new CommandContext();
			commandContext.setTarget("/demo/details");
			commandContext.setAttribute("selectedBean", selectedBean.getValue("demoString"));
			Services.getInstance().getCommandService().execute(commandContext);
		}
	}
}
