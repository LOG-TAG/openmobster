/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.showcase.app.camera;

import java.util.ArrayList;
import java.util.HashMap;
import org.showcase.app.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

/**
 * Controls the 'home' screen that is displayed when the App is first launched.
 * 
 * It displays the Showcase options in a 'ListView' 
 * 
 * @author openmobster@gmail.com
 */

public class CameraMainScreen extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState){		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		show(this);
		
	}
	
	private void show(Activity activity)
	{
		//Populate the List View
		ListView view = (ListView)findViewById(R.id.list);
		activity.setTitle("Camera Showcase");
		
		ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
		
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("empty", "");
		map.put("title", "Take a Picture");
		mylist.add(map);
		
		//int rowId = ViewHelper.findLayoutId(activity, "camera_row");
		String[] rows = new String[]{"empty","title"};
		int[] rowUI = new int[] {R.id.empty,R.id.title};
		SimpleAdapter showcaseAdapter = new SimpleAdapter(activity, mylist, R.layout.camera_row, rows, rowUI);
	    view.setAdapter(showcaseAdapter);
	    
	    OnItemClickListener clickListener = new ClickListener();
		view.setOnItemClickListener(clickListener);
	}
	
	private class ClickListener implements OnItemClickListener
	{	
		private ClickListener()
		{
		}
		
		public void onItemClick(AdapterView<?> parent, View view, int position,	long id)
		{
			
			Intent intent=new Intent(CameraMainScreen.this,PreviewScreen.class);
			startActivity(intent);
			//NavigationContext.getInstance().navigate("/camera/preview");
		}
	}	
}