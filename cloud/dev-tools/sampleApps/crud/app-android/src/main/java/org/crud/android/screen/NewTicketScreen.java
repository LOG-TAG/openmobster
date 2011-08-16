/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.crud.android.screen;

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

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;

/**
 * Controls the 'Create New Ticket' screen. 
 * 
 * The UI presents a simple Form for inputing ticket details, and then you can 'OK' or 'Cancel' the changes
 * 
 * 
 * @author openmobster@gmail.com
 */
public class NewTicketScreen extends Screen
{
	private Integer screenId;
	
	@Override
	public void render()
	{
		try
		{
			//lays out the UI specified in res/layout/new_ticket.xml
			final Activity currentActivity = (Activity)Registry.getActiveInstance().
			getContext();
			
			String layoutClass = currentActivity.getPackageName()+".R$layout";
			Class clazz = Class.forName(layoutClass);
			Field field = clazz.getField("new_ticket");
			
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
		try
		{
			final Activity currentActivity = (Activity)Registry.getActiveInstance().
			getContext();
			
			//Load the spinners asynchronously...ajaxian usecase
			CommandContext commandContext = new CommandContext();
			commandContext.setTarget("/async/load/spinners");
			Services.getInstance().getCommandService().execute(commandContext);
			
			
			//Add Event Handlers
			Button save = (Button)ViewHelper.findViewById(currentActivity, "save");
			save.setOnClickListener(new OnClickListener(){
				public void onClick(View button)
				{
					NewTicketScreen.this.save();
				}
			});
			
			Button cancel = (Button)ViewHelper.findViewById(currentActivity, "cancel");
			cancel.setOnClickListener(new OnClickListener(){
				public void onClick(View button)
				{
					Toast.makeText(currentActivity, 
							"Ticket Creation was cancelled!!", 
							Toast.LENGTH_LONG).show();
					NavigationContext.getInstance().back();
				}
			});
		}
		finally
		{
			
		}
	}
	//--------------------------------------------------------------------------------------------------------------------------------------
	private void save()
	{
		final Activity currentActivity = (Activity)Registry.getActiveInstance().
		getContext();
		
		//Creates a new ticket instance on the device. Once 'saved', it will be seamlessly synchronized with the Cloud
		MobileBean ticket = MobileBean.newInstance("crm_ticket_channel");
		
		EditText title = (EditText)ViewHelper.findViewById(currentActivity, "title");
		ticket.setValue("title", title.getText().toString());
		
		EditText comments = (EditText)ViewHelper.findViewById(currentActivity, "comments");
		ticket.setValue("comment", comments.getText().toString());
		
		Spinner customer = (Spinner)ViewHelper.findViewById(currentActivity, "customer");
		ticket.setValue("customer", ((TextView)customer.getSelectedView()).getText().toString());
		
		Spinner specialist = (Spinner)ViewHelper.findViewById(currentActivity, "specialist");
		ticket.setValue("specialist", ((TextView)specialist.getSelectedView()).getText().toString());
		
		//execute the create ticket usecase. It creates a new ticket in the on-device db and
		//its synchronized automagically with the Cloud
		CommandContext commandContext = new CommandContext();
		commandContext.setTarget("/create/ticket");
		commandContext.setAttribute("ticket", ticket);
		Services.getInstance().getCommandService().execute(commandContext);
	}
}
