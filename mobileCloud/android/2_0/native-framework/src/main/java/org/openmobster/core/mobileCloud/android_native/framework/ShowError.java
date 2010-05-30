/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.android_native.framework;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.resources.AppResources;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.SystemLocaleKeys;


/**
 * @author openmobster@gmail.com
 *
 */
final class ShowError
{
	public static void showGenericError(final CommandContext commandContext)
	{
		final Activity currentActivity = (Activity)Registry.getActiveInstance().
		getContext();
				
		AppResources appResources = Services.getInstance().getResources();
		
		String errorTitle = appResources.localize(
		SystemLocaleKeys.system_error, 
		"system_error");
		
		String errorMsg = appResources.localize(
		SystemLocaleKeys.unknown_system_error, 
		"unknown_system_error");
		
		//Show the dialog
		AlertDialog alert = new AlertDialog.Builder(currentActivity).
    	setTitle(errorTitle).
    	setMessage(errorMsg).
    	setCancelable(false).
    	create();
    	
    	alert.setButton(DialogInterface.BUTTON_POSITIVE, "OK", 
			new DialogInterface.OnClickListener() 
			{
				
				public void onClick(DialogInterface dialog, int status)
				{
					if(commandContext.getTarget().indexOf("startup")!=-1)
					{
						currentActivity.finish();
					}
					else
					{
						dialog.cancel();
					}
				}
			}
    	);
    	
    	alert.show();
	}
	
	public static void showBootstrapError(final Activity currentActivity)
	{				
		//Show the dialog
		AlertDialog alert = new AlertDialog.Builder(currentActivity).
    	setTitle("System Error").
    	setMessage("Moblet Bootstrap Failed").
    	setCancelable(false).
    	create();
    	
    	alert.setButton(DialogInterface.BUTTON_POSITIVE, "OK", 
			new DialogInterface.OnClickListener() 
			{
				
				public void onClick(DialogInterface dialog, int status)
				{
					currentActivity.finish();
				}
			}
    	);
    	
    	alert.show();
	}
}
