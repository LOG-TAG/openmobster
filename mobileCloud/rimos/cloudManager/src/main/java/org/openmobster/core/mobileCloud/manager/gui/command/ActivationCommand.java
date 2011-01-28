/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.manager.gui.command;

import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.Status;

import org.openmobster.core.mobileCloud.manager.gui.CommandKeys;
import org.openmobster.core.mobileCloud.manager.gui.LocaleKeys;
import org.openmobster.core.mobileCloud.rimos.configuration.Configuration;
import org.openmobster.core.mobileCloud.rimos.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.rimos.errors.SystemException;
import org.openmobster.core.mobileCloud.rimos.module.bus.Bus;
import org.openmobster.core.mobileCloud.rimos.module.bus.BusException;
import org.openmobster.core.mobileCloud.rimos.module.bus.Invocation;
import org.openmobster.core.mobileCloud.moblet.BootupConfiguration;

import org.openmobster.core.mobileCloud.api.service.Request;
import org.openmobster.core.mobileCloud.api.service.Response;
import org.openmobster.core.mobileCloud.api.service.MobileService;
import org.openmobster.core.mobileCloud.api.service.ServiceInvocationException;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.command.RemoteCommand;
import org.openmobster.core.mobileCloud.api.ui.framework.command.AppException;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.NavigationContext;
import org.openmobster.core.mobileCloud.api.ui.framework.resources.AppResources;


/**
 * @author openmobster@gmail.com
 */
public class ActivationCommand implements RemoteCommand
{
	public void doAction(CommandContext commandContext) 
	{
		String server = null;
		String email = null;
		String deviceIdentifier = null;
		boolean isReactivation = false;
		try
		{
			deviceIdentifier = "IMEI:"+DeviceInfo.getDeviceId();						
			server = (String)commandContext.getAttribute(CommandKeys.server);
			email = (String)commandContext.getAttribute(CommandKeys.email);
			String password = (String)commandContext.getAttribute(CommandKeys.password);
			String port = (String)commandContext.getAttribute("port");
												
			Configuration conf = Configuration.getInstance();
			if(conf.isActive())
			{
				isReactivation = true;
			}
			
			
			BootupConfiguration.bootup(server, port);
			
			
			//Go ahead and activate the device now
			Request request = new Request(CommandKeys.provisioning);
			request.setAttribute(CommandKeys.email, email);
			request.setAttribute(CommandKeys.password, password);			
			request.setAttribute(CommandKeys.device_identifier, deviceIdentifier);
			
			Response response = MobileService.invoke(request);
						
			if(response.getAttribute("idm-error") == null)
			{
				//Success Scenario
				this.processProvisioningSuccess(email,response);
				
				//Start the CometDaemon
				if(!isReactivation)
				{
					Invocation invocation = new Invocation("org.openmobster.core.mobileCloud.invocation.StartCometDaemon");
					Bus.getInstance().invokeService(invocation);
				}
				else
				{
					Invocation invocation = new Invocation("org.openmobster.core.mobileCloud.invocation.CometRecycleHandler");
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
			//System.out.println("----------------------------------------------------");
			//System.out.println("ServiceInvocationException: "+sie.getMessage());
			//System.out.println("ServiceInvocationException: "+sie.toString());
			//System.out.println("----------------------------------------------------");
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
			//System.out.println("----------------------------------------------------");
			//System.out.println("BusException: "+be.getMessage());
			//System.out.println("BusException: "+be.toString());
			//System.out.println("----------------------------------------------------");
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

	public void doViewBefore(CommandContext commandContext) 
	{
		AppResources resources = Services.getInstance().getResources();
		Status.show(resources.localize(LocaleKeys.activation_start, LocaleKeys.activation_start));
	}
	
	public void doViewAfter(CommandContext commandContext) 
	{				
		AppResources resources = Services.getInstance().getResources();
		Status.show(resources.localize(LocaleKeys.activation_success, LocaleKeys.activation_success));
		NavigationContext navigationContext = Services.getInstance().getNavigationContext();
		navigationContext.home();
	}

	public void doViewError(CommandContext commandContext) 
	{
		AppResources resources = Services.getInstance().getResources();
		AppException appException = commandContext.getAppException();
		Dialog.alert(resources.localize(appException.getMessageKey(), appException.getMessageKey()));
	}
	//-------------------------------------------------------------------------------------------------------------------------------
	private void processProvisioningSuccess(String email,Response response)
	{		
		Configuration configuration = Configuration.getInstance();
		
		String authenticationHash = response.getAttribute("authenticationHash");
		configuration.setEmail(email);
		configuration.setAuthenticationHash(authenticationHash);
		configuration.setAuthenticationNonce(authenticationHash);
		configuration.setActive(true);
		
		configuration.save();
	}				
}
