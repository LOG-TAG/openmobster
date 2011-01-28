/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.rim_native.framework.sample.commands;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.Status;

import org.openmobster.core.mobileCloud.api.ui.framework.command.AppException;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.command.RemoteCommand;

/**
 * @author openmobster@gmail.com
 *
 */
public final class RemoteAppExceptionCommand implements RemoteCommand
{
	public void doViewBefore(CommandContext commandContext)
	{
		Status.show("RemoteAppExceptionCommand about to execute........");		
	}

	public void doAction(CommandContext commandContext) 
	{
		try
		{
			Thread.currentThread().sleep(10000);
			System.out.println("-------------------------------------------------------");
			System.out.println("RemoteAppExceptionCommand successfully executed...............");
			System.out.println("-------------------------------------------------------");
			throw new AppException();
		}
		catch(Exception e)
		{
			throw new AppException();
		}
	}	
	
	public void doViewAfter(CommandContext commandContext)
	{		
	}
	
	public void doViewError(CommandContext commandContext)
	{
		Dialog.alert("RemoteAppException had an AppException....");
	}
}
