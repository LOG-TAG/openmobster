/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.pushrpc.android.app;

import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Field;

import org.openmobster.core.mobileCloud.android.configuration.Configuration;
import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.Screen;
import org.openmobster.core.mobileCloud.api.ui.framework.resources.AppResources;

import android.app.Activity;
import android.app.ListActivity;
import android.widget.ArrayAdapter;

/**
 * @author openmobster@gmail.com
 */
public class HomeScreen extends Screen
{
	private Integer screenId;
	
	//All received push messages are stored here for display. DemoPushCommand updates this list
	public static List<String> pushMessages;
	
	@Override
	public void render()
	{
		try
		{
			final Activity currentActivity = Services.getInstance().getCurrentActivity();
			
			String layoutClass = currentActivity.getPackageName()+".R$layout";
			String home = "home";
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
		//Bootstrapping.........
		ListActivity listApp = (ListActivity)Services.getInstance().getCurrentActivity();
		
		if(pushMessages == null)
		{
			pushMessages = new ArrayList<String>();
		}
		
		AppResources res = Services.getInstance().getResources();
		Configuration configuration = Configuration.getInstance(listApp);
		
		if(!configuration.isActive())
		{
			ViewHelper.getOkModalWithCloseApp(listApp, "App Error", res.localize("inactive_message","inactive_message")).
			show();
			
			return;
		}
		
		//Show the List of messages pushed from the cloud
		if(!pushMessages.isEmpty())
		{
			String[] ui = pushMessages.toArray(new String[0]);
			
			listApp.setListAdapter(new ArrayAdapter(listApp, 
		    android.R.layout.simple_list_item_1, 
		    ui));
		}
	}
}
