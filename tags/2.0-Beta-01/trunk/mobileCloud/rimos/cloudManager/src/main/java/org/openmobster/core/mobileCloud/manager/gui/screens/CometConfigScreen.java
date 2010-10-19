/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.manager.gui.screens;

import java.util.Vector;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.KeyListener;

import org.openmobster.core.mobileCloud.manager.gui.LocaleKeys;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.SystemLocaleKeys;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.NavigationContext;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.Screen;
import org.openmobster.core.mobileCloud.api.ui.framework.resources.AppResources;
import org.openmobster.core.mobileCloud.rimos.configuration.Configuration;



/**
 * @author openmobster@gmail.com
 */
public class CometConfigScreen extends Screen
{		
	private MainScreen screen;
	private ListField listField;
	
	public CometConfigScreen()
	{
		
	}
	//-------------------------------------------------------------------------------------------------------------------------------------------------
	public Object getContentPane() 
	{		
		//return this.controlPanel;
		return this.screen;
	}
	
	public void render()
	{
		NavigationContext navigationContext = NavigationContext.getInstance();
		AppResources appResources = Services.getInstance().getResources();					
		this.screen = new MainScreen();
		this.screen.setTitle(appResources.localize(LocaleKeys.push_settings, LocaleKeys.push_settings));
												
		listField = new ListField(2);
		listField.setCallback(new ListFieldCallbackImpl());	
		
		//Show Push Status
		Configuration configuration = Configuration.getInstance();
		String status = (String)navigationContext.getAttribute(this.getId(), "status");
		String statusLabel = "Push";
		if(!configuration.isInPushMode())
		{
			statusLabel = "Poll";
		}
		
		HorizontalFieldManager bannerField = new HorizontalFieldManager();
		
		if(status.equalsIgnoreCase(""+Boolean.TRUE))
		{
			EncodedImage statusIcon = (EncodedImage)Services.getInstance().getResources().getImage("/moblet-app/icon/green.png");		
			bannerField.add(new LabelField(statusLabel+": "));
			bannerField.add(new BitmapField(statusIcon.getBitmap()));
		}
		else
		{
			EncodedImage statusIcon = (EncodedImage)Services.getInstance().getResources().getImage("/moblet-app/icon/red.png");		
			bannerField.add(new LabelField(statusLabel+": "));
			bannerField.add(new BitmapField(statusIcon.getBitmap()));
		}
		
		this.screen.setBanner(bannerField);
				
		this.screen.add(listField);
		this.setMenuItems();
		this.setUpNavigation();
	}
	
	private void setMenuItems()
	{		
		AppResources resources = Services.getInstance().getResources();
		
		MenuItem selectItem = new MenuItem(resources.localize(LocaleKeys.select, LocaleKeys.select), 1, 1){
			public void run()
			{
				//UserInteraction/Event Processing...this is where the Commands can be executed				
				CometConfigScreen.this.handle();
			}
		};
		
				
		MenuItem backItem = new MenuItem(resources.localize(SystemLocaleKeys.back, SystemLocaleKeys.back), 2,2){
			public void run()
			{
				//Go Home
				Services.getInstance().getNavigationContext().home();
			}
		};
										 										
		this.screen.addMenuItem(selectItem);
		this.screen.addMenuItem(backItem);
	}
	
	private void setUpNavigation()
	{
		this.screen.addKeyListener(new KeyListener()
		{
			public boolean keyChar(char key, int status, int time)
			{				
				if(key == Characters.ESCAPE)
				{
					NavigationContext.getInstance().back();
					return true;
				}
				return false;
			}
			
			public boolean keyDown(int keyCode, int time)
			{																
				return false;
			}
			
			public boolean keyUp(int keyCode, int time)
			{				
				return false;
			}
			
			public boolean keyRepeat(int keyCode, int time)
			{				
				return false;
			}

			public boolean keyStatus(int keyCode, int time)
			{				
				return false;
			}						
		});
	}
	
	private void handle()
	{
		int selectedIndex = this.listField.getSelectedIndex();
		
		CommandContext commandContext;
		switch(selectedIndex)
		{
			case 0:	
				//Handle setting push mode
				Object[] pushOptions = new Object[]{"Start", "Stop", "Cancel"}; 
				int selected = Dialog.ask("Push", pushOptions, 0);	
				
				String pushOption = (String)pushOptions[selected];
				
				if(pushOption.equals("Start"))
				{
					commandContext = new CommandContext();
					commandContext.setTarget("cometConfig");
					commandContext.setAttribute("mode", "push");				
					Services.getInstance().getCommandService().execute(commandContext);
				}
				else if(pushOption.equals("Stop"))
				{
					//Handle comet stop
					commandContext = new CommandContext();
					commandContext.setTarget("cometStop");							
					Services.getInstance().getCommandService().execute(commandContext);
				}
			break;
			
			case 1:
				//Handle setting push mode
				Object[] pollOptions = new Object[]{"Start", "Stop", "Cancel"}; 
				int pollSelected = Dialog.ask("Poll", pollOptions, 0);	
				
				String pollOption = (String)pollOptions[pollSelected];
				
				if(pollOption.equals("Start"))
				{
					AppResources resources = Services.getInstance().getResources();
					Object[] pollChoices = new Object[]{"5","10","15","20","25","30", "Cancel"}; 
					String pollLabel = resources.localize(LocaleKeys.poll_interval, LocaleKeys.poll_interval);
					int pollChoice = Dialog.ask(pollLabel+": ", pollChoices, 2);
					
					if(pollChoice < pollChoices.length-1)
					{
						int pollMinutes = Integer.parseInt((String)pollChoices[pollChoice]);
						long pollInterval = pollMinutes * 60 * 1000; //minutes converted to milliseconds
						
						//Handle setting poll mode
						commandContext = new CommandContext();
						commandContext.setTarget("cometConfig");
						commandContext.setAttribute("mode", "poll");
						commandContext.setAttribute("poll_interval", ""+pollInterval);
						Services.getInstance().getCommandService().execute(commandContext);
					}			
				}
				else if(pollOption.equals("Stop"))
				{
					//Handle comet stop
					commandContext = new CommandContext();
					commandContext.setTarget("cometStop");							
					Services.getInstance().getCommandService().execute(commandContext);
				}									
			break;
						
			
			default:
				//Do nothing					
			break;
		}
	}
	//----------------------------------------------------------------------------------------------------------------------------------
	private static class ListFieldCallbackImpl implements ListFieldCallback
	{
		private Vector actions = new Vector();
		
		private ListFieldCallbackImpl()
		{			
			AppResources resources = Services.getInstance().getResources();
			this.actions.addElement(resources.localize(LocaleKeys.realtime_push_mode, LocaleKeys.realtime_push_mode));
			this.actions.addElement(resources.localize(LocaleKeys.poll_mode, LocaleKeys.poll_mode));			
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
