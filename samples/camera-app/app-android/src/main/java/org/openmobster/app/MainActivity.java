/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.app;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;

import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;

/**
 * @author openmobster@gmail.com
 * 
 */
public class MainActivity extends Activity
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
			
			this.showMainScreen();
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}
	}
	//------------Screen Display Code---------------------------------------------------------------------------------------
	private void showMainScreen() throws Exception
	{
		//render the main screen
		String layoutClass = this.getPackageName()+".R$layout";
		String main = "main";
		Class clazz = Class.forName(layoutClass);
		Field field = clazz.getField(main);
		int screenId = field.getInt(clazz);
		this.setContentView(screenId);
		
		ListView view = (ListView)ViewHelper.findViewById(this, "list");
	    this.setTitle("Camera");
	    
	    ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
		
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("empty", "");
		map.put("title", "Take a Picture");
		mylist.add(map);
		
		int rowId = ViewHelper.findLayoutId(this, "main_row");
		String[] rows = new String[]{"empty","title"};
		int[] rowUI = new int[] {ViewHelper.findViewId(this, "empty"), ViewHelper.findViewId(this, "title")};
		SimpleAdapter listAdapter = new SimpleAdapter(this, mylist, rowId, rows, rowUI);
	    view.setAdapter(listAdapter);
	    
	    view.setOnItemClickListener(new OnItemClickListener(){
	    	public void onItemClick(AdapterView<?> parent, View view, int position,
					long id)
	    	{
	    		try
				{
					MainActivity.this.showPreviewScreen();
				}
				catch(Exception e)
				{
					e.printStackTrace(System.out);
				}
	    	}
	    });
	}
	
	private void showPreviewScreen() throws Exception
	{
		//render the main screen
		String layoutClass = this.getPackageName()+".R$layout";
		String main = "camera_preview";
		Class clazz = Class.forName(layoutClass);
		Field field = clazz.getField(main);
		int screenId = field.getInt(clazz);
		this.setContentView(screenId);
		
		//Setup the FrameLayout with the Camera Preview Screen
		final CameraSurfaceView cameraSurfaceView = new CameraSurfaceView(this);
		FrameLayout preview = (FrameLayout)ViewHelper.findViewById(this, "preview"); 
		preview.addView(cameraSurfaceView);
		
		//Setup the 'Take Picture' button to take a picture
		Button takeAPicture = (Button)ViewHelper.findViewById(this, "buttonClick");
		takeAPicture.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) 
			{
				Camera camera = cameraSurfaceView.getCamera();
				camera.takePicture(null, null, new HandlePictureStorage());
			}
		});
		
		//Setup the 'Done' button to go back
		Button done = (Button)ViewHelper.findViewById(this, "done");
		done.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) 
			{
				try
				{
					MainActivity.this.showMainScreen();
				}
				catch(Exception e)
				{
					e.printStackTrace(System.out);
				}
			}
		});
	}
	
	private static class HandlePictureStorage implements PictureCallback
	{

		@Override
		public void onPictureTaken(byte[] picture, Camera camera) 
		{
			//The picture can be stored or do something else with the data
			//in this callback such sharing with friends, upload to a Cloud component etc
			
			//This is invoked when picture is taken and the data needs to be processed
			System.out.println("Picture successfully taken: "+picture);
		}
	}
}
