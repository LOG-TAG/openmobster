/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.showcase.app.camera;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.FileOutputStream;

import org.openmobster.android.api.sync.MobileBean;
import org.openmobster.core.mobileCloud.android.configuration.Configuration;
import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;
import org.openmobster.core.mobileCloud.android_native.framework.events.ListItemClickEvent;
import org.openmobster.core.mobileCloud.android_native.framework.events.ListItemClickListener;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.NavigationContext;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.Screen;
import org.openmobster.core.mobileCloud.api.ui.framework.resources.AppResources;
import org.openmobster.showcase.app.AppConstants;

import android.widget.FrameLayout;
import android.view.View.OnClickListener;
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
import android.widget.Button;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;

/**
 * Controls the 'home' screen that is displayed when the App is first launched.
 * 
 * It displays the Showcase options in a 'ListView' 
 * 
 * @author openmobster@gmail.com
 */
public class PreviewScreen extends Screen
{
	private Integer screenId;
	
	private Preview preview;
	
	private byte[] picture;
	
	@Override
	public void render()
	{
		try
		{
			//Lays out the screen based on configuration in res/layout/home.xml
			final Activity currentActivity = (Activity)Registry.getActiveInstance().
			getContext();
			
			String layoutClass = currentActivity.getPackageName()+".R$layout";
			String home = "camera_preview";
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
		
		this.show(app);
	}
	
	private void show(Activity activity)
	{
		//Setup the Preview screen
		this.preview = new Preview(activity);
		FrameLayout frame = (FrameLayout)ViewHelper.findViewById(activity, "preview");
		frame.addView(preview);
		
		final Button buttonClick = (Button)ViewHelper.findViewById(activity, "buttonClick");
		
		final ShutterCallback shutterCallback = new ShutterCallback() 
		{
			public void onShutter() 
			{
			}
		};

		/** Handles data for raw picture */
		final PictureCallback rawCallback = new PictureCallback() 
		{
			public void onPictureTaken(byte[] data, Camera camera) 
			{
				System.out.println("onPictureTaken - raw");
			}
		};

		/** Handles data for jpeg picture */
		final PictureCallback jpegCallback = new PictureCallback() 
		{
			public void onPictureTaken(byte[] data, Camera camera) 
			{
				FileOutputStream outStream = null;
				try 
				{
					/*outStream = new FileOutputStream(String.format(
							"/sdcard/%d.jpg", System.currentTimeMillis()));
					outStream.write(data);
					outStream.close();
					System.out.println("onPictureTaken - wrote bytes: " + data.length);*/
					PreviewScreen.this.picture = data;
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				} 
			}
		};
		
		buttonClick.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) 
			{
				Camera camera = PreviewScreen.this.preview.getCamera();
				camera.takePicture(shutterCallback, rawCallback,
						jpegCallback);
				
				NavigationContext.getInstance().back();
			}
		});
	}
}