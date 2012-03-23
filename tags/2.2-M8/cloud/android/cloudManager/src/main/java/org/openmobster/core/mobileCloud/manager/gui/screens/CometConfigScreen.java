/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.manager.gui.screens;

import org.openmobster.core.mobileCloud.android.configuration.Configuration;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android_native.framework.events.ListItemClickEvent;
import org.openmobster.core.mobileCloud.android_native.framework.events.ListItemClickListener;
import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.NavigationContext;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.Screen;
import org.openmobster.core.mobileCloud.api.ui.framework.resources.AppResources;

import android.content.Context;
import android.content.DialogInterface;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.app.Activity;


/**
 * @author openmobster@gmail.com
 */
public class CometConfigScreen extends Screen
{
	private static String[] functions = new String[] { "Real-Time Push", "Poll" };

	public CometConfigScreen()
	{
	}

	// -------------------------------------------------------------------------------------------------------------------------------------------------
	public void render()
	{
	}

	public Object getContentPane()
	{
		return null;
	}

	public void postRender()
	{						
		// render the list
		ListActivity listApp = (ListActivity)Services.getInstance().getCurrentActivity();
		
		NavigationContext navigationContext = NavigationContext.getInstance();
		AppResources appResources = Services.getInstance().getResources();
		Configuration conf = Configuration.getInstance(listApp);
		String status = (String)navigationContext.getAttribute(this.getId(), "status");

		listApp.setTitle("Push Settings");

		if(conf.isInPushMode())
		{
			listApp.setListAdapter(new EfficientAdapter(listApp,functions[0],status));
		}
		else
		{
			listApp.setListAdapter(new EfficientAdapter(listApp,functions[1],status));
		}

				
		this.setupMenu();
		
		// Add a List click listener
		ListItemClickListener clickListener = new ListItemClickListener() {
			public void onClick(ListItemClickEvent clickEvent)
			{
				int functionId = clickEvent.getPosition();
				switch(functionId)
				{					
					case 0:	
						CometConfigScreen.this.startPushConfig();
					break;
					
					case 1:
						CometConfigScreen.this.startPollConfig();
					break;
				}
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
	//---------------user interactions----------------------------------------------------------------------------------------------------------------------
	private void startPushConfig()
	{
		Activity currentActivity = Services.getInstance().getCurrentActivity();
		
		PushConfigListener listener = new PushConfigListener();
		
		AlertDialog pushDialog = new AlertDialog.Builder(currentActivity).
		setItems(new String[]{"Start","Stop","Cancel"}, listener).
    	setCancelable(false).
    	create();
		
		pushDialog.setTitle("Push");
						
		pushDialog.show();
	}
	
	private void startPollConfig()
	{
		Activity currentActivity = Services.getInstance().getCurrentActivity();
		
		PollConfigListener listener = new PollConfigListener();
		
		AlertDialog pollDialog = new AlertDialog.Builder(currentActivity).
		setItems(new String[]{"Start","Stop","Cancel"}, listener).
    	setCancelable(false).
    	create();
		
		pollDialog.setTitle("Poll");
								
		pollDialog.show();
	}
	
	private static class PushConfigListener implements DialogInterface.OnClickListener
	{
		public void onClick(DialogInterface dialog, int status)
		{
			CommandContext commandContext = new CommandContext();	
			dialog.cancel();			
			switch(status)
			{
				case 0:
					//Start					
					commandContext.setTarget("cometConfig");
					commandContext.setAttribute("mode", "push");				
					Services.getInstance().getCommandService().execute(commandContext);
				break;
				
				case 1:
					//Stop					
					commandContext.setTarget("cometStop");
					commandContext.setAttribute("mode", "push");				
					Services.getInstance().getCommandService().execute(commandContext);
				break;								
			}
		}		
	}
	
	private static class PollConfigListener implements DialogInterface.OnClickListener
	{
		public void onClick(DialogInterface dialog, int status)
		{
			Activity currentActivity = Services.getInstance().getCurrentActivity();			
			dialog.cancel();			
			switch(status)
			{
				case 0:
					//Start
					final String[] choices = new String[]{"5","10","15","20","25","30", "Cancel"};
					AlertDialog intervalDialog = new AlertDialog.Builder(currentActivity).
			    	setCancelable(false).
			    	setItems(choices,
			    			new DialogInterface.OnClickListener() 
			    			{								
								public void onClick(DialogInterface dialog, int status)
								{
									String selection = choices[status];
									if(status < choices.length-1)
									{
										int pollMinutes = Integer.parseInt(selection);
										long pollInterval = pollMinutes * 60 * 1000; //minutes converted to milliseconds
										
										//Handle setting poll mode
										CommandContext commandContext = new CommandContext();
										commandContext.setTarget("cometConfig");
										commandContext.setAttribute("mode", "poll");
										commandContext.setAttribute("poll_interval", ""+pollInterval);
										Services.getInstance().getCommandService().execute(commandContext);
									}
								}
							}).
			    	create();
					
					intervalDialog.setTitle("Select Poll Interval: ");	
														
					intervalDialog.show();
				break;
				
				case 1:
					//Stop
					CommandContext commandContext = new CommandContext();
					commandContext.setTarget("cometStop");							
					Services.getInstance().getCommandService().execute(commandContext);
				break;
			}
		}		
	}
	// ------------------------------------------------------------------------------------------------------------------------------------
	private static class EfficientAdapter extends BaseAdapter
	{
		private LayoutInflater mInflater;
		private Bitmap active;
		private Bitmap inactive;
		private Activity activity;
		private String mode;
		private String status;

		public EfficientAdapter(Activity activity, String mode, String status)
		{
			this.activity = activity;
			
			AppResources appRes = Services.getInstance().getResources();
			
			// Cache the LayoutInflate to avoid asking for a new one each time.
			mInflater = LayoutInflater.from(this.activity);

			// Icons bound to the rows.
			active = (Bitmap)appRes.getImage("/moblet-app/icon/green.png");
			inactive = (Bitmap)appRes.getImage("/moblet-app/icon/red.png");
			
			this.mode = mode;
			this.status = status;
		}

		/**
		 * The number of items in the list is determined by the number of
		 * speeches in our array.
		 * 
		 * @see android.widget.ListAdapter#getCount()
		 */
		public int getCount()
		{
			return CometConfigScreen.functions.length;
		}

		/**
		 * Since the data comes from an array, just returning the index is
		 * sufficent to get at the data. If we were using a more complex data
		 * structure, we would return whatever object represents one row in the
		 * list.
		 * 
		 * @see android.widget.ListAdapter#getItem(int)
		 */
		public Object getItem(int position)
		{
			return position;
		}

		/**
		 * Use the array index as a unique id.
		 * 
		 * @see android.widget.ListAdapter#getItemId(int)
		 */
		public long getItemId(int position)
		{
			return position;
		}

		/**
		 * Make a view to hold each row.
		 * 
		 * @see android.widget.ListAdapter#getView(int, android.view.View,
		 *      android.view.ViewGroup)
		 */
		public View getView(int position, View convertView, ViewGroup parent)
		{
			// A ViewHolder keeps references to children views to avoid
			// unneccessary calls
			// to findViewById() on each row.
			ViewHolder holder;

			// When convertView is not null, we can reuse it directly, there is
			// no need
			// to reinflate it. We only inflate a new View when the convertView
			// supplied
			// by ListView is null.
			if (convertView == null)
			{
				convertView = mInflater.inflate(ViewHelper.findLayoutId(this.activity, "cometconfig"),
						null);

				// Creates a ViewHolder and store references to the two children
				// views
				// we want to bind data to.
				holder = new ViewHolder();
				holder.text = (TextView) convertView.findViewById(ViewHelper.findViewId(activity, "text"));
				holder.icon = (ImageView) convertView.findViewById(ViewHelper.findViewId(activity, "icon"));

				convertView.setTag(holder);
			} else
			{
				// Get the ViewHolder back to get fast access to the TextView
				// and the ImageView.
				holder = (ViewHolder) convertView.getTag();
			}

			// Bind the data efficiently with the holder.
			holder.text.setText(CometConfigScreen.functions[position]);
			
			if(functions[position].equals(this.mode) && status.equals(""+Boolean.TRUE))
			{
				holder.icon.setImageBitmap(active);
			}
			else
			{
				holder.icon.setImageBitmap(inactive);
			}

			return convertView;
		}

		static class ViewHolder
		{
			TextView text;
			ImageView icon;
		}
	}
}
