/**
 * Copyright (c) {2003,2009} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.manager.gui.screens;

import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.NavigationContext;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.Screen;
import org.openmobster.core.mobileCloud.api.ui.framework.resources.AppResources;
import org.openmobster.core.mobileCloud.android_native.framework.events.ListItemClickEvent;
import org.openmobster.core.mobileCloud.android_native.framework.events.ListItemClickListener;
import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;

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
		ListActivity listApp = (ListActivity) Registry.getActiveInstance()
				.getContext();

		listApp.setTitle("Push Settings");

		listApp.setListAdapter(new EfficientAdapter(listApp));

		// Add a List click listener
		ListItemClickListener clickListener = new ListItemClickListener() {
			public void onClick(ListItemClickEvent clickEvent)
			{
			}
		};
		NavigationContext.getInstance().addClickListener(clickListener);
		
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

	// ------------------------------------------------------------------------------------------------------------------------------------
	private static class EfficientAdapter extends BaseAdapter
	{
		private LayoutInflater mInflater;
		private Bitmap active;
		private Bitmap inactive;
		private Activity activity;

		public EfficientAdapter(Activity activity)
		{
			this.activity = activity;
			
			AppResources appRes = Services.getInstance().getResources();
			
			// Cache the LayoutInflate to avoid asking for a new one each time.
			mInflater = LayoutInflater.from(this.activity);

			// Icons bound to the rows.
			active = (Bitmap)appRes.getImage("/moblet-app/icon/green.png");
			inactive = (Bitmap)appRes.getImage("/moblet-app/icon/red.png");
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
			holder.icon.setImageBitmap(position == 0 ? active : inactive);

			return convertView;
		}

		static class ViewHolder
		{
			TextView text;
			ImageView icon;
		}
	}
}
