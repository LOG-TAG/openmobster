/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
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
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.KeyListener;
import net.rim.device.api.ui.component.Dialog;

import org.openmobster.core.mobileCloud.manager.gui.LocaleKeys;
import org.openmobster.core.mobileCloud.rimos.configuration.Configuration;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.SystemLocaleKeys;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.NavigationContext;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.Screen;
import org.openmobster.core.mobileCloud.api.ui.framework.resources.AppResources;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;



/**
 * @author openmobster@gmail.com
 */
public class ManualSyncScreen extends Screen
{		
	private MainScreen screen;
	private ListField listField;
	private Vector channels;
	
	public ManualSyncScreen()
	{
		
	}
	//-------------------------------------------------------------------------------------------------------------------------------------------------
	public Object getContentPane() 
	{		
		return this.screen;
	}
	
	public void render()
	{								
		AppResources appResources = Services.getInstance().getResources();	
		Configuration configuration = Configuration.getInstance();
		this.screen = new MainScreen();
		this.screen.setTitle(appResources.localize(LocaleKeys.manual_sync, LocaleKeys.manual_sync));
		
		this.channels = configuration.getMyChannels();		
		if(channels != null && !channels.isEmpty())
		{																						
			listField = new ListField(channels.size());
			listField.setCallback(new ListFieldCallbackImpl(channels));		
					
			this.screen.add(listField);			
		}
		else
		{
			LabelField channelsNotFound = new LabelField(
			appResources.localize(LocaleKeys.channels_not_found, LocaleKeys.channels_not_found), LabelField.ELLIPSIS);
			this.screen.add(channelsNotFound);
		}
		
		this.setMenuItems();
		this.setUpNavigation();
	}
	
	private void setMenuItems()
	{		
		AppResources resources = Services.getInstance().getResources();
		
		if(this.channels != null && !this.channels.isEmpty())
		{
			MenuItem selectItem = new MenuItem(resources.localize(LocaleKeys.select, LocaleKeys.select), 1, 1){
				public void run()
				{
					//UserInteraction/Event Processing...this is where the Commands can be executed
					ManualSyncScreen.this.handle();
				}
			};
			this.screen.addMenuItem(selectItem);
		}
		
		MenuItem backItem = new MenuItem(resources.localize(SystemLocaleKeys.back, SystemLocaleKeys.back), 2, 2){
			public void run()
			{
				//Go Home
				Services.getInstance().getNavigationContext().home();
			}
		};								 												
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
		AppResources resources = Services.getInstance().getResources();
		int selectedIndex = this.listField.getSelectedIndex();
		String selectedChannel = (String)this.channels.elementAt(selectedIndex);
												
		Object[] syncOptions = new Object[]{
		resources.localize(LocaleKeys.reset_channel, LocaleKeys.reset_channel), 
		resources.localize(LocaleKeys.sync_channel, LocaleKeys.sync_channel), 
		resources.localize(SystemLocaleKeys.cancel, SystemLocaleKeys.cancel)};
		int syncOption = Dialog.ask(selectedChannel+": ", syncOptions, 1);
		
		if(syncOption != 2)
		{
			CommandContext commandContext = new CommandContext();
			commandContext.setTarget("manualSync");
			commandContext.setAttribute("syncOption", ""+syncOption);
			commandContext.setAttribute("channel", selectedChannel);
			Services.getInstance().getCommandService().execute(commandContext);
		}		
	}
	//----------------------------------------------------------------------------------------------------------------------------------
	private static class ListFieldCallbackImpl implements ListFieldCallback
	{
		private Vector channels;	
		
		private ListFieldCallbackImpl(Vector channels)
		{			
			this.channels = channels;					
		}

		public void drawListRow(ListField listField, Graphics graphics, int index,
		int y, int width) 
		{
			String action = (String)this.channels.elementAt(index);									
			graphics.drawText(action, 0, y);
		}
		
		public Object get(ListField listField, int index) 
		{			
			return (String)this.channels.elementAt(index);
		}
		
		public int indexOfList(ListField listField, String prefix, int start) 
		{			
			return this.channels.indexOf(prefix, start);
		}

		public int getPreferredWidth(ListField listField) 
		{			
			return Display.getWidth();
		}				
	}			
}
