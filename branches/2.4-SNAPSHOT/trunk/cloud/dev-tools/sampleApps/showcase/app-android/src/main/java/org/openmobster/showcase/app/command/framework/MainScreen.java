/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.showcase.app.command.framework;

import java.util.ArrayList;
import java.util.HashMap;
import org.showcase.app.R;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

/**
 * Controls the 'home' screen that is displayed when the App is first launched.
 * 
 * It displays the Showcase options in a 'ListView' 
 * 
 * @author openmobster@gmail.com
 */

public class MainScreen extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.command_framework_main);
	
		show();
	}
	
	private void show()
	{
		//Populate the List View
		ListView view = (ListView)findViewById(R.id.list);
				
		ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
		
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("empty", "");
		map.put("title", "Ajax Command");
		mylist.add(map);
		
		HashMap<String, String> map2 = new HashMap<String, String>();
		map2.put("empty", "");
		map2.put("title", "Busy Command");
		mylist.add(map2);
		
		HashMap<String, String> map3 = new HashMap<String, String>();
		map3.put("empty", "");
		map3.put("title", "Fast Command");
		mylist.add(map3);
		
		int rowId = R.layout.command_framework_row;
		String[] rows = new String[]{"empty","title"};
		int[] rowUI = new int[] {R.id.empty,R.id.title};
		SimpleAdapter showcaseAdapter = new SimpleAdapter(MainScreen.this, mylist, rowId, rows, rowUI);
	    view.setAdapter(showcaseAdapter);
	    
	    OnItemClickListener clickListener = new ClickListener();
		view.setOnItemClickListener(clickListener);
	}
	
	private class ClickListener implements OnItemClickListener
	{	
		private ClickListener()
		{
		}
		
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id)
		{
			if(position == 0)
			{
				
				Handler handler=new Handler(){
					@Override
					public void handleMessage(Message msg){
						int what=msg.what;
						if(what==1){
							Toast.makeText(MainScreen.this,"Command Execution Finished!!",1).show();
						}
					}
				};
				new AjaxCommand(MainScreen.this,handler).execute();
				
			}
			else if(position == 1)
			{
				
				//Command Execution Finished!!
				
				Handler handler=new Handler(){
					@Override
					public void handleMessage(Message msg){
						int what=msg.what;
						if(what==1){
							Toast.makeText(MainScreen.this,"Command Execution Finished!!",1).show();
						}
					}
				};
				new BusyCommand(MainScreen.this,handler).execute();
				
			}
			else if(position == 2)
			{
				
				//Command Execution Finished!!
				
				Handler handler=new Handler(){
					@Override
					public void handleMessage(Message msg){
						int what=msg.what;
						if(what==1){
							Toast.makeText(MainScreen.this,"Command Execution Finished!!",1).show();
						}
					}
				};
				new FastCommand(MainScreen.this, handler).execute();				
			}
		}
	}
	
}