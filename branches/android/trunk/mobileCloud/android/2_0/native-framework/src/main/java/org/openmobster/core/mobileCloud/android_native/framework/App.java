/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.android_native.framework;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import org.openmobster.core.mobileCloud.android.configuration.Configuration;
import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.kernel.DeviceContainer;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.NavigationContext;
import org.openmobster.core.mobileCloud.spi.ui.framework.SPIServices;

/**
 * @author openmobster@gmail.com
 * 
 */
public class App extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		try
		{
			super.onCreate(savedInstanceState);
									
			//Initialize the UI Framework
        	//Load API Services
			Services.getInstance().setResources(new NativeAppResources());
			Services.getInstance().setCommandService(new NativeCommandService());
			
			//Load SPI Services
			SPIServices.getInstance().setNavigationContextSPI(new NativeNavigationContextSPI());
			
			//Initialize the kernel
        	DeviceContainer.getInstance(this).startup();        	        	
		} 
		catch (Exception e)
		{
			ErrorHandler.getInstance().handle(new SystemException(this.getClass().getName(), "onCreate", new Object[]{
				"Message:"+e.getMessage(),
				"Exception:"+e.toString()
			}));
			ShowError.showBootstrapError(this);
			e.printStackTrace(System.out);
		}
	}
	
	@Override
	protected void onDestroy()
	{								
    	try
    	{    		    		
    		DeviceContainer.getInstance(this).shutdown();
    		
    		//TODO: Destroy other singleton state
    		Configuration.stopSingleton();
    		
    		super.onDestroy();
    	}
    	catch(Exception e)
    	{
    		ErrorHandler.getInstance().handle(new SystemException(this.getClass().getName(), "onDestroy", new Object[]{
				"Message:"+e.getMessage(),
				"Exception:"+e.toString()
			}));
    		e.printStackTrace(System.out);
    	}
	}
	
	

	@Override
	protected void onResume()
	{
		try
		{
			//Loads the home screen
			super.onResume();
			
			NavigationContext.getInstance().setHome("home");
	    	NavigationContext.getInstance().home();
		}
		catch(Exception e)
		{
			ErrorHandler.getInstance().handle(new SystemException(this.getClass().getName(), 
			"onResume", new Object[]{
				"Message:"+e.getMessage(),
				"Exception:"+e.toString()
			}));
			e.printStackTrace(System.out);
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{				
		menu.clear();
		
		super.onPrepareOptionsMenu(menu);
		
	    NavigationContext.getInstance().setAttribute("options-menu", menu);
	    
	    //FIXME: This should be a refresh so that current screen is refreshed
	    NavigationContext.getInstance().refresh();
	    
	    return true;
	}
}
