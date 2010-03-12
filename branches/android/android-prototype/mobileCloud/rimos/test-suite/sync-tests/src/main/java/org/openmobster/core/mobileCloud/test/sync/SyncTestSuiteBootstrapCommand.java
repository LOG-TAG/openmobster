/**
 * Copyright (c) {2003,2009} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.test.sync;

import test.openmobster.core.mobileCloud.rimos.testsuite.TestSuite;
import test.openmobster.core.mobileCloud.rimos.testsuite.moblet.TestSuiteBootstrapCommand;
import test.openmobster.core.mobileCloud.rimos.testsuite.moblet.ActivationUtil;
import test.openmobster.core.mobileCloud.rimos.testsuite.TestContext;

import org.openmobster.core.mobileCloud.api.system.CometUtil;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.command.AppException;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.moblet.Moblet;
import org.openmobster.core.mobileCloud.kernel.DeviceContainer;
import org.openmobster.core.mobileCloud.rimos.module.bus.Bus;
import org.openmobster.core.mobileCloud.rimos.module.bus.InvocationHandler;
import org.openmobster.core.mobileCloud.api.push.MobilePush;



/**
 * @author openmobster@gmail.com
 *
 */
public final class SyncTestSuiteBootstrapCommand extends TestSuiteBootstrapCommand
{
	public void doAction(CommandContext commandContext) 
	{	
		try
		{
			DeviceContainer.getInstance().startup();
			Moblet.getInstance().startup();
			
			ActivationUtil.activateDevice();
			
			CometUtil.subscribeChannels();
		}
		catch(Exception e)
		{
			throw new AppException();
		}
	}
	
	public void doViewAfter(CommandContext commandContext)
	{
		try
		{
			Bus bus = Bus.getInstance();
			
			//Register the ClientStorageListener as a Service Bus InvocationHandler
			InvocationHandler handler = new TestStorageListener("testServerBean.testMapping");
			bus.register(handler);
						
			//Register New Email Push Listener
			MobilePush.registerPushListener(new EmailPushListener());
			
			TestSuite suite = new TestSuite();
			TestContext context = new TestContext();
			suite.setContext(context);
			context.setAttribute("service", "testServerBean");
			context.setAttribute("identifier", "IMEI:8675309");
			
			suite.load();
			suite.execute();
		
			Services.getInstance().getNavigationContext().setHome("home");
			Services.getInstance().getNavigationContext().home();
		}
		catch(Exception e)
		{
			throw new RuntimeException(e.toString());
		}
	}
}
