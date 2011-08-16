/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.async.service.app.cloud.app.command;

import java.util.Vector;
import java.util.List;
import java.util.ArrayList;

import org.openmobster.android.api.rpc.MobileService;
import org.openmobster.android.api.rpc.Request;
import org.openmobster.android.api.rpc.Response;
import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.service.Registry;

import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;
import org.openmobster.core.mobileCloud.android_native.framework.events.ListItemClickEvent;
import org.openmobster.core.mobileCloud.android_native.framework.events.ListItemClickListener;

import org.openmobster.core.mobileCloud.api.ui.framework.command.AppException;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.command.AsyncCommand;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.NavigationContext;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;


import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Toast;

/**
 * The MVC AsyncCommand. The 'AsyncCommand' is used to perform actions asynchronously (Ajax from the web world). 
 * 
 * This particular AsyncCommand, invokes the 'GetList' service in the Cloud and gets a list of email 'Subject' values for display.
 * 
 * @author openmobster@gmail.com
 *
 */
public final class AsyncGetList implements AsyncCommand
{
	//Executes on the UI thread. All UI related operations are safe here. It is invoked to perform some pre-action UI related tasks.
	//In this case, it displays a simple status that its "Loading Emails...."
	public void doViewBefore(CommandContext commandContext)
	{	
		//Show a Loading Status
		Context context = Registry.getActiveInstance().getContext();
		Toast.makeText(context, "Loading Emails.....", 
		Toast.LENGTH_SHORT).show();
	}

	//This does not execute on the UI thread. When this method is invoked, the UI thread is freed up, so that its not frozen while the 
	//information is being loaded from the Cloud
	public void doAction(CommandContext commandContext) 
	{
		try
		{
			//Setting up the Cloud Request object
			Request request = new Request("/asyncserviceapp/getlist");	
			Response response = new MobileService().invoke(request);
			
			//Read the data from the Cloud Response
			Vector<String> list = response.getListAttribute("subjects");
			
			//Create a list of Email beans 
			List<EmailBean> emails = new ArrayList<EmailBean>();
			for(String local:list)
			{
				EmailBean email = new EmailBean();
				
				String[] tokens = local.split(":");
				
				String idToken = tokens[0];
				String oid = idToken.substring(idToken.indexOf('=')+1);
				email.setOid(oid);
				
				String subjectToken = tokens[1];
				String subject = subjectToken.substring(subjectToken.indexOf('=')+1);
				email.setSubject(subject);
				
				emails.add(email);
			}
			
			//Share the retrieved emails for this request lifecycle within the CommandContext
			commandContext.setAttribute("emails", emails);
		}
		catch(Exception e)
		{
			//throw an AppException. If this happens, the doViewError will be invoked to alert the user with an error message
			AppException appe = new AppException();
			appe.setMessage(e.getMessage());
			
			//Record this error in the Cloud Error Log
			ErrorHandler.getInstance().handle(appe);
			
			throw appe;
		}
	}	
	
	//Executes on the UI thread. All UI operations are safe. It is invoked after the doAction is executed without any errors.
	//From an Ajax standpoint, consider this invocation as the UI callback
	public void doViewAfter(CommandContext commandContext)
	{
		//Get an instance of the currently active Activity
		ListActivity listApp = (ListActivity)Registry.getActiveInstance().
		getContext();
		
		//Get the list of emails from the CommandContext
		List<EmailBean> emails = (List<EmailBean>)commandContext.getAttribute("emails");
		
		//Populate the state of the ListActivity to display the data
		String[] ui = new String[emails.size()];
		for(int i=0,size=ui.length;i<size;i++)
		{
			EmailBean email = emails.get(i);
			ui[i] = email.getSubject();
		}
		listApp.setListAdapter(new ArrayAdapter(listApp, 
	    android.R.layout.simple_list_item_1, 
	    ui));
		
		//Add an event listener to respond to 'Email' selections
		ListItemClickListener clickListener = new ClickListener(commandContext);
		NavigationContext.getInstance().addClickListener(clickListener);
	}
	
	//Executes on the UI thread. All UI operations are safe. This method is invokes if there is an error during the doAction execution.
	//From an Ajax standpoint, consider this invocation as a UI callback
	public void doViewError(CommandContext commandContext)
	{
		//Shows an Error Dialog
		Activity currentActivity = (Activity)Registry.getActiveInstance().getContext();
		ViewHelper.getOkModal(currentActivity, "App Error", 
		commandContext.getAppException().getMessage()).
		show();
	}
	//---------------------------------------------------------------------------------------------------
	//Responds to click event when an 'Email' is selected for detailed viewing from the list
	private static class ClickListener implements ListItemClickListener
	{
		private CommandContext commandContext;
		
		private ClickListener(CommandContext commandContext)
		{
			this.commandContext = commandContext;
		}
		
		public void onClick(ListItemClickEvent clickEvent)
		{
			List<EmailBean> emails = (List<EmailBean>)this.commandContext.getAttribute("emails");
			int selectedIndex = clickEvent.getPosition();
			EmailBean selectedBean = emails.get(selectedIndex);
			
			//Starts a brand new asynchronous command execution cycle. In this case, it will invoke the 'AsyncGetDetails' AsyncCommand
			CommandContext newCommandContext = new CommandContext();
			newCommandContext.setTarget("/asyncserviceapp/getdetails");
			newCommandContext.setAttribute("email", selectedBean);
			Services.getInstance().getCommandService().execute(newCommandContext);
		}
	}
}
