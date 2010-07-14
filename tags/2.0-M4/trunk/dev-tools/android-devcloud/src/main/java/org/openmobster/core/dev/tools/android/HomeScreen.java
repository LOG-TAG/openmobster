/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.dev.tools.android;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Properties;

import org.openmobster.core.mobileCloud.android.configuration.Configuration;
import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android.storage.Database;
import org.openmobster.core.mobileCloud.android_native.framework.events.ListItemClickEvent;
import org.openmobster.core.mobileCloud.android_native.framework.events.ListItemClickListener;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.NavigationContext;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.Screen;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
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
	
	private String server;
	private String email;
	private String password;
	
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
			
			//Initialize the activation properties
			if(this.server == null || this.email == null || this.password == null)
			{
				Properties properties = new Properties();
				properties.load(HomeScreen.class.getResourceAsStream("/moblet-app/activation.properties"));
				this.server = properties.getProperty("cloud_server_ip");
				this.email = properties.getProperty("email");
				this.password = properties.getProperty("password");
			}
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
		
		listApp.setTitle("Development Cloud");
		
		listApp.setListAdapter(new ArrayAdapter(listApp, 
			    android.R.layout.simple_list_item_1, 
			    new String[]{"Activate"}));
		
		ListItemClickListener clickListener = new ListItemClickListener()
		{
			public void onClick(ListItemClickEvent clickEvent)
			{
				int functionId = clickEvent.getPosition();
				switch(functionId)
				{
					case 0:
						try
						{
							HomeScreen.this.mockActivate();
						}
						catch(Exception e)
						{
							e.printStackTrace(System.out);
							throw new RuntimeException(e);
						}
					break;
				}
			}
		};
		NavigationContext.getInstance().addClickListener(clickListener);
		
		this.setupMenu();
	}
	
	private void setupMenu()
	{
		final ListActivity listApp = (ListActivity)Registry.getActiveInstance().
		getContext();
		Menu menu = (Menu)NavigationContext.getInstance().
		getAttribute("options-menu");
		
		if(menu != null)
		{
			MenuItem item1 = menu.add(Menu.NONE, Menu.NONE, 0, "Activate");
			item1.setOnMenuItemClickListener(new OnMenuItemClickListener()
			{
				public boolean onMenuItemClick(MenuItem clickedItem)
				{
					try
					{
						HomeScreen.this.mockActivate();
						return true;
					}
					catch(Exception e)
					{
						e.printStackTrace(System.out);
						throw new RuntimeException(e);
					}
				}
			});
			
			MenuItem item2 = menu.add(Menu.NONE, Menu.NONE, 1, "Exit");
			item2.setOnMenuItemClickListener(new OnMenuItemClickListener()
			{
				public boolean onMenuItemClick(MenuItem clickedItem)
				{
					listApp.finish();
					return true;
				}
			});
		}
	}
	
	private void mockActivate() throws Exception
	{
		final Activity currentActivity = (Activity)Registry.getActiveInstance().
		getContext();
		
		this.startup(currentActivity);
		
		//make this externally configurable 
		this.activateDevice(this.server, "1502", this.email, this.password);
	}
	
	private void startup(final Activity activity) throws Exception
    {
		Database database = Database.getInstance(activity);
		if(database.doesTableExist(Database.provisioning_table))
		{
	    	//Make a local copy of registered channels
	    	//System.out.println("Copying the channels...........");
	    	Configuration configuration = Configuration.getInstance(activity);
	    	List<String> myChannels = configuration.getMyChannels();
	    	
	    	//drop the configuration so new one will be generated
	    	//System.out.println("Dropping the configuration.......");
	    	configuration.stop();
	    	
	    	//Clear out the provisioning table
	    	database.dropTable(Database.provisioning_table);
	    	database.createTable(Database.provisioning_table);
	    	
	    	//restart the configuration
	    	//System.out.println("Restarting the configuration.......");
	    	configuration.start(activity);
	    	
	    	//Now reload the registered channels if any were found
	    	//System.out.println("Reloading the channels.......");
	    	if(myChannels != null && myChannels.size()>0)
	    	{
		    	configuration = Configuration.getInstance(activity);
		    	for(String channel:myChannels)
		    	{
		    		configuration.addMyChannel(channel);
		    	}
		    	configuration.save(activity);
	    	}
	    	//System.out.println("Startup successfull.............");
		}
    }
	
	private void activateDevice(String server, String port, String email, String password)
	{
		Context context = Registry.getActiveInstance().
		getContext();
		Configuration conf = Configuration.getInstance(context);
		CommandContext commandContext = new CommandContext();
		commandContext.setTarget("activate");
		
		//System.out.println("-------------------------------------------------");
		//System.out.println("Server: "+server);
		//System.out.println("Port: "+port);
		//System.out.println("Email: "+email);
		//System.out.println("Password: "+password);
		//System.out.println("-------------------------------------------------");
		
		commandContext.setAttribute("server", server);
		commandContext.setAttribute("email", email);
		commandContext.setAttribute("password", password);
		if(!conf.isActive())
		{
			commandContext.setAttribute("port", port);
		}
		Services.getInstance().getCommandService().execute(commandContext);
	}
}
