/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.moblet;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.container.MainScreen;

import org.openmobster.core.mobileCloud.rimos.errors.SystemException;

/**
 * @author openmobster@gmail.com
 *
 */
public abstract class NativeUiMoblet extends UiApplication
{
	private static NativeUiMoblet singleton;
		
	public NativeUiMoblet()
	{
		NativeUiMoblet.singleton = this;
		
		StatusScreen statusScreen = new StatusScreen();
		this.pushScreen(statusScreen);
	}
	
	public static NativeUiMoblet getInstance()
	{
		return NativeUiMoblet.singleton;
	}	
	//------Developer's call backs-------------------------------------------------------------------------------------------------------------------------------
	/**
	 * Call back asking the Application to render the application's main screen
	 */
	protected abstract void displayMainScreen();
	
	/**
	 * Call back asking the Application to show an appropriate error message
	 */
	protected abstract void displayDeviceContainerInActive();
	
	/**
	 * Call back asking the Application to show an appropriate error message
	 */
	protected abstract void displayError(Exception t);
	//--Public API-------------------------------------------------------------------------------------------------------------------------------------------
	public void startMoblet()
	{
		try
		{
			Moblet.getInstance().startup();	
			//CometUtil.subscribeChannels();
		}
		catch(Exception e)
		{
			System.out.println("---------------------------------------------");
			System.out.println("Exception: "+e.toString());
			System.out.println("Message: "+e.getMessage());
			System.out.println("---------------------------------------------");
			throw new RuntimeException(e.toString());
		}
	}
	
	public void stopMoblet()
	{
		Moblet.getInstance().shutdown();		
	}
	//----------------------------------------------------------------------------------------------------------------------------------------------
	private class StatusScreen extends MainScreen
	{		
		protected void onVisibilityChange(boolean visible)
		{
			if(visible)
			{
				Runnable runnable = new Runnable(){
					public void run()
					{
						try
						{
							//Show Status
							Status.show("Connecting to the Cloud....");
							
							//Start the Moblet
							startMoblet();	
							
							//Launch the Application
							popScreen(getActiveScreen());
							displayMainScreen();
						}
						catch(SystemException syse)
						{
							if(syse.getMessage().indexOf("waitForContainer")!=-1)
							{
								popScreen(getActiveScreen());
								displayDeviceContainerInActive();
							}
							else
							{
								popScreen(getActiveScreen());
								displayError(syse);
							}
						}
						catch(Exception t)
						{
							popScreen(getActiveScreen());
							displayError(t);
						}						
					}
				};	
				Application.getApplication().invokeLater(runnable);
			}
		}
	}	
}
