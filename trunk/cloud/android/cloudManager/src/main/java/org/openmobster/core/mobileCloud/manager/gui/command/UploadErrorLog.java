/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.manager.gui.command;

import android.content.Context;
import android.app.Activity;
import android.widget.Toast;

import org.openmobster.core.mobileCloud.android.configuration.Configuration;
import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;

import org.openmobster.core.mobileCloud.api.rpc.MobileService;
import org.openmobster.core.mobileCloud.api.rpc.Request;
import org.openmobster.core.mobileCloud.api.rpc.Response;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.command.RemoteCommand;
import org.openmobster.core.mobileCloud.api.ui.framework.command.AppException;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.NavigationContext;


/**
 * @author openmobster@gmail.com
 */
public class UploadErrorLog implements RemoteCommand
{
	public void doAction(CommandContext commandContext) 
	{
		try
		{
			Registry registry = Registry.getActiveInstance();
			Configuration configuration = Configuration.getInstance(registry.getContext());
			String errorLog = ErrorHandler.getInstance().generateReport();
			
			//Go ahead and activate the device now
			Request request = new Request(commandContext.getTarget());
			request.setAttribute("user.id", configuration.getEmail());
			request.setAttribute("error.log", errorLog);			
			request.setAttribute("device.platform", "android");
			
			Response response = MobileService.invoke(request);
			if(response == null || !response.getStatusCode().equals("204"))
			{
				throw new RuntimeException("ErrorLog Transfer Failed!!!");
			}
		}
		catch(Exception e)
		{
			ErrorHandler.getInstance().handle(new SystemException(this.getClass().getName(), "doAction", new Object[]{					
				"Target Command:"+commandContext.getTarget(),
				"Exception :"+ e.toString(),
				"Message :"+ e.getMessage()
			}));
			throw new AppException();
		}	
	}

	public void doViewBefore(CommandContext commandContext) 
	{
		Context context = Registry.getActiveInstance().getContext();
		Toast.makeText(context, "ErrorLog Transfer in progress....", 
		Toast.LENGTH_SHORT).show();
	}
	
	public void doViewAfter(CommandContext commandContext) 
	{					
		Context context = Registry.getActiveInstance().getContext();
		Toast.makeText(context, "ErrorLog Transfer finished....", 
		Toast.LENGTH_SHORT).show();
		
		NavigationContext navigationContext = Services.getInstance().getNavigationContext();
		navigationContext.home();
	}

	public void doViewError(CommandContext commandContext) 
	{
		Activity currentActivity = (Activity)Registry.getActiveInstance().getContext();
		ViewHelper.getOkModal(currentActivity, "", 
		"ErrorLog Transfer Failed!!!").
		show();
	}		
}
