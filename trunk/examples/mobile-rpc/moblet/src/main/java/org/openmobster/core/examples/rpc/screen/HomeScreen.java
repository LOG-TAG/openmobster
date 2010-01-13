/**
 * Copyright (c) {2003,2009} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.examples.rpc.screen;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.RichTextField;

import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.Screen;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.resources.AppResources;
import org.openmobster.core.mobileCloud.rimos.configuration.Configuration;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.NavigationContext;
import org.openmobster.core.mobileCloud.rim_native.framework.AppScreen;



/**
 * @author openmobster@gmail.com
 */
public class HomeScreen extends Screen
{		
	private MainScreen screen;
	private boolean itemAdded = false;
	
	public HomeScreen()
	{
		this.screen = new AppScreen();								
	}
	
	public void render()
	{	
		AppResources res = Services.getInstance().getResources();
		Configuration configuration = Configuration.getInstance();
		
		this.screen.setTitle(res.localize("title", "title"));
		
		if(!configuration.isActive())
		{
			Dialog.alert(res.localize("inactive_message","inactive_message"));
			return;
		}
		
	
		if(!this.itemAdded)
		{
			MenuItem rpcCommand = new MenuItem("RPC Demo", 1, 1){
				public void run()
				{
					//UserInteraction/Event Processing...this is where the Commands can be executed
					CommandContext commandContext = new CommandContext();
					commandContext.setTarget("/demo/rpc");
					Services.getInstance().getCommandService().execute(commandContext);
				}
			}; 
	
			this.screen.addMenuItem(rpcCommand);
			this.itemAdded = true;
		}
		
		NavigationContext navigationContext = Services.getInstance().getNavigationContext();
		if(navigationContext.getAttribute(this.getId(), "param1")!=null)
		{
			RichTextField message = new RichTextField();
			message.setEditable(false);
			message.setText((String)navigationContext.getAttribute(this.getId(), "param1"));
			this.screen.add(message);
		}
		if(navigationContext.getAttribute(this.getId(), "param2")!=null)
		{
			RichTextField message = new RichTextField();
			message.setEditable(false);
			message.setText((String)navigationContext.getAttribute(this.getId(), "param2"));
			this.screen.add(message);
		}
	}
	
	public Object getContentPane() 
	{		
		return this.screen;
	}			
}
