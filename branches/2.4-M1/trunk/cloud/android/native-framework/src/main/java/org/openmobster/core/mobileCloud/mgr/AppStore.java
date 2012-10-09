/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.mgr;

import java.util.Vector;

import org.openmobster.android.api.rpc.MobileService;
import org.openmobster.android.api.rpc.Request;
import org.openmobster.android.api.rpc.Response;
import org.openmobster.core.mobileCloud.android.module.bus.Bus;
import org.openmobster.core.mobileCloud.android.module.bus.Invocation;
import org.openmobster.core.mobileCloud.android.module.bus.InvocationResponse;
import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

/**
 *
 * @author openmobster@gmail.com
 */
public final class AppStore
{
	private Activity currentActivity;
	
	private AppStore(Activity currentActivity)
	{
		this.currentActivity = currentActivity;
	}
	
	public static AppStore getInstance(Activity currentActivity)
	{
		return new AppStore(currentActivity);
	}
	
	public void start()
	{
		try
		{
			CommandContext commandContext = new CommandContext();
			
			commandContext.setAttribute("task", new LoadAppStoreTask());
			commandContext.setAttribute("currentActivity", currentActivity);
			
			TaskExecutor taskExecutor = new TaskExecutor("Enterprise App Store","Loading the App Store....",
					null,currentActivity);
			taskExecutor.execute(commandContext);
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			ViewHelper.getOkModal(currentActivity, "System Error", "Loading the App Store Failed").show();
		}
	}
}
