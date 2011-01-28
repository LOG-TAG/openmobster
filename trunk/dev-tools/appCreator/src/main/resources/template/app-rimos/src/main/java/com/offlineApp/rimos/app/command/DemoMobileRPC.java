/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.offlineApp.rimos.app.command;

import net.rim.device.api.ui.component.Status;
import net.rim.device.api.ui.component.Dialog;

import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.command.AsyncCommand;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.NavigationContext;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.service.Request;
import org.openmobster.core.mobileCloud.api.service.Response;
import org.openmobster.core.mobileCloud.api.service.MobileService;

/**
 * @author openmobster@gmail.com
 *
 */
public final class DemoMobileRPC implements AsyncCommand
{
	public void doViewBefore(CommandContext commandContext)
	{		
	}

	public void doAction(CommandContext commandContext) 
	{
		try
		{
			Request request = new Request("/demo/mobile-rpc");	
			request.setAttribute("param1", "paramValue1");
			request.setAttribute("param2", "paramValue2");
			Response response = new MobileService().invoke(request);
			
			commandContext.setAttribute("param1", response.getAttribute("param1"));
			commandContext.setAttribute("param2", response.getAttribute("param2"));
		}
		catch(Exception e)
		{
			throw new RuntimeException(e.toString());
		}
	}	
	
	public void doViewAfter(CommandContext commandContext)
	{
		String param1 = (String)commandContext.getAttribute("param1");
		String param2 = (String)commandContext.getAttribute("param2");
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("Param1: "+param1+"\n\n\n");
		buffer.append("Param2: "+param2);
		
		Dialog.alert(buffer.toString());
	}
	
	public void doViewError(CommandContext commandContext)
	{
		Status.show(this.getClass().getName()+" had an error!!");
	}
}
