/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.manager.gui.command;

import java.util.Vector;

import org.openmobster.android.api.rpc.MobileService;
import org.openmobster.android.api.rpc.Request;
import org.openmobster.android.api.rpc.Response;
import org.openmobster.android.api.rpc.ServiceInvocationException;
import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;

import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.command.RemoteCommand;
import org.openmobster.core.mobileCloud.api.ui.framework.command.AppException;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.NavigationContext;
import org.openmobster.core.mobileCloud.api.ui.framework.resources.AppResources;


import android.app.Activity;

/**
 * @author openmobster@gmail.com
 */
public class LoadAppStore implements RemoteCommand
{
	private static String myScreen = "appstore";
	
	public void doAction(CommandContext commandContext) 
	{		
		try
		{
			Request request = new Request("moblet-management://appStore");
			request.setAttribute("action", "getRegisteredApps");
			request.setAttribute("platform", "android");
			
			Response response = MobileService.invoke(request);	
			
			Vector uris = response.getListAttribute("uris");
			Vector names = response.getListAttribute("names");
			Vector descs = response.getListAttribute("descs");
			Vector downloadUrls = response.getListAttribute("downloadUrls");
			
												
			//Put this into state management			
			NavigationContext navContext = NavigationContext.getInstance();			
			navContext.setAttribute(myScreen, "uris", uris);			
			navContext.setAttribute(myScreen, "names", names);			
			navContext.setAttribute(myScreen, "descs", descs);			
			navContext.setAttribute(myScreen, "downloadUrls", downloadUrls);			
		}		
		catch(ServiceInvocationException sie)
		{
			ErrorHandler.getInstance().handle(new SystemException(this.getClass().getName(), "doAction", new Object[]{
				"Loading the AppStore....",
				"Target Command:"+commandContext.getTarget()				
			}));
			throw new RuntimeException(sie.toString());
		}		
	}

	public void doViewBefore(CommandContext commandContext) 
	{		
	}
	
	public void doViewAfter(CommandContext commandContext) 
	{				
		Services.getInstance().getNavigationContext().navigate(myScreen);				
	}

	public void doViewError(CommandContext commandContext) 
	{
		AppResources resources = Services.getInstance().getResources();
		AppException appException = commandContext.getAppException();
		
		Activity currentActivity = (Activity)Registry.getActiveInstance().getContext();
		ViewHelper.getOkModal(currentActivity, "", 
		resources.localize(appException.getMessageKey(), appException.getMessageKey())).
		show();
	}					
}
