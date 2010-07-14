/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.manager.gui.command;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.Status;

import org.openmobster.core.mobileCloud.manager.gui.LocaleKeys;
import org.openmobster.core.mobileCloud.rimos.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.rimos.errors.SystemException;
import org.openmobster.core.mobileCloud.rimos.module.bus.Bus;
import org.openmobster.core.mobileCloud.rimos.module.bus.BusException;
import org.openmobster.core.mobileCloud.rimos.module.bus.Invocation;

import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.command.RemoteCommand;
import org.openmobster.core.mobileCloud.api.ui.framework.command.AppException;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.NavigationContext;
import org.openmobster.core.mobileCloud.api.ui.framework.resources.AppResources;


/**
 * @author openmobster@gmail.com
 */
public class SwitchSSLMode implements RemoteCommand
{
	public void doAction(CommandContext commandContext) 
	{		
		try
		{
			Invocation invocation = new Invocation("org.openmobster.core.mobileCloud.invocation.SwitchSecurityMode");															
			Bus.getInstance().invokeService(invocation);	
		}		
		catch(BusException be)
		{
			ErrorHandler.getInstance().handle(new SystemException(this.getClass().getName(), "doAction", new Object[]{
				"Restarting the Comet Daemon....",
				"Target Command:"+commandContext.getTarget()				
			}));
			throw new RuntimeException(be.toString());
		}
	}

	public void doViewBefore(CommandContext commandContext) 
	{								
		AppResources resources = Services.getInstance().getResources();
		Status.show(resources.localize(LocaleKeys.ssl_mod_in_progress, LocaleKeys.ssl_mod_in_progress));
	}
	
	public void doViewAfter(CommandContext commandContext) 
	{				
		AppResources resources = Services.getInstance().getResources();
		Status.show(resources.localize(LocaleKeys.ssl_mod_success, LocaleKeys.ssl_mod_success));
		
		NavigationContext navigationContext = Services.getInstance().getNavigationContext();
		navigationContext.home();
	}

	public void doViewError(CommandContext commandContext) 
	{
		AppResources resources = Services.getInstance().getResources();
		AppException appException = commandContext.getAppException();
		Dialog.alert(resources.localize(appException.getMessageKey(), appException.getMessageKey()));
	}					
}
