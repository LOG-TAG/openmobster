/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.showcase.app.command.framework;

import org.openmobster.core.mobileCloud.api.ui.framework.command.AppException;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.command.AsyncCommand;
import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;

import android.app.Activity;
import android.widget.Toast;


/**
 * @author openmobster@gmail.com
 *
 */
public final class AjaxCommand implements AsyncCommand
{
	public void doViewBefore(CommandContext commandContext)
	{
		//Show a message before Action phase
		Activity activity = (Activity)commandContext.getAppContext();
		Toast.makeText(activity, 
				"Pre-Action Processing in progess", 
				Toast.LENGTH_LONG).show();
	}

	public void doAction(CommandContext commandContext) 
	{
		try
		{
			//sleep for 5 seconds simulating an operational delay
			Thread.currentThread().sleep(5000); 
		}
		catch(Exception e)
		{
			AppException appe = new AppException();
			appe.setMessage(e.getMessage());
			ErrorHandler.getInstance().handle(appe);
			
			throw appe;
		}
	}	
	
	public void doViewAfter(CommandContext commandContext)
	{
		//Show message upon successful completion
		Activity currentActivity = (Activity)Registry.getActiveInstance().getContext();
		ViewHelper.getOkModal(currentActivity, "Ajax Command", "Command Execution Finished!!").
		show();
	}
	
	public void doViewError(CommandContext commandContext)
	{
		//Show message upon App Exception
		Activity currentActivity = (Activity)Registry.getActiveInstance().getContext();
		ViewHelper.getOkModal(currentActivity, "App Error", 
		this.getClass().getName()+" had an error!!\n\n"+commandContext.getAppException().getMessage()).
		show();
	}
}
