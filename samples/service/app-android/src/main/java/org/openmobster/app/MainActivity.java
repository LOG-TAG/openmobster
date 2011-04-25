/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.app;

import java.lang.reflect.Field;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Intent;

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
			
			//render the main screen
			String layoutClass = this.getPackageName()+".R$layout";
			String main = "main";
			Class clazz = Class.forName(layoutClass);
			Field field = clazz.getField(main);
			int screenId = field.getInt(clazz);
			this.setContentView(screenId);
			
			//StartService button
			Button startService = (Button)ViewHelper.findViewById(this, 
			"start_service");
			startService.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View button) 
				{
					//Start the Service
					Intent start = new Intent("org.openmobster.app.DemoService");
					MainActivity.this.startService(start);
				}
			  }
			);
			
			//Stop Service Button
			Button stopService = (Button)ViewHelper.findViewById(this, 
			"stop_service");
			stopService.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View button) 
				{
					//Start the Service
					Intent stop = new Intent("org.openmobster.app.DemoService");
					MainActivity.this.stopService(stop);
				}
			  }
			);
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}
	}
}
