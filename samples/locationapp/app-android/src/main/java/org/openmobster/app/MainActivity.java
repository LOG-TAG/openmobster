/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.app;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.HashMap;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener;


/**
 * @author openmobster@gmail.com
 * 
 */
public class MainActivity extends ListActivity
{
	public MainActivity()
	{
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
	}

	@Override
	protected void onResume()
	{
		try
		{
			super.onResume();
			
			//render the main screen
			String layoutClass = this.getPackageName()+".R$layout";
			String main = "main";
			Class clazz = Class.forName(layoutClass);
			Field field = clazz.getField(main);
			int screenId = field.getInt(clazz);
			this.setContentView(screenId);
			
			//Setup the ListView with action items
			String[] ui = new String[]{"Get Location Coordinates", "Get Location Address"};
			this.setListAdapter(new ArrayAdapter(this, 
		    android.R.layout.simple_list_item_1, 
		    ui));
			
			//Add the Event Listener
			this.getListView().setOnItemClickListener(new ClickListener());
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}
	}
	
	private class ClickListener implements OnItemClickListener
	{

		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int position,long id) 
		{
			switch(position)
			{
				case 0:
					//Get the Location Coordinates
					Map<String,Object> parameters = new HashMap<String,Object>();
					new GetLocationCoordinatesTask(MainActivity.this).execute(parameters);
				break;
				
				case 1:
					//Get Location Address
					Map<String,Object> params = new HashMap<String,Object>();
					new GetLocationAddressTask(MainActivity.this).execute(params);
				break;
				
				default:
				break;
			}
		}
	}
	//---------------------------------------------------------------------------------------------------------------------------
}
