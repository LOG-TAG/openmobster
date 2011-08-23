/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

/**
 * 
 */
package org.openmobster.core.mobileCloud.manager.gui.command;

import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.module.bus.Bus;
import org.openmobster.core.mobileCloud.android.module.bus.Invocation;
import org.openmobster.core.mobileCloud.android.module.bus.InvocationResponse;

import org.openmobster.core.mobileCloud.api.ui.framework.navigation.NavigationContext;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.command.RemoteCommand;

/**
 * @author openmobster@gmail
 *
 */
public class CometStatus implements RemoteCommand
{
	public void doViewBefore(CommandContext commandContext)
	{		
	}
	
	public void doAction(CommandContext commandContext)
	{	
		try
		{
			Invocation invocation = new Invocation("org.openmobster.core.mobileCloud.android.invocation.CometStatusHandler");
			InvocationResponse response = Bus.getInstance().invokeService(invocation);
			
			String status = response.getValue("status");
			commandContext.setAttribute("status", status);
		}
		catch(Exception e)
		{
			ErrorHandler.getInstance().handle(new SystemException(this.getClass().getName(), "doAction", new Object[]{
				"Checking Comet Status",
				"Target Command:"+commandContext.getTarget()				
			}));
			throw new RuntimeException(e.toString());
		}
	}

	public void doViewAfter(CommandContext commandContext)
	{
		String status = (String)commandContext.getAttribute("status");
		NavigationContext navigationContext = NavigationContext.getInstance();
		
		navigationContext.setAttribute("cometConfig", "status", status);		
		navigationContext.navigate("cometConfig");
	}

	public void doViewError(CommandContext commandContext)
	{	
	}
}
