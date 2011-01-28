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
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.Screen;
import org.openmobster.core.mobileCloud.api.ui.framework.resources.AppResources;
import org.openmobster.core.mobileCloud.rim_native.framework.sample.LocaleKeys;
import org.openmobster.core.mobileCloud.rim_native.framework.AppScreen;


/**
 * @author openmobster@gmail.com
 */
public class AsyncCommandScreen extends Screen
{		
	private MainScreen screen;
	private boolean isRendered;
	
	public AsyncCommandScreen()
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
			
			this.screen.setTitle("AsyncCommand!!");
							
			//Add Menu Items			 						
			MenuItem homeCommand = new MenuItem(appResources.localize(LocaleKeys.home, LocaleKeys.home), 1, 1)
			{
				public void run()
				{
					//UserInteraction/Event Processing...this is where the Commands can be executed															
					Services.getInstance().getNavigationContext().home();
				}
			}; 
			
			 		
			this.screen.addMenuItem(homeCommand);			
			
			isRendered = true;
		}
	}
	
	public Object getContentPane() 
	{		
		return this.screen;
	}		
	//---------------------------------------------------------------------------------------------------------------------------------------------------
}
