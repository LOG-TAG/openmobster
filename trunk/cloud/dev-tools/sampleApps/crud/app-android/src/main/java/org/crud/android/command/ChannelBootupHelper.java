/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.crud.android.command;

import org.openmobster.android.api.sync.MobileBean;
import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.command.RemoteCommand;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.NavigationContext;

import android.app.Activity;
import android.widget.Toast;

/**
 * 
 * @author openmobster@gmail.com
 */
public class ChannelBootupHelper implements RemoteCommand
{
	public void doViewBefore(CommandContext commandContext)
	{
		Activity activity = (Activity)commandContext.getAppContext();
		Toast.makeText(activity, 
				"Waiting for the 'crm_ticket_channel' to finish bootstrapping....", 
				Toast.LENGTH_LONG).show();
	}

	public void doAction(CommandContext commandContext)
	{
		try
		{
			int counter = 10;
			while(!MobileBean.isBooted("crm_ticket_channel") && counter>0)
			{
				Thread.currentThread().sleep(1000);
				counter--;
			}
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public void doViewAfter(CommandContext commandContext)
	{		
		NavigationContext.getInstance().home();
	}

	public void doViewError(CommandContext commandContext)
	{
		Activity activity = (Activity)commandContext.getAppContext();
		ViewHelper.getOkModalWithCloseApp(activity, "App Error", "The 'crm_ticket_channel' is not ready. Please launch the App again in a few minutes").
		show();
	}
}
