/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.test.sync;

import net.rim.device.api.ui.component.Dialog;

import org.openmobster.core.mobileCloud.api.system.CometUtil;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.command.AppException;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.moblet.Moblet;
import org.openmobster.core.mobileCloud.kernel.DeviceContainer;
import org.openmobster.core.mobileCloud.rim_native.framework.SystemLocaleKeys;
import org.openmobster.core.mobileCloud.rimos.module.bus.Bus;
import org.openmobster.core.mobileCloud.rimos.module.bus.InvocationHandler;
import org.openmobster.core.mobileCloud.api.push.MobilePush;
import org.openmobster.core.mobileCloud.api.ui.framework.command.LocalCommand;


/**
 * @author openmobster@gmail.com
 *
 */
public final class SyncTestSuiteBootstrapCommand implements LocalCommand
{
	public void doViewBefore(CommandContext commandContext)
	{
		
	}
	
	public void doAction(CommandContext commandContext) 
	{	
		try
		{
			DeviceContainer.getInstance().startup();
			Moblet.getInstance().startup();
									
			boolean wasChannelBootupStarted = CometUtil.subscribeChannels();			
			commandContext.setAttribute("isChannelBootActive", new Boolean(wasChannelBootupStarted));
			
			Bus bus = Bus.getInstance();
			
			//Register the ClientStorageListener as a Service Bus InvocationHandler
			InvocationHandler handler = new TestStorageListener("testServerBean.testMapping");
			bus.register(handler);
						
			//Register New Email Push Listener
			MobilePush.registerPushListener(new EmailPushListener());
		}
		catch(Exception e)
		{
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
