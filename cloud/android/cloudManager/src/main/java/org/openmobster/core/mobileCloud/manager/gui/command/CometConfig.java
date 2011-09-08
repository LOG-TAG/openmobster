/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.manager.gui.command;

import org.openmobster.core.mobileCloud.android.module.bus.Bus;
import org.openmobster.core.mobileCloud.android.module.bus.BusException;
import org.openmobster.core.mobileCloud.android.module.bus.Invocation;
import org.openmobster.core.mobileCloud.android.module.bus.InvocationResponse;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;
import org.openmobster.core.mobileCloud.manager.gui.LocaleKeys;

import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.command.RemoteCommand;
import org.openmobster.core.mobileCloud.api.ui.framework.command.AppException;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.NavigationContext;
import org.openmobster.core.mobileCloud.api.ui.framework.resources.AppResources;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;


/**
 * @author openmobster@gmail.com
 */
public class CometConfig implements RemoteCommand
{
	public void doViewBefore(CommandContext commandContext) 
	{
		AppResources resources = Services.getInstance().getResources();
		Activity activity = Services.getInstance().getCurrentActivity();
		Toast.makeText(activity, resources.localize(LocaleKeys.push_restart, LocaleKeys.push_restart), 
		Toast.LENGTH_SHORT).show();
	}
	
	public void doAction(CommandContext commandContext) 
	{		
		try
		{
			Invocation invocation = new Invocation("org.openmobster.core.mobileCloud.android.invocation.CometConfigHandler");
			invocation.setValue("mode", (String)commandContext.getAttribute("mode"));
			String pollInterval = (String)commandContext.getAttribute("poll_interval");	
			
			if(pollInterval != null && pollInterval.trim().length()>0)
			{
				invocation.setValue("poll_interval", pollInterval);
			}						
			
			InvocationResponse response = Bus.getInstance().invokeService(invocation);			
			String status = response.getValue("status");
			commandContext.setAttribute("status", status);	
		}		
		catch(BusException be)
		{
			ErrorHandler.getInstance().handle(new SystemException(this.getClass().getName(), "doAction", new Object[]{
				"Restarting the Comet Daemon....",
				"Target Command:"+commandContext.getTarget()				
			}));
			throw new RuntimeException(be.toString());
		}
	}
		
	public void doViewAfter(CommandContext commandContext) 
	{				
		AppResources resources = Services.getInstance().getResources();
		Activity activity = Services.getInstance().getCurrentActivity();
		Toast.makeText(activity, resources.localize(LocaleKeys.push_restarted, LocaleKeys.push_restarted), 
		Toast.LENGTH_SHORT).show();
		
		String status = (String)commandContext.getAttribute("status");
		NavigationContext navigationContext = NavigationContext.getInstance();
		
		navigationContext.setAttribute("cometConfig", "status", status);		
		navigationContext.refresh();
	}

	public void doViewError(CommandContext commandContext) 
	{
		AppResources resources = Services.getInstance().getResources();
		AppException appException = commandContext.getAppException();
		
		Activity activity = Services.getInstance().getCurrentActivity();
		ViewHelper.getOkModal(activity, "", 
		resources.localize(appException.getMessageKey(), appException.getMessageKey())).
		show();				
	}					
}
