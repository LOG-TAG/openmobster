/**
 * Copyright (c) {2003,2009} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.hello.mobster.screen;

import java.util.Vector;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;

import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.Screen;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;

import org.openmobster.core.mobileCloud.rim_native.framework.AppScreen;

/**
 * "Screen" is a UI layer component. The concept of a "Screen" is an abstraction to represent what is displayed on the screen
 * of the mobile device when this component is rendered by the MVC Framework. This components carries platform specific UI code
 * for rendering the UI that must be displayed. 
 * 
 * In this example, this is a Home screen (the screen that is first displayed when the App is launched). It works on the BlackBerry
 * UI components
 * 
 * It provides a simple "View Tweets" option, which once selected, executes the TwitterCommand and fetches the Tweets from the Cloud
 * 
 * @author openmobster@gmail.com
 */
public class HomeScreen extends Screen
{	
	//BlackBerry MainScreen objects contain a title section, a separator element, 
	//and a main scrollable section (actually a single vertical field manager used to maintain a list of fields)
	private MainScreen screen;
	
	//BlackBerry ListField contains rows of selectable list items
	private ListField listField;
	
	public HomeScreen()
	{					
	}
	
	/**
	 * Contract Explanation 
	 * 
	 * the 'render' method of the Screen component is invoked by the MVC Framework when this
	 * screen needs to be displayed on the mobile device
	 */
	public void render()
	{
		//Create the MainScreen and set its title
		this.screen = new AppScreen();							
		this.screen.setTitle("Twitter Cloud App Demo");	
		
		//Add the 'List' of Actions that can be clicked on for this App
		this.listField = new ListField(1);		
		this.listField.setCallback(new ListFieldCallbackImpl());						
		this.screen.add(listField);
		
		//SetUp Menu Items that user can click and interact to start a process
		//In this case, its the TwitterCommand for fetching Tweets from the Cloud
		this.setMenuItems();
	}
	
	/**
	 * Contract Explanation
	 * 
	 * 'getContentPane' is invoked by the MVC framework to obtain fully 'rendered' platform-specific 
	 * UI component 
	 */
	public Object getContentPane() 
	{		
		return this.screen;
	}
	//--------------------------------------------------------------------------------------------------------------------------------------
	private void setMenuItems()
	{							
		MenuItem startItem = new MenuItem("Start", 1, 1){
			public void run()
			{
				//GUI Event Handling code. This is executed in response to a user interaction with the UI component in question
				//This code always executes on the Event Dispatch Thread (EDT)
				
				//Create a CommandContext used to invoke the "TwitterCommand" service
				CommandContext commandContext = new CommandContext();
				commandContext.setTarget("twitterCommand");
				Services.getInstance().getCommandService().execute(commandContext);
			}
		}; 
										
		this.screen.addMenuItem(startItem);		
	}
	
	private static class ListFieldCallbackImpl implements ListFieldCallback
	{
		private Vector actions = new Vector();
		
		private ListFieldCallbackImpl()
		{			
			this.actions.addElement("View Tweets");			
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
