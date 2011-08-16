/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.showcase.app.crud;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import org.openmobster.core.mobileCloud.android.configuration.Configuration;
import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;
import org.openmobster.core.mobileCloud.android_native.framework.events.ListItemClickEvent;
import org.openmobster.core.mobileCloud.android_native.framework.events.ListItemClickListener;
import org.openmobster.core.mobileCloud.api.sync.MobileBean;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.NavigationContext;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.Screen;
import org.openmobster.core.mobileCloud.api.ui.framework.resources.AppResources;
import org.openmobster.showcase.app.AppConstants;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
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
public class CRUDMainScreen extends Screen
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
			String home = "crud";
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
		
		this.showTickets(app);
		this.setupMenu(app);
	}
	
	private void showTickets(Activity activity)
	{
		//Populate the List View
		ListView view = (ListView)ViewHelper.findViewById(activity, "list");
		activity.setTitle("CRUD");
		
		MobileBean[] beans = MobileBean.readAll(AppConstants.webappsync);
		
		ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
		for(MobileBean local:beans)
		{
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("empty", "");
			map.put("title", local.getValue("title"));
			mylist.add(map);
		}
		
		int rowId = ViewHelper.findLayoutId(activity, "crud_row");
		String[] rows = new String[]{"empty","title"};
		int[] rowUI = new int[] {ViewHelper.findViewId(activity, "empty"), ViewHelper.findViewId(activity, "title")};
		SimpleAdapter showcaseAdapter = new SimpleAdapter(activity, mylist, rowId, rows, rowUI);
	    view.setAdapter(showcaseAdapter);
	    
	    OnItemClickListener clickListener = new ClickListener(beans);
		view.setOnItemClickListener(clickListener);
	}
	
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
					NavigationContext.getInstance().navigate("/save/ticket");
					return true;
				}
			});
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
			
			String title = selectedBean.getValue("title");
			String comment = selectedBean.getValue("comment");
			
			//Show the details of the ticket in an AlertDialog with three possible actions
			//'Update', 'Close' (does nothing), and 'Delete'
			AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity);
			builder.setMessage(comment)
			       .setCancelable(false).setTitle(title).
			       setPositiveButton("Update", new DialogInterface.OnClickListener() 
			       {
			           public void onClick(DialogInterface dialog, int id) 
			           {
			        	   //navigate to the Update screen
			        	   NavigationContext navContext = NavigationContext.getInstance();
			        	   navContext.setAttribute("/save/ticket","active-bean", selectedBean);
			        	   NavigationContext.getInstance().navigate("/save/ticket");
			        	   
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
			        	   commandContext.setAttribute("active-bean", selectedBean);
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