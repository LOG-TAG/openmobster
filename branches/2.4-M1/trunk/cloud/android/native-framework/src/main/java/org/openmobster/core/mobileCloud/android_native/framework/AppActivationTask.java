/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android_native.framework;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.openmobster.android.api.rpc.MobileService;
import org.openmobster.android.api.rpc.Request;
import org.openmobster.android.api.rpc.Response;
import org.openmobster.android.api.rpc.ServiceInvocationException;
import org.openmobster.core.mobileCloud.android.configuration.Configuration;
import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.module.bus.Bus;
import org.openmobster.core.mobileCloud.android.module.bus.BusException;
import org.openmobster.core.mobileCloud.android.module.bus.Invocation;
import org.openmobster.core.mobileCloud.android.module.dm.DeviceManager;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.SystemLocaleKeys;
import org.openmobster.core.mobileCloud.api.ui.framework.command.AppException;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.resources.AppResources;
import org.openmobster.core.mobileCloud.moblet.BootupConfiguration;

/**
 * @author openmobster@gmail.com
 *
 */
final class AppActivationTask extends AsyncTask<CommandContext,Integer,CommandContext>
{
	private ProgressDialog progressDialog;
	private Activity currentActivity;
	
	AppActivationTask(Activity currentActivity)
	{
		this.currentActivity = currentActivity;
		
		this.progressDialog = new ProgressDialog(currentActivity);
		this.progressDialog.setTitle("");
		this.progressDialog.setMessage("App Activation in Progress.....");
		this.progressDialog.setCancelable(false);
	}
	
	@Override
	protected CommandContext doInBackground(CommandContext... input)
	{
		CommandContext commandContext = input[0];
		try
		{			
			if(commandContext.isTimeoutActivated())
			{
				Timer timer = new Timer();
				timer.schedule(new RemoteCommandExpiry(commandContext), 
				15000);
			}
			else
			{
				Timer timer = new Timer();
				timer.schedule(new RemoteCommandExpiry(commandContext), 
				45000); //should never take longer this...this is a forced abort
			}
			
			this.publishProgress(0);
			
			//Perform action
			try
			{
				this.performActivation(commandContext);
			}
			catch(AppException ape)
			{
				commandContext.setAppException(ape);
				return commandContext;
			}
			finally
			{
				commandContext.setAttribute("action-finished", "");
			}
			
			return commandContext;
		}
		catch(Exception e)
		{
			//report to ErrorHandling system
			ErrorHandler.getInstance().handle(new SystemException(this.getClass().getName(), "execute", new Object[]{
				"Message:"+e.getMessage(),
				"Exception:"+e.toString()
			}));
			commandContext.setAttribute("system_error", e);
			
			return commandContext;
		}
	}

	@Override
	protected void onPostExecute(CommandContext result)
	{
		//if this command timeout, then it should not execute the view phase
		if(result.getAttribute("timer-expired") != null)
		{
			return;
		}
		
		this.progressDialog.dismiss();
		
		//Check for any errors
		Exception systemError = (Exception)result.getAttribute("system_error");		
		if(systemError != null)
		{
			AppResources appResources = Services.getInstance().getResources();
			
			String errorTitle = appResources.localize(
			SystemLocaleKeys.system_error, 
			"system_error");
			
			String errorMsg = appResources.localize(
			SystemLocaleKeys.unknown_system_error, 
			"unknown_system_error");
			
			//Show the dialog
	    	ViewHelper.getOkModal(this.currentActivity, errorTitle, errorMsg).show();
			return;
		}
		
		if(result.hasErrors())
		{
			//Show the dialog
	    	ViewHelper.getOkModal(this.currentActivity, "Activation Error", result.getAppException().getMessageKey()).show();
			return;
		}
		
		//Everything is great!!
		ViewHelper.getOkModal(this.currentActivity, "Activation", "App is successfully Activated").show();
	}

	@Override
	protected void onProgressUpdate(Integer... values)
	{
		if(values[0] != -1)
		{
			this.progressDialog.show();
		}
		else
		{	
			AlertDialog timeoutDialog = ViewHelper.getOkModal(currentActivity, 
			"Command Timed Out", 
			"RemoteCommand is taking longer to execute than expected");
			
			this.progressDialog.dismiss();
			timeoutDialog.show();
		}
	}
	//-------------------------------------------------------------------------------------
	private class RemoteCommandExpiry extends TimerTask
	{
		private CommandContext commandContext;
		
