/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sample.component.activity.android.app;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * This is a simple tutorial of an Android Activity component
 * 
 * It shows the following:
 * 
 * * Shows how to launch and App and display the 'Home' screen
 * * Shows how to navigate away from this 'Home' screen to a 'Next' screen
 * * Shows App state persistence across App re-starts
 * * Shows Menu creation and handling for the App
 *
 * @author openmobster@gmail.com
 */
public class SampleActivity extends Activity 
{
	private Cache cache;
	
	@Override
	/**
	 * This is where initial activation of the Activity happens. This is where the state including the UI that
	 * must be displayed are prepared and in the case of the UI, displayed.
	 * 
	 * In the scope of the lifecycle, when another Activity (from the same App or different App) becomes visible,
	 * this activity becomes no longer visible.
	 * 
	 * When this activity should become visible again, *if Android runtime has killed this activity's process*, this
	 * method is invoked again. If the process is still active, this will *not* be invoked
	 */
	protected void onCreate(Bundle savedInstanceState) 
	{
		try
		{
			//This must be invoked..otherwise the Android runtime will throw an exception
			super.onCreate(savedInstanceState);
			
			//Load some App state information that is stored in a simple cache. This information survives App re-starts
			this.loadCache();
			
			//Get the layout information for the home screen
			Integer homeScreenLayoutId  = R.layout.home;
			
			//Get the layout information for the next screen
			Integer nextScreenLayoutId  = R.layout.nextscreen;
			
			this.cache.put("home", homeScreenLayoutId);
			this.cache.put("nextscreen", nextScreenLayoutId);
		}
		catch(Exception e)
		{
			//Show an error screen and exit
			this.getErrorDialog("System Error", e.toString()).show();
		}
	}
	

	@Override
	/**
	 * This method is invoked when the App is fully loaded and should now display the UI associated with the activity.
	 * 
	 * Usually you perform the screen/view rendering of the screen that should be displayed. This could be the home/main screen 
	 * of the app. In this Activity's case, to demonstrate caching capabilities, it shows the screen that was being displayed
	 * when the App was last running
	 */
	protected void onResume() 
	{
		try
		{
			//This must be invoked..otherwise the Android runtime will throw an exception
			super.onResume();
			
			//Find and render last active screen
			String active = (String)this.cache.get("active");
			if(active != null)
			{
				if(active.equals("home"))
				{
					this.renderHomeScreen();
				}
				else
				{
					this.renderNextScreen();
				}
			}
			else
			{
				//render the home view
				this.renderHomeScreen();
			}
		}
		catch(Exception e)
		{
			//Show an error screen and exit
			this.getErrorDialog("System Error", e.toString()).show();
		}
	}

	@Override
	/**
	 * This method is invoked when the App is being destroyed. 
	 * 
	 * Typically you would do some state management here. In this case,it saves the cache on the file system, so next time when the App is launched,
	 * the currently active screen is displayed.
	 */
	protected void onDestroy() 
	{
		try
		{
			//This must be invoked..otherwise the Android runtime will throw an exception
			super.onDestroy();
			
			//Saving the App State
			this.saveCache();
		}
		catch(Exception e)
		{
			//Handle Exception..App is being destroyed...in case of an error, let the platform handle it with a system message
			throw new RuntimeException(e);
		}
	}
	
	@Override
	/**
	 * Call back to create an options menu for the App
	 */
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		try
		{
			//Inflate the layout for the menu. The xml is stored in res/menu/app_menu.xml
		    MenuInflater inflater = getMenuInflater();
			Integer app_menu_id  = R.menu.app_menu;
		    inflater.inflate(app_menu_id, menu);
		    
		    return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}
	
	@Override
	/**
	 * Call back when one of the menu items is clicked. Returns 'true' if its appropriately handled so it does not need to propagate the event any further.
	 * 'false' otherwise
	 */
	public boolean onOptionsItemSelected(MenuItem item) 
	{
	    // Handle item selection
	    switch (item.getItemId()) 
	    {
	    	case R.id.hello:
	    		Toast.makeText(this, "Hello World!!!", Toast.LENGTH_SHORT).show();
	    		return true;
	    	case R.id.quit:
	    		this.finish();
	    		return true;
	    	default:
	        return super.onOptionsItemSelected(item);
	    }
	}
    //---------end of Activity lifecycle call back methods-----------------------------------------------------------------------------
	/**
	 * Renders the home screen of the App
	 */
	private void renderHomeScreen()
	{
		final Cache cache = this.cache;
		Integer homeLayout = (Integer)cache.get("home");
		
		//Now display the home screen...After this the layout will be inflated
		//and the screen wil be displayed
		this.setContentView(homeLayout);
		
		//Setup the list options
		ListView list = (ListView)this.findViewById(android.R.id.list);
		String[] options = new String[]{"Demo Navigate"};
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,options);
		list.setAdapter(adapter);
		
