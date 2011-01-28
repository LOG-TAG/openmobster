/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.rim_native.framework.command;

import net.rim.device.api.ui.component.Dialog;

import org.openmobster.core.mobileCloud.moblet.Moblet;
import org.openmobster.core.mobileCloud.rim_native.framework.SystemLocaleKeys;

import org.openmobster.core.mobileCloud.api.system.CometUtil;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.AppPushListener;
import org.openmobster.core.mobileCloud.api.ui.framework.command.AppException;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.command.LocalCommand;


/**
 * @author openmobster@gmail.com
 *
 */
public final class MobletBootstrapCommand implements LocalCommand
{
	public void doViewBefore(CommandContext commandContext)
	{
		((AppPushListener)Services.getInstance().getPushListener()).start();
		Services.getInstance().getPushListener().clearNotification();
	}

	public void doAction(CommandContext commandContext) 
	{	
		try
		{
			Moblet.getInstance().startup();
			boolean wasChannelBootupStarted = CometUtil.subscribeChannels();
			
			commandContext.setAttribute("isChannelBootActive", new Boolean(wasChannelBootupStarted));
		}
		catch(Exception e)
		{
			//System.out.println("-----------------------------------------------------");
			//System.out.println("Exception: "+e.getMessage());
			//System.out.println("-----------------------------------------------------");
			throw new AppException();
		}
	}
	
	public void doViewAfter(CommandContext commandContext)
	{
		Services.getInstance().getNavigationContext().setHome("home");
		Services.getInstance().getNavigationContext().home();
	}
	
	public void doViewError(CommandContext commandContext)
	{
		Dialog.alert(Services.getInstance().getResources().localize(SystemLocaleKeys.moblet_startup_error, 
		SystemLocaleKeys.moblet_startup_error));			
		System.exit(1);
	}
}
