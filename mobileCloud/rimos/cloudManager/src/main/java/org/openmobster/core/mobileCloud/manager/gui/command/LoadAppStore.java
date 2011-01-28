/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.manager.gui.command;

import java.util.Vector;

import net.rim.device.api.ui.component.Dialog;

import org.openmobster.core.mobileCloud.rimos.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.rimos.errors.SystemException;

import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.command.RemoteCommand;
import org.openmobster.core.mobileCloud.api.ui.framework.command.AppException;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.NavigationContext;
import org.openmobster.core.mobileCloud.api.ui.framework.resources.AppResources;

import org.openmobster.core.mobileCloud.api.service.Request;
import org.openmobster.core.mobileCloud.api.service.Response;
import org.openmobster.core.mobileCloud.api.service.MobileService;
import org.openmobster.core.mobileCloud.api.service.ServiceInvocationException;

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
		Dialog.alert(resources.localize(appException.getMessageKey(), appException.getMessageKey()));
	}					
}
