/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.bus.testdrive;


import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.MainScreen;

import org.bus.testdrive.module.bus.Bus;
import org.bus.testdrive.module.bus.Invocation;
import org.bus.testdrive.module.bus.InvocationHandler;
import org.bus.testdrive.module.bus.InvocationResponse;

/**
 * @author openmobster@gmail
 * 
 */
public class TestDrive extends UiApplication
{
	public static void main(String[] args)
	{
		UiApplication app = new TestDrive();
		app.enterEventDispatcher();
	}

	public void activate()
	{
		this.pushScreen(new AppScreen());
	}

	// create a new screen that extends MainScreen, which provides
	// default standard behavior for BlackBerry applications
	final class AppScreen extends MainScreen
	{
		public AppScreen()
		{
			// invoke the MainScreen constructor
			super();

			// add a title to the screen
			LabelField title = new LabelField("Bus TestDrive",
					LabelField.ELLIPSIS | LabelField.USE_ALL_WIDTH);
			setTitle(title);
			
			this.setupMenu();
			
			Bus.getInstance().start();
		}
		
		private void setupMenu()
		{
			MenuItem simpledb = new MenuItem("Simple Bus Usecase", 1, 1){
				public void run()
				{
					//UserInteraction/Event Processing...this is where the Commands can be executed
					AppScreen.this.handleSimpleBusUsecase();
				}
			}; 
			
			MenuItem remotedb = new MenuItem("Remote Bus Usecase", 1, 1){
				public void run()
				{
					//UserInteraction/Event Processing...this is where the Commands can be executed
					AppScreen.this.handleRemoteBusUsecase();
				}
			}; 
			
			MenuItem viewBatteryLevel = new MenuItem("View Battery Level", 1, 1){
				public void run()
				{
					//UserInteraction/Event Processing...this is where the Commands can be executed
					AppScreen.this.viewBatteryLevel();
				}
			}; 
											
			this.addMenuItem(simpledb);	
			this.addMenuItem(remotedb);
			this.addMenuItem(viewBatteryLevel);
		}
		
		private void handleSimpleBusUsecase()
		{
			try
			{
				Bus bus = Bus.getInstance();
				
				InvocationHandler handler = new MockInvocationHandler("MockInvocationHandler", "Output/Hello_World");
				bus.register(handler);
				
				Invocation invocation = new Invocation("MockInvocationHandler");
				invocation.setValue("input", "test://Invocation");
				
				InvocationResponse response = bus.invokeService(invocation);
				String returnValue = response.getValue(InvocationResponse.returnValue);
				
				
				Dialog.alert("Success: "+returnValue);
			}
			catch(Exception e)
			{
				Dialog.alert("Error: "+e.toString());
			}
		}
		
		private void handleRemoteBusUsecase()
		{
			try
			{
				Bus bus = Bus.getInstance();
				
								
				Invocation invocation = new Invocation("org.bus.testdrive.RemoteInvocationHandler");
				invocation.setValue("input", "test://RemoteInvocation");
				
				InvocationResponse response = bus.invokeService(invocation);
				String returnValue = response.getValue(InvocationResponse.returnValue);
				
				
				Dialog.alert("Success: "+returnValue);
			}
			catch(Exception e)
			{
				Dialog.alert("Error: "+e.toString());
			}
		}
		
		private void viewBatteryLevel()
		{
			Dialog.alert("Battery Level: "+DeviceInfo.getBatteryLevel());
		}
	}
}
