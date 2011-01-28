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
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.system.CodeModuleManager; 
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationDescriptor;

import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.Screen;
import org.openmobster.core.mobileCloud.api.ui.framework.resources.AppResources;

import org.openmobster.core.mobileCloud.rim_native.framework.SystemLocaleKeys;
import org.openmobster.core.mobileCloud.rimos.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.rimos.errors.SystemException;
import org.openmobster.core.mobileCloud.manager.gui.CommandKeys;
import org.openmobster.core.mobileCloud.manager.gui.LocaleKeys;
import org.openmobster.core.mobileCloud.rimos.configuration.Configuration;


/**
 * @author openmobster@gmail.com
 */
public class HomeScreen extends Screen
{		
	private MainScreen screen;
	private ListField listField;
	
	public HomeScreen()
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
		
				
		MenuItem startItem = new MenuItem(resources.localize(LocaleKeys.select, LocaleKeys.select), 1, 1){
			public void run()
			{
				try
				{
					//UserInteraction/Event Processing...this is where the Commands can be executed
					HomeScreen.this.handleStart();
				}
				catch(Exception e)
				{
					//If an exception bubbled up here, this is not cool
					SystemException syse = new SystemException(this.getClass().getName(),"setMenuItems/eventThread",
					new Object[]{
						"Exception: "+e.getMessage()
					});
					ErrorHandler.getInstance().handle(syse);
					AppResources appResources = Services.getInstance().getResources();
					Dialog.alert(appResources.localize(SystemLocaleKeys.unknown_system_error, "unknown_system_error"));
				}
			}
		}; 
		
		MenuItem cloudItem = new MenuItem("Restore MobileCloud", 3, 3){
			public void run()
			{
				try
				{
					//Cloud Restart				
					int cloudHandle = CodeModuleManager.getModuleHandle("MobileCloud");
					ApplicationDescriptor[] cloudDesc =  CodeModuleManager.getApplicationDescriptors(cloudHandle);
					ApplicationManager.getApplicationManager().runApplication(cloudDesc[0]);
					Status.show("On-Device Cloud successfully restarted");
				}
				catch(Exception e)
				{
					//If an exception bubbled up here, this is not cool
					SystemException syse = new SystemException(this.getClass().getName(),"setMenuItems/restoringCloud",
					new Object[]{
						"Exception: "+e.getMessage()
					});
					ErrorHandler.getInstance().handle(syse);
					AppResources appResources = Services.getInstance().getResources();
					Dialog.alert(appResources.localize(SystemLocaleKeys.unknown_system_error, "unknown_system_error"));
				}
			}
		};
		
		MenuItem batteryItem = new MenuItem("Battery-Level", 4, 4){
			public void run()
			{
				//Handle Check Cloud Status				
				Dialog.alert("Battery Level: "+DeviceInfo.getBatteryLevel());
			}
		};
		
		
		MenuItem errorLog = new MenuItem("View Error Log", 6, 6){
			public void run()
			{
				String errorLog = ErrorHandler.getInstance().generateReport();
				
				if(errorLog != null && errorLog.trim().length()>0)
				{
					Dialog.alert(errorLog);
				}
				else
				{
					Status.show("Error Log is empty");
				}
			}
		};
		
		MenuItem uploadLog = new MenuItem("Upload Error Log", 7, 7){
			public void run()
			{
				//String errorLog = ErrorHandler.getInstance().generateReport();
				
				//FIXME: Add ability to upload the error log to the CloudServer
				//It can be used to diagonose/debug issues on the on-device moblet apps
				//ErrorHandler.getInstance().handle(new RuntimeException("Testing ErrorLog Clearing!!!"));
			}
		};
		
		MenuItem clearLog = new MenuItem("Clear Error Log", 8, 8){
			public void run()
			{
				ErrorHandler.getInstance().clearAll();
				Status.show("Error Log is now empty");
			}
		};
										
