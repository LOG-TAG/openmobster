/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.offlineApp.rimos.app.command;

import net.rim.device.api.ui.component.Status;

import org.openmobster.core.mobileCloud.rimos.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.rimos.errors.SystemException;
import org.openmobster.core.mobileCloud.rimos.module.bus.Bus;
import org.openmobster.core.mobileCloud.rimos.module.bus.SyncInvocation;

import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.command.RemoteCommand;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.NavigationContext;


/**
 * @author openmobster@gmail.com
 */
public class ResetChannel implements RemoteCommand
{
	public void doAction(CommandContext commandContext) 
	{		
		try
		{			
			SyncInvocation syncInvocation = new SyncInvocation("org.openmobster.core.mobileCloud.invocation.SyncInvocationHandler", 
			SyncInvocation.bootSync, "offlineapp_demochannel");		
			Bus.getInstance().invokeService(syncInvocation);
		}		
		catch(Exception be)
		{
			ErrorHandler.getInstance().handle(new SystemException(this.getClass().getName(), "doAction", new Object[]{
				"Manually Synchronizing (offlineapp_demochannel)",
				"Target Command:"+commandContext.getTarget()				
			}));
			throw new RuntimeException(be.toString());
		}
	}

	public void doViewBefore(CommandContext commandContext) 
	{		
	}
	
	public void doViewAfter(CommandContext commandContext) 
	{				
		Status.show("Channel is succesfully reset!!");
		NavigationContext.getInstance().home();
	}

	public void doViewError(CommandContext commandContext) 
	{
		Status.show(this.getClass().getName()+" had an error!!");
	}					
}
