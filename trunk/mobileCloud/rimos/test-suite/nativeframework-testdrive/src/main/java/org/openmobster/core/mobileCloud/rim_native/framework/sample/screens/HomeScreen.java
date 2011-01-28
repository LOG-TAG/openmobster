/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.rim_native.framework.sample.screens;

import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.MenuItem;

import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.rim_native.framework.AppScreen;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.Screen;
import org.openmobster.core.mobileCloud.api.ui.framework.resources.AppResources;
import org.openmobster.core.mobileCloud.rim_native.framework.sample.LocaleKeys;
import org.openmobster.core.mobileCloud.rimos.module.bus.Bus;
import org.openmobster.core.mobileCloud.rimos.module.bus.MobilePushInvocation;
import org.openmobster.core.mobileCloud.rimos.module.bus.MobilePushMetaData;


/**
 * @author openmobster@gmail.com
 */
public class HomeScreen extends Screen
{		
	private MainScreen screen;
	private boolean isRendered;
	
	public HomeScreen()
	{
		this.screen = new AppScreen();										
	}
	//-------------------------------------------------------------------------------------------------------------------------------------------------
	public void render()
	{	
		if(!isRendered)
		{
			//Platform specific UI producing code
			AppResources appResources = Services.getInstance().getResources();			
			
			this.screen.setTitle(appResources.localize(LocaleKeys.home, LocaleKeys.home));
							
			//Add Menu Items			 						
			MenuItem localCommand = new MenuItem(appResources.localize(LocaleKeys.localCommand, LocaleKeys.localCommand), 1, 1){
				public void run()
				{
					//UserInteraction/Event Processing...this is where the Commands can be executed
					CommandContext commandContext = new CommandContext();
					commandContext.setTarget("/demo/local");
					Services.getInstance().getCommandService().execute(commandContext);
				}
			}; 
			
			MenuItem remoteCommand = new MenuItem(appResources.localize(LocaleKeys.remoteCommand, LocaleKeys.remoteCommand), 2, 2){
				public void run()
				{
					//UserInteraction/Event Processing.....this is where the Commands can be executed
					CommandContext commandContext = new CommandContext();
					commandContext.setTarget("/demo/remote");
					Services.getInstance().getCommandService().execute(commandContext);
				}
			};
			
			MenuItem localAppExceptionCommand = new MenuItem(appResources.localize(LocaleKeys.localAppException, LocaleKeys.localAppException), 3, 3){
				public void run()
				{
					//UserInteraction/Event Processing.....this is where the Commands can be executed
					CommandContext commandContext = new CommandContext();
					commandContext.setTarget("/demo/localAppException");
					Services.getInstance().getCommandService().execute(commandContext);
				}
			};
			
			MenuItem remoteAppExceptionCommand = new MenuItem(appResources.localize(LocaleKeys.remoteAppException, LocaleKeys.remoteAppException), 4, 4){
				public void run()
				{
					//UserInteraction/Event Processing.....this is where the Commands can be executed
					CommandContext commandContext = new CommandContext();
					commandContext.setTarget("/demo/remoteAppException");
					Services.getInstance().getCommandService().execute(commandContext);
				}
			};
			
			MenuItem localExceptionCommand = new MenuItem(appResources.localize(LocaleKeys.localException, LocaleKeys.localException), 5, 5){
				public void run()
				{
					//UserInteraction/Event Processing.....this is where the Commands can be executed
					CommandContext commandContext = new CommandContext();
					commandContext.setTarget("/demo/localException");
					Services.getInstance().getCommandService().execute(commandContext);
				}
			};
			
			MenuItem remoteExceptionCommand = new MenuItem(appResources.localize(LocaleKeys.remoteException, LocaleKeys.remoteException), 6, 6){
				public void run()
				{
					//UserInteraction/Event Processing.....this is where the Commands can be executed
					CommandContext commandContext = new CommandContext();
					commandContext.setTarget("/demo/remoteException");
					Services.getInstance().getCommandService().execute(commandContext);
				}
			};
			
			MenuItem pushNotificationCommand = new MenuItem("Push Notification", 6, 6){
				public void run()
				{
					//UserInteraction/Event Processing.....this is where the Commands can be executed
					CommandContext commandContext = new CommandContext();
					commandContext.setTarget("/demo/pushNotification");
					Services.getInstance().getCommandService().execute(commandContext);
				}
			};
			
			MenuItem asyncCommand = new MenuItem("Async Command", 7, 7){
				public void run()
				{
					//UserInteraction/Event Processing.....this is where the Commands can be executed
					CommandContext commandContext = new CommandContext();
					commandContext.setTarget("/demo/asyncCommand");
					Services.getInstance().getCommandService().execute(commandContext);
				}
			};
			
			MenuItem remoteTimeoutCommand = new MenuItem("Timeout Remote Command", 8, 8){
				public void run()
				{
					//UserInteraction/Event Processing.....this is where the Commands can be executed
					CommandContext commandContext = new CommandContext();
					commandContext.setTarget("/demo/remote/timeout");
					commandContext.activateTimeout();
					Services.getInstance().getCommandService().execute(commandContext);
				}
			};
		
			this.screen.addMenuItem(localCommand);
			this.screen.addMenuItem(remoteCommand);
			this.screen.addMenuItem(localAppExceptionCommand);
			this.screen.addMenuItem(remoteAppExceptionCommand);
			this.screen.addMenuItem(localExceptionCommand);
			this.screen.addMenuItem(remoteExceptionCommand);
			this.screen.addMenuItem(pushNotificationCommand);
			this.screen.addMenuItem(asyncCommand);
			this.screen.addMenuItem(remoteTimeoutCommand);
			
			isRendered = true;
		}
	}
	
	public Object getContentPane() 
	{		
		return this.screen;
	}	
	//---------------------------------------------------------------------------------------------------------------------------------------------------
}
