/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.showcase.app;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import org.openmobster.android.api.sync.MobileBean;
import org.openmobster.core.mobileCloud.android.configuration.Configuration;
import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;
import org.openmobster.core.mobileCloud.android_native.framework.events.ListItemClickEvent;
import org.openmobster.core.mobileCloud.android_native.framework.events.ListItemClickListener;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.NavigationContext;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.Screen;
import org.openmobster.core.mobileCloud.api.ui.framework.resources.AppResources;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;
import android.widget.ListView;

/**
 * Controls the 'home' screen that is displayed when the App is first launched.
 * 
 * It displays the Showcase options in a 'ListView' 
 * 
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
			//Lays out the screen based on configuration in res/layout/home.xml
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
		Activity app = (Activity)Registry.getActiveInstance().getContext();
		
		Configuration conf = Configuration.getInstance(app);
		if(!conf.isActive())
		{
			ViewHelper.getOkModalWithCloseApp(app, "App Error", "Device needs to be activated via the Cloud Manager App").
			show();
			
			return;
		}
		
		//App Title
		app.setTitle("Showcase App");
		
		//List Population
		this.setupScreen(app);
	}
	
	private void setupScreen(final Activity activity)
	{
		//Populate the List View
		ListView view = (ListView)ViewHelper.findViewById(activity, "list");
		
		ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
		
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("empty", "");
		map.put("title", "CRUD/Sync Showcase");
		mylist.add(map);
		
		HashMap<String, String> map2 = new HashMap<String, String>();
		map2.put("empty", "");
		map2.put("title", "Command Framework Showcase");
		mylist.add(map2);
		
		HashMap<String, String> map3 = new HashMap<String, String>();
		map3.put("empty", "");
		map3.put("title", "Camera Showcase");
		mylist.add(map3);
		
		SimpleAdapter showcaseAdapter = new SimpleAdapter(activity, mylist, ViewHelper.findLayoutId(activity, "home_row"),
	            new String[] {"empty", "title"}, new int[] {ViewHelper.findViewId(activity, "empty"), ViewHelper.findViewId(activity, "title")});
	    view.setAdapter(showcaseAdapter);
	    
	    OnItemClickListener clickListener = new ClickListener();
		view.setOnItemClickListener(clickListener);
	}
	
	private static class ClickListener implements OnItemClickListener
	{
		private ClickListener()
		{
		}
		
		public void onItemClick(AdapterView<?> parent, View view, int position,long id)
		{
			int selectedIndex = position;
			
			if(selectedIndex == 0)
			{
				if(!MobileBean.isBooted(AppConstants.webappsync))
				{
					CommandContext commandContext = new CommandContext();
					commandContext.setTarget("/channel/bootup/helper");
					Services.getInstance().getCommandService().execute(commandContext);
				}
				NavigationContext.getInstance().navigate("/crud");
			}
			else if(selectedIndex == 1)
			{
				NavigationContext.getInstance().navigate("/command/framework");
			}
			else if(selectedIndex == 2)
			{
				NavigationContext.getInstance().navigate("/camera");
			}
		}
	}
}
