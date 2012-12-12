/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.app;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import org.openmobster.android.api.sync.MobileBean;
import org.openmobster.android.api.sync.CommitException;
import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.NavigationContext;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.Screen;

import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.ListView;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;
import android.view.LayoutInflater;

/**
 * Controls the 'home' screen that is displayed when the App is first launched.
 * 
 * Displays all the ticket instances that are synchronized with the Cloud
 * 
 * It displays the tickets in a 'ListView' 
 * 
 * The screen menu provides the following functions, 'New Ticket', 'Reset Channel', and 'Demo Push'
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
		Activity app = Services.getInstance().getCurrentActivity();
		
		if(!MobileBean.isBooted("cloud_channel"))
		{
			CommandContext commandContext = new CommandContext();
			commandContext.setTarget("/channel/bootup/helper");
			Services.getInstance().getCommandService().execute(commandContext);
		}
		
		//Setup the App Menu
		this.setupMenu(app);
		
		//Display the beans
		this.showBeans(app);
	}
	//------------------------------------------------------------------------------------------------------------------
	private void showBeans(Activity app)
	{
		//Read all the beans from the channel
		MobileBean[] beans = MobileBean.readAll("cloud_channel");
		if(beans != null && beans.length > 0)
		{
			//Populate the List View
			ListView view = (ListView)ViewHelper.findViewById(app, "list");
			
			//Prepare the data for the adapter. Data is read from the ticket bean instances
			ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
			for(MobileBean local:beans)
			{
				HashMap<String, String> map = new HashMap<String, String>();
				
				String name = local.getValue("name");
				String value = local.getValue("value");
				
				if(name.length() > 25)
				{
					name = name.substring(0, 22)+"...";
				}
				
				if(value.length() > 25)
				{
					value = value.substring(0, 22)+"...";
				}
				
				map.put("name", name);
				map.put("value", value);
				mylist.add(map);
			}
			
			SimpleAdapter beanAdapter = new SimpleAdapter(app, mylist, ViewHelper.findLayoutId(app, "bean_row"),
		            new String[] {"name", "value"}, new int[] {ViewHelper.findViewId(app, "name"), ViewHelper.findViewId(app, "value")});
		    view.setAdapter(beanAdapter);
		    
		    //List Listener...used to respond to selecting a ticket instance
			OnItemClickListener clickListener = new ClickListener(beans);
			view.setOnItemClickListener(clickListener);
		}	
		else
		{
			//Populate the List View
			ListView view = (ListView)ViewHelper.findViewById(app, "list");
			
			//Prepare the data for the adapter. Data is read from the ticket bean instances
			ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
			
			SimpleAdapter beanAdapter = new SimpleAdapter(app, mylist, ViewHelper.findLayoutId(app, "bean_row"),
		            new String[] {"name", "value"}, new int[] {ViewHelper.findViewId(app, "name"), ViewHelper.findViewId(app, "value")});
		    view.setAdapter(beanAdapter);
		}
	}
	
	private void setupMenu(final Activity app)
	{
		//Get the menu associated with this screen
		Menu menu = (Menu)NavigationContext.getInstance().
		getAttribute("options-menu");
		
		if(menu != null)
		{
			//Add the 'New Ticket' Menu Item
			MenuItem newTicket = menu.add(Menu.NONE, Menu.NONE, 0, "New Ticket");
			newTicket.setOnMenuItemClickListener(new OnMenuItemClickListener()
			{
				public boolean onMenuItemClick(MenuItem clickedItem)
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(app).setCancelable(false);
					LayoutInflater inflater = (LayoutInflater)app.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					int dialogLayout = ViewHelper.findLayoutId(app, "dialog");
					View dialogView = inflater.inflate(dialogLayout, null);
					
					//Setup the Name Value
					int nameEditPointer = ViewHelper.findViewId(app, "nameEdit"); 
					final EditText nameEdit = (EditText)dialogView.findViewById(nameEditPointer);
					nameEdit.setText("");
					
					//Setup the Value Value
					int valueEditPointer = ViewHelper.findViewId(app, "valueEdit");
					final EditText valueEdit = (EditText)dialogView.findViewById(valueEditPointer);
					valueEdit.setText("");
					
					//Setup the buttons on the dialog box
					builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface dialog, int id)
						{
							String name = nameEdit.getText().toString();
							String value = valueEdit.getText().toString();
							
							if(name == null || name.trim().length()==0)
							{
								ViewHelper.getOkModal(app, "Validation Error", "'Name' is required").
								show();
								return;
							}
							if(value == null || value.trim().length()==0)
							{
								ViewHelper.getOkModal(app, "Validation Error", "'Value' is required").
								show();
								return;
							}
							
							MobileBean newBean = MobileBean.newInstance("cloud_channel");
							newBean.setValue("name", name);
							newBean.setValue("value", value);
							try
							{
								newBean.save();
							}
							catch(CommitException se)
							{
								ViewHelper.getOkModal(app, "Error", se.getMessage());
								return;
							}
							
							NavigationContext.getInstance().refresh();
						}
					});
					
					builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface dialog, int id)
						{
							NavigationContext.getInstance().refresh();
						}
					});
					
					AlertDialog beanDialog = builder.create();
					beanDialog.setView(dialogView);
					beanDialog.show();
					
					return true;
				}
			});
		}
	}
	
	private class ClickListener implements OnItemClickListener
	{
		private MobileBean[] activeBeans;
		
		private ClickListener(MobileBean[] activeBeans)
		{
			this.activeBeans = activeBeans;
		}
		
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id)
		{
			final Activity currentActivity = Services.getInstance().getCurrentActivity();
			
			//Get the ticket bean selected by the user
			int selectedIndex = position;
			final MobileBean selectedBean = activeBeans[selectedIndex];
			
			String name = selectedBean.getValue("name");
			String value = selectedBean.getValue("value");
			
			AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity).setCancelable(false);
			
			LayoutInflater inflater = (LayoutInflater)currentActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			int dialogLayout = ViewHelper.findLayoutId(currentActivity, "dialog");
			View dialogView = inflater.inflate(dialogLayout, null);
			
			//Setup the Name Value
			int nameEditPointer = ViewHelper.findViewId(currentActivity, "nameEdit"); 
			final EditText nameEdit = (EditText)dialogView.findViewById(nameEditPointer);
			nameEdit.setText(name);
			
			//Setup the Value Value
			int valueEditPointer = ViewHelper.findViewId(currentActivity, "valueEdit");
			final EditText valueEdit = (EditText)dialogView.findViewById(valueEditPointer);
			valueEdit.setText(value);
			
			//Setup the buttons on the dialog box
			builder.setPositiveButton("Update", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int id)
				{
					String name = nameEdit.getText().toString();
					String value = valueEdit.getText().toString();
					
					if(name == null || name.trim().length()==0)
					{
						ViewHelper.getOkModal(currentActivity, "Validation Error", "'Name' is required").
						show();
						return;
					}
					if(value == null || value.trim().length()==0)
					{
						ViewHelper.getOkModal(currentActivity, "Validation Error", "'Value' is required").
						show();
						return;
					}
					
					selectedBean.setValue("name", name);
					selectedBean.setValue("value", value);
					try
					{
						selectedBean.save();
					}
					catch(CommitException se)
					{
						try
						{
							selectedBean.refresh();
							selectedBean.setValue("name", name);
							selectedBean.setValue("value", value);
							selectedBean.save();
						}
						catch(Exception e)
						{
							//we tried, put up an error message
							ViewHelper.getOkModal(currentActivity, "Error", se.getMessage());
						}
					}
					
					NavigationContext.getInstance().refresh();
				}
			});
			
			builder.setNegativeButton("Delete", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int id)
				{
					try
					{
						selectedBean.delete();
					}
					catch(CommitException se)
					{
						ViewHelper.getOkModal(currentActivity, "Error", se.getMessage());
						return;
					}
					NavigationContext.getInstance().refresh();
				}
			});
			
			builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int id)
				{
					NavigationContext.getInstance().refresh();
				}
			});
			
			AlertDialog beanDialog = builder.create();
			beanDialog.setView(dialogView);
			beanDialog.show();
		}
	}
}
