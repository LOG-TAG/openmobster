/**
 * Copyright (c) {2003,2009} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.core.mobileCloud.rimos.testsuite.moblet;

import net.rim.device.api.ui.component.Dialog;

import org.openmobster.core.mobileCloud.api.system.CometUtil;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.command.AppException;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.command.RemoteCommand;
import org.openmobster.core.mobileCloud.moblet.Moblet;
import org.openmobster.core.mobileCloud.rim_native.framework.SystemLocaleKeys;
import test.openmobster.core.mobileCloud.rimos.testsuite.TestSuite;



/**
 * @author openmobster@gmail.com
 *
 */
public class TestSuiteBootstrapCommand implements RemoteCommand
{
	public void doViewBefore(CommandContext commandContext)
	{
		
	}

	public void doAction(CommandContext commandContext) 
	{	
		try
		{
			Moblet.getInstance().startup();
			ActivationUtil.activateDevice();
			
			CometUtil.subscribeChannels();
		}
		catch(Exception e)
		{
			System.out.println("---------------------------------------------------------------");
			System.out.println("Exception: "+e.getMessage());
			System.out.println("---------------------------------------------------------------");
			throw new AppException();
		}
	}
	
	public void doViewAfter(CommandContext commandContext)
	{
		try
		{
			TestSuite suite = new TestSuite();
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
	
	public void doViewError(CommandContext commandContext)
	{
		Dialog.alert(Services.getInstance().getResources().localize(SystemLocaleKeys.moblet_startup_error, SystemLocaleKeys.moblet_startup_error));			
		System.exit(1);
	}
}
