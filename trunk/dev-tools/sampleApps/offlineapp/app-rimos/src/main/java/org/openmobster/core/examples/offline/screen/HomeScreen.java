/**
 * Copyright (c) {2003,2009} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.examples.offline.screen;

import java.util.Vector;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;

import org.openmobster.core.mobileCloud.rimos.configuration.Configuration;
import org.openmobster.core.mobileCloud.rim_native.framework.AppScreen;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.Screen;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.resources.AppResources;
import org.openmobster.core.mobileCloud.api.model.MobileBean;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;



/**
 * @author openmobster@gmail.com
 */
public class HomeScreen extends Screen
{		
	private MainScreen screen;
	private ListField listField;
	private Vector actions;
	
	public HomeScreen()
	{									
	}
	
	public void render()
	{	
		this.screen = new AppScreen();
		AppResources res = Services.getInstance().getResources();
		Configuration configuration = Configuration.getInstance();
		
		this.screen.setTitle(res.localize("title", "title"));
		
		if(!configuration.isActive())
		{
			Dialog.alert(res.localize("inactive_message","inactive_message"));
			return;
		}
		
		if(!MobileBean.isBooted("/offlineapp/demochannel"))
		{
			Dialog.alert(res.localize("channel_not_ready", "channel_not_ready"));
			return;
		}
		
		//Read the demo beans stored on the device
		MobileBean[] demoBeans = MobileBean.readAll("/offlineapp/demochannel");
		
		actions = new Vector();
		int size = demoBeans.length;
		for(int i=0; i<size; i++)
		{
			String beanValue = demoBeans[i].getValue("demoString");
			if(!actions.contains(beanValue))
			{
				actions.addElement(beanValue);
			}
		}
		
		//Setup a list field to be displayed
		this.listField = new ListField(actions.size());
		this.listField.setCallback(new ListFieldCallbackImpl(actions));
		this.screen.add(this.listField);
		this.setMenuItems();
	}
	
	public Object getContentPane() 
	{		
		return this.screen;
	}	
	
	private void setMenuItems()
	{
		MenuItem detailItem = new MenuItem("Details", 1, 1){
			public void run()
			{
				//UserInteraction/Event Processing...this is where the Commands can be executed
				HomeScreen.this.handleDetails();
			}
		}; 
										
		this.screen.addMenuItem(detailItem);	
	}
	
	private void handleDetails()
	{
		int selectedIndex = this.listField.getSelectedIndex();
		String selectedBean = (String)this.actions.elementAt(selectedIndex);
		
		CommandContext commandContext = new CommandContext();
		commandContext.setTarget("/demo/details");
		commandContext.setAttribute("selectedBean", selectedBean);
		Services.getInstance().getCommandService().execute(commandContext);
	}
	
	private static class ListFieldCallbackImpl implements ListFieldCallback
	{
		private Vector actions = new Vector();
		
		private ListFieldCallbackImpl(Vector actions)
		{
			this.actions = actions;
		}

		public void drawListRow(ListField listField, Graphics graphics, int index,
		int y, int width) 
		{
			String action = (String)this.actions.elementAt(index);									
			graphics.drawText(action, 0, y);
		}
		
		public Object get(ListField listField, int index) 
		{			
			return this.actions.elementAt(index);
		}

		public int getPreferredWidth(ListField listField) 
		{			
			return Display.getWidth();
		}

		public int indexOfList(ListField listField, String prefix, int start) 
		{			
			return this.actions.indexOf(prefix, start);
		}		
	}		
}
