/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.manager.gui.screens;

import java.lang.reflect.Field;
import java.util.Vector;


import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android_native.framework.events.ListItemClickEvent;
import org.openmobster.core.mobileCloud.android_native.framework.events.ListItemClickListener;
import org.openmobster.core.mobileCloud.android.configuration.Configuration;

import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.NavigationContext;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.Screen;
import org.openmobster.core.mobileCloud.api.ui.framework.resources.AppResources;
import org.openmobster.core.mobileCloud.manager.gui.LocaleKeys;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.ArrayAdapter;
import android.net.Uri;


/**
 * @author openmobster@gmail.com
 */
public class AppStoreScreen extends Screen
{	
	private Integer screenId;
	private Vector<AppMetaData> appMetaData;
			
	public AppStoreScreen()
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
		final Configuration conf = Configuration.getInstance(listApp);
		AppResources appResources = Services.getInstance().getResources();	
		
		listApp.setTitle(appResources.localize(LocaleKeys.app_store, LocaleKeys.app_store));
		
		//Access the screen state
		NavigationContext navContext = NavigationContext.getInstance();
		Vector uris = (Vector)navContext.getAttribute(this.getId(), "uris");
		Vector names = (Vector)navContext.getAttribute(this.getId(), "names");
		Vector descs = (Vector)navContext.getAttribute(this.getId(), "descs");
		Vector downloadUrls = (Vector)navContext.getAttribute(this.getId(), "downloadUrls");
		
		final boolean appsFound;
		if(uris != null && !uris.isEmpty())
		{
			appsFound = true;
			
			this.appMetaData = new Vector<AppMetaData>();
			int size = uris.size();
			String[] adapterArray = new String[size];
			
			for(int i=0; i<size; i++)
			{
				AppMetaData cour = new AppMetaData();
				
				cour.uri = (String)uris.elementAt(i);
				cour.name = (String)names.elementAt(i);
				cour.description = (String)descs.elementAt(i);
				cour.downloadUrl = (String)downloadUrls.elementAt(i);
				
				this.appMetaData.addElement(cour);
				adapterArray[i] = cour.name;
			}
				
			listApp.setListAdapter(new ArrayAdapter(listApp, 
					android.R.layout.simple_list_item_1, 
					adapterArray));
		}
		else
		{
			appsFound = false;
			listApp.setListAdapter(new ArrayAdapter(listApp, 
					android.R.layout.simple_list_item_1, 
					new String[]{appResources.localize(LocaleKeys.no_apps_found, LocaleKeys.no_apps_found)}));
		}
		
		
		//Setup Menu
		this.setupMenu();
		
		//Add a List click listener
		ListItemClickListener clickListener = new ListItemClickListener()
		{
			public void onClick(ListItemClickEvent clickEvent)
			{
				if(!appsFound)
				{
					Services.getInstance().getNavigationContext().back();
					return;
				}
				
				//Handle App selection
				Activity currentActivity = Services.getInstance().getCurrentActivity();
				int choice = clickEvent.getPosition();
				AppMetaData selectedApp = AppStoreScreen.this.appMetaData.get(choice);
				
				AlertDialog appDialog = new AlertDialog.Builder(currentActivity).
				setItems(new String[]{"Download/Install","Cancel"}, new AppDetailListener(selectedApp)).
		    	setCancelable(false).
		    	create();
				
				appDialog.setTitle(selectedApp.description);
								
				appDialog.show();
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
	
	private static class AppMetaData
	{
		private String uri;
		private String name;
		private String description;
		private String downloadUrl;
	}
	
	private static class AppDetailListener implements DialogInterface.OnClickListener
	{
		private AppMetaData selectedApp;
		
		private AppDetailListener(AppMetaData selectedApp)
		{
			this.selectedApp = selectedApp;
		}
		
		public void onClick(DialogInterface dialog, int status)
		{
			switch(status)
			{
				case 0:
					//Download/Install the App via the official browser download mechanism
					Context context = Registry.getActiveInstance().getContext();
					Activity activity = Services.getInstance().getCurrentActivity();
					Configuration conf = Configuration.getInstance(context);
					
					String appUrl = "http://"+conf.getServerIp()+":"+conf.getHttpPort()+"/o/android"+selectedApp.downloadUrl;
					activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(appUrl)));
					
					dialog.cancel();
				break;
				
				case 1:
					dialog.cancel();
				break;
			}
		}
	}
}
