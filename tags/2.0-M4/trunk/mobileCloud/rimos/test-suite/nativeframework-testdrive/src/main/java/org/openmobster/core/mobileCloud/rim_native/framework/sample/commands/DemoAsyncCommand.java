/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.rim_native.framework.sample.commands;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.Status;

import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.command.AsyncCommand;

/**
 * @author openmobster@gmail.com
 *
 */
public final class DemoAsyncCommand implements AsyncCommand
{
	public void doViewBefore(CommandContext commandContext)
	{
		Status.show("AsyncCommand about to execute........");				
	}

	public void doAction(CommandContext commandContext) 
	{
		try
		{
			//Simulate network latency
			Thread.currentThread().sleep(10000);
			System.out.println("-------------------------------------------------------");
			System.out.println("Demo Async Command successfully executed...............");
			System.out.println("-------------------------------------------------------");						
		}
		catch(Exception e)
		{
			throw new RuntimeException(e.toString());
		}
	}	
	
	public void doViewAfter(CommandContext commandContext)
	{
		Dialog.alert("Async Command success...");
		
		//An Async Command should not navigate away from the screen that launch it...it can result in yucky UI errors
		//Services.getInstance().getNavigationContext().navigate("async");
	}
	
	public void doViewError(CommandContext commandContext)
	{
		Status.show("DemoAsyncCommand had an error!!");
	}
}
