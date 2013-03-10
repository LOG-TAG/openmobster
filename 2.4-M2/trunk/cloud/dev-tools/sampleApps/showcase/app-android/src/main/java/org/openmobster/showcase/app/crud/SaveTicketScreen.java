/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.showcase.app.crud;

import java.lang.reflect.Field;

import org.openmobster.android.api.sync.MobileBean;
import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.NavigationContext;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.Screen;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.showcase.app.AppConstants;

import android.app.Activity;
import android.app.ListActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;

/**
 * Controls the 'Save Ticket' screen. 
 * 
 * The UI presents a simple Form for inputing ticket details, and then you can 'OK' or 'Cancel' the changes
 * 
 * 
 * @author openmobster@gmail.com
 */
public class SaveTicketScreen extends Screen
{
	private Integer screenId;
	
	@Override
	public void render()
	{
		try
		{
			//lays out the UI specified in res/layout/new_ticket.xml
			final Activity currentActivity = Services.getInstance().getCurrentActivity();
			
			String layoutClass = currentActivity.getPackageName()+".R$layout";
			Class clazz = Class.forName(layoutClass);
			Field field = clazz.getField("save_ticket");
			
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
		NavigationContext navContext = NavigationContext.getInstance();
		try
		{
			final Activity currentActivity = Services.getInstance().getCurrentActivity();
			
			//Populate the screen with existing ticket instance state
			final MobileBean activeBean = (MobileBean)navContext.getAttribute(this.getId(),"active-bean");
			
			//If update, then populate the fields with selected ticket information
			if(activeBean != null)
			{
				EditText title = (EditText)ViewHelper.findViewById(currentActivity, "title");
				title.setText(activeBean.getValue("title"));
				
				EditText comments = (EditText)ViewHelper.findViewById(currentActivity, "comments");
				comments.setText(activeBean.getValue("comment"));
			}
			
			//Add Event Handlers
			Button save = (Button)ViewHelper.findViewById(currentActivity, "save");
			save.setOnClickListener(new OnClickListener(){
				public void onClick(View button)
				{
					SaveTicketScreen.this.save(activeBean);
				}
			});
			
			Button cancel = (Button)ViewHelper.findViewById(currentActivity, "cancel");
			cancel.setOnClickListener(new OnClickListener(){
				public void onClick(View button)
				{
					Toast.makeText(currentActivity, 
							"Save was cancelled!!", 
							Toast.LENGTH_LONG).show();
					NavigationContext.getInstance().back();
				}
			});
		}
		finally
		{
			navContext.removeAttribute(this.getId(), "active-bean");
		}
	}
	//--------------------------------------------------------------------------------------------------------------------------------------
	private void save(MobileBean activeBean)
	{
		if(activeBean != null)
		{
			//update
			this.update(activeBean);
		}
		else
		{
			//add
			this.add();
		}
	}
	private void add()
	{
		final Activity currentActivity = Services.getInstance().getCurrentActivity();
		
		//Creates a new ticket instance on the device. Once 'saved', it will be seamlessly synchronized with the Cloud
		MobileBean activeBean = MobileBean.newInstance(AppConstants.channel);
		
		EditText title = (EditText)ViewHelper.findViewById(currentActivity, "title");
		activeBean.setValue("title", title.getText().toString());
		
		EditText comments = (EditText)ViewHelper.findViewById(currentActivity, "comments");
		activeBean.setValue("comment", comments.getText().toString());
		
		//execute the create ticket usecase. It creates a new ticket in the on-device db and
		//its synchronized automagically with the Cloud
		CommandContext commandContext = new CommandContext();
		commandContext.setTarget("/save/ticket");
		commandContext.setAttribute("active-bean", activeBean);
		Services.getInstance().getCommandService().execute(commandContext);
	}
	
	private void update(MobileBean activeBean)
	{
		final Activity currentActivity = Services.getInstance().getCurrentActivity();
		
		EditText title = (EditText)ViewHelper.findViewById(currentActivity, "title");
		activeBean.setValue("title", title.getText().toString());
		
		EditText comments = (EditText)ViewHelper.findViewById(currentActivity, "comments");
		activeBean.setValue("comment", comments.getText().toString());
		
		
		//execute the create ticket usecase. It creates a new ticket in the on-device db and
		//its synchronized automagically with the Cloud
		CommandContext commandContext = new CommandContext();
		commandContext.setTarget("/save/ticket");
		commandContext.setAttribute("active-bean", activeBean);
		Services.getInstance().getCommandService().execute(commandContext);
	}
}
