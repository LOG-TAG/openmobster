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

import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.command.LocalCommand;

/**
 * @author openmobster@gmail.com
 *
 */
public final class LocalExceptionCommand implements LocalCommand
{
	public void doViewBefore(CommandContext commandContext)
	{
		Status.show("LocalExceptionCommand about to execute........");		
	}

	public void doAction(CommandContext commandContext) 
	{
		System.out.println("-------------------------------------------------------");
		System.out.println("LocalExceptionCommand successfully executed...............");
		System.out.println("-------------------------------------------------------");
		throw new RuntimeException("LocalExceptionCommand!!");
	}	
	
	public void doViewAfter(CommandContext commandContext)
	{		
	}
	
	public void doViewError(CommandContext commandContext)
	{
		Dialog.alert("If I am here. This test failed!!");
	}
}
