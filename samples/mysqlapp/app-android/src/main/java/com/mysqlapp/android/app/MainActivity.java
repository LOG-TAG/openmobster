/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.mysqlapp.android.app;

import java.util.ArrayList;
import java.util.HashMap;

import org.openmobster.android.api.sync.MobileBean;
import org.openmobster.core.mobileCloud.android_native.framework.BaseCloudActivity;
import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @author openmobster@gmail.com
 * 
 */
public class MainActivity extends BaseCloudActivity
{
	public MainActivity()
	{
		
	}

	@Override
	public void displayMainScreen() 
	{
		this.setContentView(R.layout.main);
		this.displayDataObjects();
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		//Setup the 'New Object' item
		MenuItem item1 = menu.add(Menu.NONE, Menu.NONE, 0, "Add New Object");
		item1.setOnMenuItemClickListener(new OnMenuItemClickListener()
		{
			public boolean onMenuItemClick(MenuItem clickedItem)
			{
				setContentView(R.layout.new_data_object);
				
				//Add the Save Event Handler
				Button save = (Button)ViewHelper.findViewById(MainActivity.this, "save");
				save.setOnClickListener(new OnClickListener(){
					public void onClick(View button)
					{
						try
						{
							MainActivity.this.save();
						}
						catch(Exception e)
						{
							e.printStackTrace(System.out);
							ViewHelper.getOkModal(MainActivity.this, "Error", e.toString());
						}
					}
				});
				
				//Add the Cancel Event Handler
				Button cancel = (Button)ViewHelper.findViewById(MainActivity.this, "cancel");
				cancel.setOnClickListener(new OnClickListener(){
					public void onClick(View button)
					{
						Toast.makeText(MainActivity.this, 
								"Data Object creation was cancelled", 
								Toast.LENGTH_LONG).show();
						
						MainActivity.this.setContentView(R.layout.main);
						MainActivity.this.displayDataObjects();
					}
				});
				
				return true;
			}
		});
		
		
		return true;
	}
	//--------------------------------------------------------------------------------------------------------------------------
	private void displayDataObjects()
	{
		MobileBean[] dataObjects = MobileBean.readAll("data_object_channel");
		if(dataObjects == null)
		{
			ViewHelper.getOkModal(this, "Data Objects", "Data Objects are not synced yet").show();
			return;
		}
		
		//Populate the List View
		ListView view = (ListView)ViewHelper.findViewById(this, "data_objects");
		
		//Prepare the data for the adapter. Data is read from the data_object data instances
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		for(MobileBean local:dataObjects)
		{
			HashMap<String, String> map = new HashMap<String, String>();
			
			String field1 = local.getValue("field1");
			String field2 = local.getValue("field2");
			
			map.put("field1", field1);
			map.put("field2", field2);
			list.add(map);
		}
		
		//Prepare the data object adapter to display the data in a list
		SimpleAdapter dataObjectAdapter = new SimpleAdapter(this, list, ViewHelper.findLayoutId(this, "data_object_row"),
	    new String[] {"field1", "field2"}, new int[] {ViewHelper.findViewId(this, "field1"), 
		ViewHelper.findViewId(this, "field2")});
		
	    view.setAdapter(dataObjectAdapter);
	    
	    //List Listener...used to respond to selecting a data_object instance
		OnItemClickListener clickListener = new ClickListener(dataObjects);
		view.setOnItemClickListener(clickListener);
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
			final Activity currentActivity = MainActivity.this;
			
			//Get the ticket bean selected by the user
			int selectedIndex = position;
			final MobileBean selectedBean = activeBeans[selectedIndex];
			
			//Show the details of the data_object in an AlertDialog with three possible actions
			//'Update', 'Close' (does nothing), and 'Delete'
			AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity);
			builder.setMessage(selectedBean.getValue("field3"))
			       .setCancelable(false).setTitle(selectedBean.getValue("field4")).
			       setPositiveButton("Update", new DialogInterface.OnClickListener() 
			       {
			           public void onClick(DialogInterface dialog, int id) 
			           {
			        	   dialog.dismiss();
			        	   
			        	   //Load the object data into the Update screen
			        	   MainActivity.this.loadUpdate(selectedBean);
			           }
			       })
			       .setNegativeButton("Delete", new DialogInterface.OnClickListener() 
			       {
			           public void onClick(DialogInterface dialog, int id) 
			           {
			        	   try
			        	   {
			        		   dialog.dismiss();
			        	   
			        		   //Delete this object locally. It will be seamlessly synchronized back with the Cloud
			        		   selectedBean.delete();
			        		   
			        		   //refresh the screen
			        		   MainActivity.this.setContentView(R.layout.main);
			        		   MainActivity.this.displayDataObjects();
			        	   }
			        	   catch(Exception e)
			        	   {
			        		   e.printStackTrace(System.out);
			        		   ViewHelper.getOkModal(MainActivity.this, "Error", e.toString());  
			        	   }
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
	
	private void save() throws Exception
	{
		final Activity currentActivity = this;
		
		//Creates a new data_object instance on the device. Once 'saved', it will be seamlessly synchronized with the Cloud
		MobileBean data_object = MobileBean.newInstance("data_object_channel");
		
		EditText field1 = (EditText)ViewHelper.findViewById(currentActivity, "field1");
		if(field1.getText().toString() == null || field1.getText().toString().trim().length()==0)
		{
			ViewHelper.getOkModal(this, "Validation Error", "All Fields are required").show();
			return;
		}
		data_object.setValue("field1", field1.getText().toString());
		
		EditText field2 = (EditText)ViewHelper.findViewById(currentActivity, "field2");
		if(field2.getText().toString() == null || field2.getText().toString().trim().length()==0)
		{
			ViewHelper.getOkModal(this, "Validation Error", "All Fields are required").show();
			return;
		}
		data_object.setValue("field2", field2.getText().toString());
		
		EditText field3 = (EditText)ViewHelper.findViewById(currentActivity, "field3");
		if(field3.getText().toString() == null || field3.getText().toString().trim().length()==0)
		{
			ViewHelper.getOkModal(this, "Validation Error", "All Fields are required").show();
			return;
		}
		data_object.setValue("field3", field3.getText().toString());
		
		EditText field4 = (EditText)ViewHelper.findViewById(currentActivity, "field4");
		if(field4.getText().toString() == null || field4.getText().toString().trim().length()==0)
		{
			ViewHelper.getOkModal(this, "Validation Error", "All Fields are required").show();
			return;
		}
		data_object.setValue("field4", field4.getText().toString());
		
		data_object.save();
		
		this.setContentView(R.layout.main);
		this.displayDataObjects();
	}
	
	private void update(MobileBean data_object) throws Exception
	{
		final Activity currentActivity = this;
		
		EditText field1 = (EditText)ViewHelper.findViewById(currentActivity, "field1");
		if(field1.getText().toString() == null || field1.getText().toString().trim().length()==0)
		{
			ViewHelper.getOkModal(this, "Validation Error", "All Fields are required").show();
			return;
		}
		data_object.setValue("field1", field1.getText().toString());
		
		EditText field2 = (EditText)ViewHelper.findViewById(currentActivity, "field2");
		if(field2.getText().toString() == null || field2.getText().toString().trim().length()==0)
		{
			ViewHelper.getOkModal(this, "Validation Error", "All Fields are required").show();
			return;
		}
		data_object.setValue("field2", field2.getText().toString());
		
		EditText field3 = (EditText)ViewHelper.findViewById(currentActivity, "field3");
		if(field3.getText().toString() == null || field3.getText().toString().trim().length()==0)
		{
			ViewHelper.getOkModal(this, "Validation Error", "All Fields are required").show();
			return;
		}
		data_object.setValue("field3", field3.getText().toString());
		
		EditText field4 = (EditText)ViewHelper.findViewById(currentActivity, "field4");
		if(field4.getText().toString() == null || field4.getText().toString().trim().length()==0)
		{
			ViewHelper.getOkModal(this, "Validation Error", "All Fields are required").show();
			return;
		}
		data_object.setValue("field4", field4.getText().toString());
		
		data_object.save();
		
		this.setContentView(R.layout.main);
		this.displayDataObjects();
	}
	
	private void loadUpdate(final MobileBean selectedBean)
	{
		setContentView(R.layout.update_data_object);
		
		EditText field1 = (EditText)ViewHelper.findViewById(this, "field1");
		field1.setText(selectedBean.getValue("field1"));
		
		EditText field2 = (EditText)ViewHelper.findViewById(this, "field2");
		field2.setText(selectedBean.getValue("field2"));
		
		EditText field3 = (EditText)ViewHelper.findViewById(this, "field3");
		field3.setText(selectedBean.getValue("field3"));
		
		EditText field4 = (EditText)ViewHelper.findViewById(this, "field4");
		field4.setText(selectedBean.getValue("field4"));
		
		//Activate the Save Button
		Button update = (Button)ViewHelper.findViewById(this, "save");
		update.setOnClickListener(new OnClickListener(){
			public void onClick(View button)
			{
				try
				{
					MainActivity.this.update(selectedBean);
				}
				catch(Exception e)
				{
        		   e.printStackTrace(System.out);
        		   ViewHelper.getOkModal(MainActivity.this, "Error", e.toString());  
				}
			}
		});
		
		//Activate the Cancel Button
		Button cancel = (Button)ViewHelper.findViewById(this, "cancel");
		cancel.setOnClickListener(new OnClickListener(){
			public void onClick(View button)
			{
				Toast.makeText(MainActivity.this, 
						"Data Object update was cancelled", 
						Toast.LENGTH_LONG).show();
				
				MainActivity.this.setContentView(R.layout.main);
				MainActivity.this.displayDataObjects();
			}
		});
	}
}
