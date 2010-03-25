/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.android_native.framework;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.command.AppException;
import org.openmobster.core.mobileCloud.api.ui.framework.command.Command;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandService;

/**
 * @author openmobster@gmail.com
 *
 */
final class LongAsyncTask extends AsyncTask<CommandContext,Integer,CommandContext>
{
	private ProgressDialog progressDialog;
	
	LongAsyncTask()
	{
		Activity currentActivity = (Activity)Registry.getActiveInstance().
		getContext();
		this.progressDialog = new ProgressDialog(currentActivity);
		this.progressDialog.setTitle("");
		this.progressDialog.setMessage("Loading...");
		this.progressDialog.setCancelable(false);
	}
	
	@Override
	protected CommandContext doInBackground(CommandContext... input)
	{
		CommandContext commandContext = input[0];
		try
		{			
			CommandService service = Services.getInstance().getCommandService();			
			Command command = service.find(commandContext.getTarget());
			
			this.publishProgress(0);
			
			//Perform action
			try
			{
				command.doAction(commandContext);
			}
			catch(AppException ape)
			{
				service.reportAppException(commandContext, ape);
				return commandContext;
			}
			
			return commandContext;
		}
		catch(Exception e)
		{
			//report to ErrorHandling system
			ErrorHandler.getInstance().handle(new SystemException(this.getClass().getName(), "execute", new Object[]{
				"Message:"+e.getMessage(),
				"Exception:"+e.toString(),
				"Target Command:"+commandContext.getTarget()
			}));
			commandContext.setAttribute("system_error", e);
			
			return commandContext;
		}
	}

	@Override
	protected void onPostExecute(CommandContext result)
	{
		this.progressDialog.dismiss();
		
		CommandService service = Services.getInstance().getCommandService();			
		Command command = service.find(result.getTarget());
		
		//Check for any errors
		Exception systemError = (Exception)result.getAttribute("system_error");		
		if(systemError != null)
		{
			ShowError.showGenericError(result);
			return;
		}
		
		//Check for an AppException
		if(result.hasErrors())
		{
			command.doViewError(result);
			return;
		}
		
		//Everything is great!!
		command.doViewAfter(result);
	}

	@Override
	protected void onProgressUpdate(Integer... values)
	{
		this.progressDialog.show();
	}
}
