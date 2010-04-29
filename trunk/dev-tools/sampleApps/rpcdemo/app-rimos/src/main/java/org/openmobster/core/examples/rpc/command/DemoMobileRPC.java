/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.examples.rpc.command;

import net.rim.device.api.ui.component.Status;

import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.command.RemoteCommand;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.NavigationContext;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.service.Request;
import org.openmobster.core.mobileCloud.api.service.Response;
import org.openmobster.core.mobileCloud.api.service.MobileService;

/**
 * @author openmobster@gmail.com
 *
 */
public final class DemoMobileRPC implements RemoteCommand
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
			
			NavigationContext navigation = Services.getInstance().getNavigationContext();
			navigation.setAttribute("home", "param1", response.getAttribute("param1"));
			navigation.setAttribute("home", "param2", response.getAttribute("param2"));
		}
		catch(Exception e)
		{
			throw new RuntimeException(e.toString());
		}
	}	
	
	public void doViewAfter(CommandContext commandContext)
	{
		NavigationContext navigation = Services.getInstance().getNavigationContext();
		navigation.home();
	}
	
	public void doViewError(CommandContext commandContext)
	{
		Status.show(this.getClass().getName()+" had an error!!");
	}
}
