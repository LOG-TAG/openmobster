/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.location.app;

import java.lang.reflect.Field;

import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.app.Activity;
import android.content.Intent;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.GeoPoint;
import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;

/**
 * @author openmobster@gmail.com
 * 
 */
public class LocationMapActivity extends MapActivity
{
	public LocationMapActivity()
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
			
			//Get the Address that needs to be mapped
			Intent intent = this.getIntent();
			String street = intent.getStringExtra("street");
			String city = intent.getStringExtra("city");
			String zip = intent.getStringExtra("zip");
			String target = intent.getStringExtra("target");
			
			//ugly hack to get the progress dialog to show
			Services.getInstance().setCurrentActivity(this);
			
			//Invoke the Map command that makes a Location Aware Request
			CommandContext commandContext = new CommandContext();
			commandContext.setAttribute("activity", this);
			commandContext.setAttribute("street", street);
			commandContext.setAttribute("city", city);
			commandContext.setAttribute("zip", zip);
			commandContext.setTarget(target);
			Services.getInstance().getCommandService().execute(commandContext);
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}
	}

	@Override
	protected boolean isRouteDisplayed() 
	{
		return false;
	}
}
