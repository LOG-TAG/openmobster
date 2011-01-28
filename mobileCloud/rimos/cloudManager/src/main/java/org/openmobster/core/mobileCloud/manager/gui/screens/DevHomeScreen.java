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
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.component.PasswordEditField;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.component.Dialog;

import org.openmobster.core.mobileCloud.manager.gui.CommandKeys;
import org.openmobster.core.mobileCloud.manager.gui.LocaleKeys;
import org.openmobster.core.mobileCloud.rimos.configuration.Configuration;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.Screen;
import org.openmobster.core.mobileCloud.api.ui.framework.resources.AppResources;

/**
 * @deprecated: This class is no longer needed after the AppCreator was introduced in the 2.0-M2 release
 * 
 * @author openmobster@gmail.com
 */
public class DevHomeScreen extends Screen
{		
	private MainScreen screen;
	private ListField listField;
	
	public DevHomeScreen()
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
		AppResources appResources = Services.getInstance().getResources();					
		this.screen = new MainScreen();
		this.screen.setTitle(appResources.localize(LocaleKeys.control_panel, LocaleKeys.control_panel));
												
		listField = new ListField(6);
		listField.setCallback(new ListFieldCallbackImpl());		
				
		this.screen.add(listField);
		this.setMenuItems();
	}
	
	private void setMenuItems()
	{		
		AppResources resources = Services.getInstance().getResources();
		
				
		MenuItem startItem = new MenuItem(resources.localize(LocaleKeys.start, LocaleKeys.start), 1, 1){
			public void run()
			{
				//UserInteraction/Event Processing...this is where the Commands can be executed
				DevHomeScreen.this.handleStart();
			}
		}; 
										
		this.screen.addMenuItem(startItem);		
	}	
	
	private void handleStart()
	{
		int selectedIndex = this.listField.getSelectedIndex();
		AppResources resources = Services.getInstance().getResources();
		Configuration configuration = Configuration.getInstance();
		
		CommandContext commandContext = new CommandContext();
		switch(selectedIndex)
		{
			case 0:
				//Handle Activation Function
				//This is the real code		
				commandContext.setTarget("activate");
				LabelField serverLabel = new LabelField(resources.localize(LocaleKeys.server, LocaleKeys.server)+":", LabelField.ELLIPSIS);
				BasicEditField serverField = new BasicEditField(BasicEditField.FILTER_URL);
				LabelField portLabel = new LabelField(resources.localize(LocaleKeys.port, LocaleKeys.port)+":", LabelField.ELLIPSIS);
				BasicEditField portField = new BasicEditField(BasicEditField.FILTER_INTEGER);
				LabelField emailLabel = new LabelField(resources.localize(LocaleKeys.email, LocaleKeys.email)+":", LabelField.ELLIPSIS);
				BasicEditField emailField = new BasicEditField(BasicEditField.FILTER_EMAIL);
				LabelField passwordLabel = new LabelField(resources.localize(LocaleKeys.password, LocaleKeys.password)+":", LabelField.ELLIPSIS);
				PasswordEditField passwordField = new PasswordEditField();
				if(!configuration.isActive())
				{	
					commandContext.setAttribute(CommandKeys.server, "192.168.1.108");
					commandContext.setAttribute(CommandKeys.email, "blah2@gmail.com");
					commandContext.setAttribute(CommandKeys.password, "blahblah2");
					Services.getInstance().getCommandService().execute(commandContext);
				}
				else
				{
					commandContext.setAttribute(CommandKeys.server, configuration.getServerIp());
					commandContext.setAttribute(CommandKeys.email, configuration.getEmail());					
									
					Dialog passwordDialog = new Dialog(Dialog.D_OK,"Email: "+configuration.getEmail(),0,null,0);
					passwordDialog.add(passwordLabel);
					passwordDialog.add(passwordField);
					passwordDialog.doModal();
					
					String password = passwordField.getText();
					if(password==null || password.trim().length()==0)
					{
						Dialog.alert(resources.localize(LocaleKeys.password, LocaleKeys.password)+ " " +
						resources.localize(LocaleKeys.input_required, LocaleKeys.input_required));
						return;
					}
					
					commandContext.setAttribute(CommandKeys.password, passwordField.getText());
					Services.getInstance().getCommandService().execute(commandContext);
				}
			break;
			
			case 1:
				//Handle App Store functionality				
				if(configuration.isActive())
				{					
					commandContext.setTarget("loadAppStore");
					Services.getInstance().getCommandService().execute(commandContext);
				}
				else
				{
					Status.show(resources.localize(LocaleKeys.device_inactive, LocaleKeys.device_inactive));
				}
			break;
			
			case 2:
				//Handle Push Settings				
				if(configuration.isActive())
				{
					Services.getInstance().getNavigationContext().navigate("cometConfig");
				}
				else
				{
					Status.show(resources.localize(LocaleKeys.device_inactive, LocaleKeys.device_inactive));
				}
			break;
			
			case 3:
				//Handle Manual Sync 				
				if(configuration.isActive())
				{					
					Services.getInstance().getNavigationContext().navigate("manualSync");
				}
				else
				{
					Status.show(resources.localize(LocaleKeys.device_inactive, LocaleKeys.device_inactive));
				}
			break;
			
			case 4:
				//Handle Security Settings				
				if(configuration.isActive())
				{					
					Services.getInstance().getNavigationContext().navigate("security");
				}
				else
				{
					Status.show(resources.localize(LocaleKeys.device_inactive, LocaleKeys.device_inactive));
				}
			break;
			
			case 5:
				//Handle Check Cloud Status				
				if(configuration.isActive())
				{					
					commandContext.setTarget("checkCloudStatus");
					Services.getInstance().getCommandService().execute(commandContext);
				}
				else
				{
					Status.show(resources.localize(LocaleKeys.device_inactive, LocaleKeys.device_inactive));
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
			//TODO: Hardcoded for now....this can be made more dynamic, but loaded from an external file
			//This information cannot be loaded from the server, since this is where the server information is 
			//collected in the first place
			AppResources resources = Services.getInstance().getResources();
			this.actions.addElement(resources.localize(LocaleKeys.activate, LocaleKeys.activate));
			this.actions.addElement(resources.localize(LocaleKeys.app_store, LocaleKeys.app_store));
			this.actions.addElement(resources.localize(LocaleKeys.push_settings, LocaleKeys.push_settings));
			this.actions.addElement(resources.localize(LocaleKeys.manual_sync, LocaleKeys.manual_sync));
			this.actions.addElement(resources.localize(LocaleKeys.security, LocaleKeys.security));
			this.actions.addElement(resources.localize(LocaleKeys.status, LocaleKeys.status));
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
