/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.rim_native.framework.sample.commands;

import net.rim.device.api.ui.component.Status;

import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.command.AppException;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.command.LocalCommand;

/**
 * @author openmobster@gmail.com
 *
 */
public final class DemoLocalCommand implements LocalCommand
{
	public void doViewBefore(CommandContext commandContext)
	{
		Status.show("LocalCommand about to execute........");		
	}

	public void doAction(CommandContext commandContext) 
	{
		try
		{
			System.out.println("-------------------------------------------------------");
			System.out.println("Demo Local Command successfully executed...............");
			System.out.println("-------------------------------------------------------");
		}
		catch(Exception e)
		{
			throw new RuntimeException(e.toString());
		}
	}	
	
	public void doViewAfter(CommandContext commandContext)
	{
		Services.getInstance().getNavigationContext().navigate("local");
	}
	
	public void doViewError(CommandContext commandContext)
	{
		Status.show("DemoLocalCommand had an error!!");
	}
}
