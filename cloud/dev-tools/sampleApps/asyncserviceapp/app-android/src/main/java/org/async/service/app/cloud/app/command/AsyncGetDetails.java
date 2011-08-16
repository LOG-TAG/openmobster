/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.async.service.app.cloud.app.command;

import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;
import org.openmobster.core.mobileCloud.api.ui.framework.command.AppException;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.command.AsyncCommand;
import org.openmobster.core.mobileCloud.api.rpc.MobileService;
import org.openmobster.core.mobileCloud.api.rpc.Request;
import org.openmobster.core.mobileCloud.api.rpc.Response;

import android.app.Activity;

/**
 * The MVC AsyncCommand. The 'AsyncCommand' is used to perform actions asynchronously (Ajax from the web world). 
 * 
 * This particular AsyncCommand, invokes the 'GetDetails' service in the Cloud and gets a fully populated Email instance for display.
 * 
 * @author openmobster@gmail.com
 *
 */
public final class AsyncGetDetails implements AsyncCommand
{
	//Executes on the UI thread. All UI related operations are safe here. It is invoked to perform some pre-action UI related tasks.
	public void doViewBefore(CommandContext commandContext)
	{	
		//Nothing to do
	}

	//This does not execute on the UI thread. When this method is invoked, the UI thread is freed up, so that its not frozen while the 
	//information is being loaded from the Cloud
	public void doAction(CommandContext commandContext) 
	{
		try
		{
			//Populate the Cloud Request
			EmailBean email = (EmailBean)commandContext.getAttribute("email");
			Request request = new Request("/asyncserviceapp/getdetails");	
			request.setAttribute("oid", email.getOid());
			
			Response response = new MobileService().invoke(request);
			
			//Process the Cloud Response
			String oid = response.getAttribute("oid");
			String from = response.getAttribute("from");
			String to = response.getAttribute("to");
			String subject = response.getAttribute("subject");
			String date = response.getAttribute("date");
			
			//Populate the Email bean with the information fetched from the Cloud
			email.setOid(oid);
			email.setFrom(from);
			email.setTo(to);
			email.setSubject(subject);
			email.setDate(date);
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
		//Get the populated Email bean
		EmailBean email = (EmailBean)commandContext.getAttribute("email");
		
		//Build the string to be displayed
		StringBuilder buffer = new StringBuilder();
		buffer.append("From: "+email.getFrom()+"\n");
		buffer.append("To: "+email.getTo()+"\n");
		buffer.append("Date: "+email.getDate()+"\n\n");
		buffer.append("Subject: "+email.getSubject());
		
		//Display the details in a 'Dialog' box
		Activity currentActivity = (Activity)Registry.getActiveInstance().getContext();
		ViewHelper.getOkModal(currentActivity, "Email", 
		buffer.toString()).
		show();
	}
	
	//Executes on the UI thread. All UI operations are safe. This method is invokes if there is an error during the doAction execution.
	//From an Ajax standpoint, consider this invocation as a UI callback
	public void doViewError(CommandContext commandContext)
	{
		//Shows an Error Alert
		Activity currentActivity = (Activity)Registry.getActiveInstance().getContext();
		ViewHelper.getOkModal(currentActivity, "App Error", 
		commandContext.getAppException().getMessage()).
		show();
	}
}