		this.screen.addMenuItem(startItem);
		this.screen.addMenuItem(MenuItem.separator(2));
		
		//System related functions
		this.screen.addMenuItem(cloudItem);
		this.screen.addMenuItem(batteryItem);
		
		//error log related items
		this.screen.addMenuItem(MenuItem.separator(5));
		this.screen.addMenuItem(errorLog);
		this.screen.addMenuItem(uploadLog);
		this.screen.addMenuItem(clearLog);
	}	
	
	private void handleStart() throws Exception
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
					Dialog serverDialog = new Dialog(Dialog.D_OK,resources.localize(LocaleKeys.activate, LocaleKeys.activate),0,null,0);
					serverDialog.add(serverLabel);
					serverDialog.add(serverField);
					serverDialog.doModal();
					
					Dialog portDialog = new Dialog(Dialog.D_OK,resources.localize(LocaleKeys.port, LocaleKeys.port),0,null,0);
					portDialog.add(portLabel);
					portDialog.add(portField);
					//fill in default value
					portField.setText(configuration.getPlainServerPort());
					portDialog.doModal();
					
					Dialog emailDialog = new Dialog(Dialog.D_OK,resources.localize(LocaleKeys.activate, LocaleKeys.activate),0,null,0);
					emailDialog.add(emailLabel);
					emailDialog.add(emailField);
					emailDialog.doModal();
					
					Dialog passwordDialog = new Dialog(Dialog.D_OK,resources.localize(LocaleKeys.activate, LocaleKeys.activate),0,null,0);
					passwordDialog.add(passwordLabel);
					passwordDialog.add(passwordField);
					passwordDialog.doModal();
					
					//UserInteraction/Event Processing...this is where the Commands can be executed
					//Validate server input
					String server = serverField.getText();
					if(server==null || server.trim().length()==0)
					{
						Dialog.alert(resources.localize(LocaleKeys.server, LocaleKeys.server)+ " " +
						resources.localize(LocaleKeys.input_required, LocaleKeys.input_required));
						return;
					}
					
					String port = portField.getText();
					if(port==null || port.trim().length()==0)
					{
						Dialog.alert(resources.localize(LocaleKeys.port, LocaleKeys.port)+ " " +
						resources.localize(LocaleKeys.input_required, LocaleKeys.input_required));
						return;
					}
					
					//Validate email input
					String email = emailField.getText();
					if(email==null || email.trim().length()==0)
					{
						Dialog.alert(resources.localize(LocaleKeys.email, LocaleKeys.email)+ " " +
						resources.localize(LocaleKeys.input_required, LocaleKeys.input_required));
						return;
					}
					
					//Validate password input
					String password = passwordField.getText();
					if(password==null || password.trim().length()==0)
					{
						Dialog.alert(resources.localize(LocaleKeys.password, LocaleKeys.password)+ " " +
						resources.localize(LocaleKeys.input_required, LocaleKeys.input_required));
						return;
					}
					
					//Execute the "Activation" Command
					commandContext.setAttribute(CommandKeys.server, server);
					commandContext.setAttribute(CommandKeys.email, email);
					commandContext.setAttribute(CommandKeys.password, password);
					commandContext.setAttribute("port", port);
					Services.getInstance().getCommandService().execute(commandContext);
				}
				else
				{
					commandContext.setAttribute(CommandKeys.server, configuration.getServerIp());
					commandContext.setAttribute(CommandKeys.email, configuration.getEmail());					
									
					Dialog passwordDialog = new Dialog(Dialog.D_OK_CANCEL,"Email: "+configuration.getEmail(),0,null,0);
					passwordDialog.add(passwordLabel);
					passwordDialog.add(passwordField);
					passwordDialog.doModal();
							
					int selectedValue = passwordDialog.getSelectedValue();
					
					if(selectedValue == 0)
					{
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
					commandContext.setTarget("cometStatus");
					Services.getInstance().getCommandService().execute(commandContext);
					
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
