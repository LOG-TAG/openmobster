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
import java.util.List;

import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;
import org.openmobster.core.mobileCloud.android_native.framework.BaseCloudActivity;
import org.openmobster.core.mobileCloud.api.camera.CloudPhoto;
import org.openmobster.core.mobileCloud.api.camera.CloudCamera;
import org.openmobster.core.mobileCloud.api.camera.CCException;

/**
 * @author openmobster@gmail.com
 * 
 */
public class MainActivity extends BaseCloudActivity
{
	private CloudPhoto shareme;
	
	@Override
	public void displayMainScreen()
	{
		try
		{
			//render the main screen
			String layoutClass = this.getPackageName()+".R$layout";
			String main = "main";
			Class clazz = Class.forName(layoutClass);
			Field field = clazz.getField(main);
			int screenId = field.getInt(clazz);
			this.setContentView(screenId);
			
			ListView view = (ListView)ViewHelper.findViewById(this, "list");
		    this.setTitle("Cloud Camera App");
		    
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
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			throw new RuntimeException(e);
		}
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
		
		//Setup the 'Upload to Cloud' button to take a picture
		Button upload = (Button)ViewHelper.findViewById(this, "upload");
		upload.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View button) 
			{
				try
				{
					//Upload share me to the cloud
					List<CloudPhoto> toUpload = new ArrayList<CloudPhoto>();
					toUpload.add(MainActivity.this.shareme);
					
					//Get the CloudCamera service
					CloudCamera cloudCamera = CloudCamera.getInstance();
					String cameraCommand = "/share/photo";
					cloudCamera.syncWithCloud(cameraCommand, toUpload);
					
					ViewHelper.getOkModal(MainActivity.this, "Success", "Upload successfull").show();
				}
				catch(Exception e)
				{
					e.printStackTrace(System.out);
					ViewHelper.getOkModal(MainActivity.this, "Error", "Upload to Cloud Failed").show();
				}
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
					MainActivity.this.displayMainScreen();
				}
				catch(Exception e)
				{
					e.printStackTrace(System.out);
				}
			}
		});
	}
	
	private class HandlePictureStorage implements PictureCallback
	{

		@Override
		public void onPictureTaken(byte[] picture, Camera camera) 
		{
			//The picture can be stored or do something else with the data
			//in this callback such sharing with friends, upload to a Cloud component etc
			
			//This is invoked when picture is taken and the data needs to be processed
			System.out.println("Picture successfully taken: "+picture);
			
			String fileName = "shareme.jpg";
			String mime = "image/jpeg";
			
			MainActivity.this.shareme = new CloudPhoto();
			MainActivity.this.shareme.setFullName(fileName);
			MainActivity.this.shareme.setMimeType(mime);
			MainActivity.this.shareme.setPhoto(picture);
		}
	}
}
