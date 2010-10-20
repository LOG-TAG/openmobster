/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.core.mobileCloud.rimos.testsuite.moblet;

import java.util.Vector;

import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.container.MainScreen;

import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.Screen;


/**
 * @author openmobster@gmail.com
 */
public class HomeScreen extends Screen
{		
	private MainScreen screen;
	private ListField listField;
	
	public HomeScreen()
	{
		this.screen = new MainScreen();										
	}
	//-------------------------------------------------------------------------------------------------------------------------------------------------
	public void render()
	{	
		this.screen.setTitle("OpenMobster TestSuite");
		
		this.listField = new ListField(2);
		this.listField.setCallback(new ListFieldCallbackImpl());		
				
		this.screen.add(this.listField);
		
		this.setupMenu();
	}
	
	public Object getContentPane() 
	{		
		return this.screen;
	}			
	//---------------------------------------------------------------------------------------------------------------------------------------------------
	private void setupMenu()
	{
		MenuItem start = new MenuItem("Start", 1, 1){
			public void run()
			{
				//UserInteraction/Event Processing...this is where the Commands can be executed
				HomeScreen.this.handleStart();
			}
		}; 
						
		this.screen.addMenuItem(start);		
	}		
	
	private void handleStart()
	{
		int selectedIndex = this.listField.getSelectedIndex();
		
		switch(selectedIndex)
		{
			case 0:
				this.run();
			break;
		
			case 1:
				this.viewBatteryLevel();
			break;
		}
	}
	
	private void run()
	{
		CommandContext commandContext = new CommandContext();
		commandContext.setTarget("run");
		Services.getInstance().getCommandService().execute(commandContext);
	}
	
	private void viewBatteryLevel()
	{
		Dialog.alert("Battery Level: "+DeviceInfo.getBatteryLevel());
	}
	
	//----------------------------------------------------------------------------------------------------------------------------------
	private static class ListFieldCallbackImpl implements ListFieldCallback
	{
		private Vector actions = new Vector();
		
		private ListFieldCallbackImpl()
		{
			this.actions.addElement("Run TestSuite");
			this.actions.addElement("Check Battery Level");
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
