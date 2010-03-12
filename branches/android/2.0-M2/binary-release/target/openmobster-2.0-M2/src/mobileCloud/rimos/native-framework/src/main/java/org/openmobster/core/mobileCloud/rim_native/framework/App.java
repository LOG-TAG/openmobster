/**
 * Copyright (c) {2003,2009} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.rim_native.framework;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.container.MainScreen;

import org.openmobster.core.mobileCloud.rimos.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.rimos.errors.SystemException;
import org.openmobster.core.mobileCloud.spi.ui.framework.SPIServices;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.resources.AppResources;


/**
 * @author openmobster@gmail.com
 *
 */
public class App extends UiApplication 
{
	public App()
	{
	}
	
	public static void main(String[] args)
    {		
    	try
    	{    		    		    					
    		//Load API Services
			Services.getInstance().setResources(new NativeAppResources());
			Services.getInstance().setCommandService(new NativeCommandService());
			
			//Load SPI Services
			SPIServices.getInstance().setNavigationContextSPI(new NativeNavigationContextSPI());
			
			//To make the application enter the event thread and start processing messages, 
			//we invoke the enterEventDispatcher method
			App moblet = new App();
			
			moblet.enterEventDispatcher();
    	}
    	catch(Exception e)
    	{
    		SystemException syse = new SystemException(App.class.getName(), "main", new Object[]{
    			"Exception: "+e.toString(),
    			"Message: "+e.getMessage()
    		});
    		ErrorHandler.getInstance().handle(syse);
    		
    		System.exit(1);    		    		
    	}
    }
	//-------------------------------------------------------------------------------------------------------------------------------------------
	public void activate() 
	{
		try
		{	
			AppResources resources = Services.getInstance().getResources();
			
			MainScreen startupScreen = new MainScreen();
			this.pushScreen(startupScreen);
			
			if(!Services.getInstance().isFrameworkActive())
			{
				throw new IllegalStateException(resources.localize(SystemLocaleKeys.framework_bootup_error, SystemLocaleKeys.framework_bootup_error));
			}
			
			CommandContext commandContext = new CommandContext();
			commandContext.setTarget("startup");
			Services.getInstance().getCommandService().execute(commandContext);						
		}
		catch(Exception e)
		{
			SystemException syse = new SystemException(App.class.getName(), "activate", new Object[]{
    			"Exception: "+e.toString(),
    			"Message: "+e.getMessage()
    		});
    		ErrorHandler.getInstance().handle(syse);
    		
			this.displayError(e);
		}
	}
			
	private void displayError(Exception t) 
	{
		Application.getApplication().invokeAndWait(new ShowError(t));
	}	
	//--execute on EDT------------------------------------------------------------------------------------------------------------------------------------------
	private static class ShowError implements Runnable
	{
		private Exception e;
		
		private ShowError(Exception e)
		{			
			this.e = e;
		}
		
		public void run()
		{
			//Display a Generic Error Message			
			Dialog.alert(e.toString());			
			System.exit(1);
		}
	}
}
