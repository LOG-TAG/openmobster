/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.crud.android.screen;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import org.openmobster.core.mobileCloud.android.configuration.Configuration;
import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;
import org.openmobster.core.mobileCloud.api.model.MobileBean;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.NavigationContext;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.Screen;
import org.openmobster.core.mobileCloud.api.ui.framework.resources.AppResources;

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
import android.content.DialogInterface;

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
		Activity app = (Activity)Registry.getActiveInstance().
		getContext();
		
		//Bootstrapping the App...These 3 checks should be sucked into the framework.instead of spilled into the App
		AppResources res = Services.getInstance().getResources();
		Configuration configuration = Configuration.getInstance(app);
		
		if(!configuration.isActive())
		{
			ViewHelper.getOkModalWithCloseApp(app, "App Error", res.localize("inactive_message","inactive_message")).
			show();
			
			return;
		}
		
		if(!MobileBean.isBooted("crm_ticket_channel"))
		{
			CommandContext commandContext = new CommandContext();
			commandContext.setTarget("/channel/bootup/helper");
			Services.getInstance().getCommandService().execute(commandContext);
			
			return;
		}
		
		//Setup the App Menu
		this.setupMenu(app);
		
		//Display the tickets
		this.showTickets(app);
	}
	//------------------------------------------------------------------------------------------------------------------
	private void setupMenu(Activity app)
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
					//UserInteraction/Event Processing...This will navigate to the NewTicket Screen
					NavigationContext.getInstance().navigate("/new/ticket");
					return true;
				}
			});
			
			//Add the 'Reset Channel' Menu Item
			MenuItem resetChannel = menu.add(Menu.NONE, Menu.NONE, 1, "Reset Channel");
			resetChannel.setOnMenuItemClickListener(new OnMenuItemClickListener()
			{
				public boolean onMenuItemClick(MenuItem clickedItem)
				{
					//UserInteraction/Event Processing...this will cleanup the channel by fully
					//resetting/booting up the channel with the Cloud
					CommandContext commandContext = new CommandContext();
					commandContext.setTarget("/reset/channel");
					Services.getInstance().getCommandService().execute(commandContext);
					return true;
				}
			});
			
			MenuItem pushTrigger = menu.add(Menu.NONE, Menu.NONE, 2, "Demo Push");
			pushTrigger.setOnMenuItemClickListener(new OnMenuItemClickListener()
			{
				public boolean onMenuItemClick(MenuItem clickedItem)
				{
					//UserInteraction/Event Processing....This triggers the PushListener to create
					//a mock ticket. This ticket is then pushed to the device in real time
					//This triggers/simulates the usecase where a new instance appearing 
					//on the Cloud side is pushed down to the device in real time
					CommandContext commandContext = new CommandContext();
					commandContext.setTarget("/demo/push");
					Services.getInstance().getCommandService().execute(commandContext);
					return true;
				}
			});
		}
	}
	
	private void showTickets(Activity app)
	{
		//Read all the ticket instances synchronized with the Cloud
		MobileBean[] tickets = MobileBean.readAll("crm_ticket_channel");
		if(tickets != null && tickets.length > 0)
		{
			//Populate the List View
			ListView view = (ListView)ViewHelper.findViewById(app, "list");
			
			//Prepare the data for the adapter. Data is read from the ticket bean instances
			ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
			for(MobileBean local:tickets)
			{
				HashMap<String, String> map = new HashMap<String, String>();
				
				String customer = local.getValue("customer");
				String title = local.getValue("title");
				
				if(customer.length() > 25)
				{
					customer = customer.substring(0, 22)+"...";
				}
				
				if(title.length() > 25)
				{
					title = title.substring(0, 22)+"...";
				}
				
				map.put("customer", customer);
				map.put("title", title);
				mylist.add(map);
			}
			
			SimpleAdapter ticketAdapter = new SimpleAdapter(app, mylist, ViewHelper.findLayoutId(app, "ticket_row"),
		            new String[] {"customer", "title"}, new int[] {ViewHelper.findViewId(app, "customer"), ViewHelper.findViewId(app, "title")});
		    view.setAdapter(ticketAdapter);
		    
		    //List Listener...used to respond to selecting a ticket instance
			OnItemClickListener clickListener = new ClickListener(tickets);
			view.setOnItemClickListener(clickListener);
		}	
	}
	
	private static class ClickListener implements OnItemClickListener
	{
		private MobileBean[] activeBeans;
		
		private ClickListener(MobileBean[] activeBeans)
		{
			this.activeBeans = activeBeans;
		}
		
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id)
		{
			final Activity currentActivity = (Activity)Registry.getActiveInstance().
			getContext();
			
			//Get the ticket bean selected by the user
			int selectedIndex = position;
			final MobileBean selectedBean = activeBeans[selectedIndex];
			
			//Show the details of the ticket in an AlertDialog with three possible actions
			//'Update', 'Close' (does nothing), and 'Delete'
			AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity);
			builder.setMessage(selectedBean.getValue("comment"))
			       .setCancelable(false).setTitle("Specialist: "+selectedBean.getValue("specialist")).
			       setPositiveButton("Update", new DialogInterface.OnClickListener() 
			       {
			           public void onClick(DialogInterface dialog, int id) 
			           {
			        	   //navigate to the Update screen
			        	   NavigationContext navContext = NavigationContext.getInstance();
			        	   navContext.setAttribute("/update/ticket","ticket", selectedBean);
			        	   NavigationContext.getInstance().navigate("/update/ticket");
			        	   
			        	   dialog.dismiss();
			           }
			       })
			       .setNegativeButton("Delete", new DialogInterface.OnClickListener() 
			       {
			           public void onClick(DialogInterface dialog, int id) 
			           {
			        	   dialog.dismiss();
			        	   
			        	   //Delete this ticket instance. This CRUD operation is then seamlessly
			        	   //synchronized back with the Cloud
			        	   CommandContext commandContext = new CommandContext();
			        	   commandContext.setTarget("/delete/ticket");
			        	   commandContext.setAttribute("ticket", selectedBean);
			        	   Services.getInstance().getCommandService().execute(commandContext);
			           }
			       })
			       .setNeutralButton("Close", new DialogInterface.OnClickListener() 
			       {
			           public void onClick(DialogInterface dialog, int id) 
			           {
			                dialog.dismiss();
			           }
			       });
			
			AlertDialog alert = builder.create();
			alert.show();
		}
	}
}