		private RemoteCommandExpiry(CommandContext commandContext)
		{
			this.commandContext = commandContext;
		}
		
		public void run()
		{
			try
			{
				if(commandContext.getAttribute("action-finished") == null)
				{
					//remote command is still busy
					commandContext.setAttribute("timer-expired", "");	
					AppActivationTask.this.publishProgress(-1);		
				}
			}
			finally
			{
				//makes sure this is cleaned up
				this.cancel();
			}
		}
	}
	
	private void performActivation(CommandContext commandContext) throws AppException
	{
		Context context = Registry.getActiveInstance().getContext();
		String server = null;
		String email = null;
		String deviceIdentifier = null;
		boolean isReactivation = false;
		try
		{			
			server = (String)commandContext.getAttribute("server");
			email = (String)commandContext.getAttribute("email");
			String password = (String)commandContext.getAttribute("password");
			String port = (String)commandContext.getAttribute("port");
												
			Configuration conf = Configuration.getInstance(context);
			if(conf.isActive())
			{
				isReactivation = true;
			}
			
			
			BootupConfiguration.bootup(server, port);
			
			deviceIdentifier = conf.getDeviceId();
			
			
			//Go ahead and activate the device now
			Request request = new Request("provisioning");
			request.setAttribute("email", email);
			request.setAttribute("password", password);			
			request.setAttribute("identifier", deviceIdentifier);
			
			Response response = MobileService.invoke(request);
						
			if(response.getAttribute("idm-error") == null)
			{
				//Success Scenario
				this.processProvisioningSuccess(email,response);
				
				//Start the CometDaemon
				if(!isReactivation)
				{
					Invocation invocation = new Invocation("org.openmobster.core.mobileCloud.android.invocation.StartCometDaemon");
					Bus.getInstance().invokeService(invocation);
				}
				else
				{
					Invocation invocation = new Invocation("org.openmobster.core.mobileCloud.android.invocation.CometRecycleHandler");
					Bus.getInstance().invokeService(invocation);
				}
			}
			else
			{
				//Error Scenario
				String errorKey = response.getAttribute("idm-error");
																
				//report to ErrorHandling system
				SystemException syse = new SystemException(this.getClass().getName(), "doAction", new Object[]{
					"Error Key:"+errorKey,					
					"Target Command:"+commandContext.getTarget(),
					"Device Identifier:"+ deviceIdentifier,
					"Server:"+ server,
					"Email:"+email
				});
												
				ErrorHandler.getInstance().handle(syse);
				AppException appException = new AppException();
				appException.setMessageKey(errorKey);
				throw appException;
			}	
		}
		catch(ServiceInvocationException sie)
		{
			//sie.printStackTrace(System.out);			
			ErrorHandler.getInstance().handle(new SystemException(this.getClass().getName(), "doAction", new Object[]{					
				"Target Command:"+commandContext.getTarget(),
				"Device Identifier:"+ deviceIdentifier,
				"Server:"+ server,
				"Email:"+email
			}));
			throw new RuntimeException(sie.toString());
		}
		catch(BusException be)
		{
			//be.printStackTrace(System.out);			
			ErrorHandler.getInstance().handle(new SystemException(this.getClass().getName(), "doAction", new Object[]{
				"Trying to start the Comet Daemon....",
				"Target Command:"+commandContext.getTarget(),
				"Device Identifier:"+ deviceIdentifier,
				"Server:"+ server,
				"Email:"+email
			}));
			throw new RuntimeException(be.toString());
		}		
	}
	
	private void processProvisioningSuccess(String email,Response response)
	{
		Context context = Registry.getActiveInstance().getContext();
		Configuration configuration = Configuration.getInstance(context);
		
		String authenticationHash = response.getAttribute("authenticationHash");
		configuration.setEmail(email);
		configuration.setAuthenticationHash(authenticationHash);
		configuration.setAuthenticationNonce(authenticationHash);
		configuration.setActive(true);
		
		configuration.save(context);
		
		//Broadcast deviceManagement meta data to the server
		DeviceManager dm = DeviceManager.getInstance();
		dm.sendOsCallback();
	}	
}