		//Setup the eventlistener that reacts to user interaction for this list
		list.setOnItemClickListener(new OnItemClickListener() 
		{
		    public void onItemClick(AdapterView<?> parent, View view,
		        int position, long id) 
		    {
		    	// When clicked, show a toast with the TextView text
		    	TextView textView = (TextView)view;
		    	String selection = textView.getText().toString();
		    	if(selection.equals("Demo Navigate"))
		    	{
					//render the 'nextscreen' view
					SampleActivity.this.renderNextScreen();
		    	}
		    }
		  });
		
		//Mark this as the active screen
		cache.put("active", "home");
	}
	
	/**
	 * Renders the 'nextscreen' when 'Demo Navigate' is chosen on the 'home' screen
	 */
	private void renderNextScreen()
	{
		final Cache cache = this.cache;
		Integer nextScreenLayout = (Integer)cache.get("nextscreen");
		
		//Now display the home screen...After this the layout will be inflated
		//and the screen wil be displayed
		this.setContentView(nextScreenLayout);
		
		//Setup the list options
		ListView list = (ListView)this.findViewById(android.R.id.list);
		String[] options = new String[]{"Go Back"};
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,options);
		list.setAdapter(adapter);
		
		//Setup the eventlistener that reacts to user interaction for this list
		list.setOnItemClickListener(new OnItemClickListener() 
		{
		    public void onItemClick(AdapterView<?> parent, View view,
		        int position, long id) 
		    {
		    	//render the 'home' view
				SampleActivity.this.renderHomeScreen();
		    }
		  });
		
		//Mark this as the active screen
		cache.put("active", "nextscreen");
	}
	
	/**
	 * Saves the App state to survive re-starts
	 * 
	 * @throws Exception
	 */
	private void saveCache() throws Exception
	{
		//Load the SharedPreferences Editor
		SharedPreferences local = this.getPreferences(0);
		SharedPreferences.Editor editor = local.edit();
		
		//Store the 'home' layout id
		Integer home = (Integer)cache.get("home");
		if(home != null)
		{
			editor.putString("home", ""+home);
		}
		
		//Store the 'nextscreen' layout id
		Integer nextscreen = (Integer)cache.get("nextscreen");
		if(nextscreen != null)
		{
			editor.putString("nextscreen", ""+nextscreen);
		}
		
		//Store the 'active' screen, this can be used to display this screen when the App is launched again
		String active = (String)cache.get("active");
		if(active != null)
		{
			editor.putString("active", active);
		}
		
		//commit the changes
		editor.commit();
	}
	
	/**
	 * Load the App State from SharedPreferences
	 * 
	 * @throws Exception
	 */
	private void loadCache() throws Exception
	{
		SharedPreferences local = this.getPreferences(0);
		this.cache = new Cache();
		String home = local.getString("home", "");
		String nextscreen = local.getString("nextscreen", "");
		String active = local.getString("active", "");
		
		if(home != null && home.trim().length()>0)
		{
			cache.put("home", new Integer(Integer.parseInt(home)));
		}
		
		if(nextscreen != null && nextscreen.trim().length()>0)
		{
			cache.put("nextscreen", new Integer(Integer.parseInt(nextscreen)));
		}
		
		if(active != null && active.trim().length()>0)
		{
			cache.put("active", active);
		}
	}
	
	/**
	 * Creates and AlertDialog with the specified title and message
	 * 
	 * @param title
	 * @param message
	 * @return
	 */
	private AlertDialog getErrorDialog(String title, String message)
	{
		AlertDialog okModal = null;

		//Generate a dialog using a dialog builder
		okModal = new AlertDialog.Builder(this).setTitle(title)
				.setMessage(message).setCancelable(false).create();
		
		//set the 'OK' button on the dialog and its corresponding event handler
		okModal.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int status)
					{
						dialog.dismiss();
						SampleActivity.this.finish();
					}
				});

		return okModal;
	}
}
