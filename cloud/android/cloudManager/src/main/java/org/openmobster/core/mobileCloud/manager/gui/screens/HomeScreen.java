/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.manager.gui.screens;

import java.lang.reflect.Field;

import org.openmobster.core.mobileCloud.android.configuration.Configuration;
import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;
import org.openmobster.core.mobileCloud.android_native.framework.events.ListItemClickEvent;
import org.openmobster.core.mobileCloud.android_native.framework.events.ListItemClickListener;
import org.openmobster.core.mobileCloud.android.util.GenericAttributeManager;

import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.NavigationContext;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.Screen;
import org.openmobster.core.mobileCloud.api.ui.framework.resources.AppResources;
import org.openmobster.core.mobileCloud.manager.gui.CommandKeys;
import org.openmobster.core.mobileCloud.manager.gui.LocaleKeys;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;
import android.text.InputType;


/**
 * @author openmobster@gmail.com
 */
public class HomeScreen extends Screen
{	
	private Integer screenId;
	
	private static String[] functions = new String[]
	{"Activate","Corporate App Store","Push Settings", "Manual Sync","Cloud Status"};
	
	public HomeScreen()
	{										
	}
	//-------------------------------------------------------------------------------------------------------------------------------------------------
	public void render()
	{
		try
		{
			final Activity currentActivity = Services.getInstance().getCurrentActivity();
			
			String layoutClass = currentActivity.getPackageName()+".R$layout";
			String main = "main";
			Class clazz = Class.forName(layoutClass);
			Field field = clazz.getField(main);
			this.screenId = field.getInt(clazz);						
		}
		catch(Exception e)
		{
			SystemException se = new SystemException(this.getClass().getName(), "render", new Object[]{
				"Message:"+e.getMessage(),
				"Exception:"+e.toString()
			});
			ErrorHandler.getInstance().handle(se);
			throw se;
		}
	}
	
	public Object getContentPane() 
	{		
		return this.screenId;
	}
	
	public void postRender()
	{
		//render the list		
		ListActivity listApp = (ListActivity)Services.getInstance().getCurrentActivity();
		
		//Set the title
		listApp.setTitle("Control Panel");
		
		//Show the List of operations on the control panel
		listApp.setListAdapter(new ArrayAdapter(listApp, 
	    android.R.layout.simple_list_item_1, 
	    functions));
		
		//Setup a menu
		this.setupMenu();
		
		//Add a List click listener
		ListItemClickListener clickListener = new ListItemClickListener()
		{
			public void onClick(ListItemClickEvent clickEvent)
			{
				int functionId = clickEvent.getPosition();
				
				switch(functionId)
				{
					
					case 0:
						//Device Activation with the Cloud
						HomeScreen.this.startActivationWizard();
					break;
					
					case 1:
						//Enterprise App Store
						HomeScreen.this.startAppStoreConf();
					break;
					
					case 2:
						//Push Configuration
						HomeScreen.this.startPushConfiguration();
					break;
					
					case 3:
						//ManualSync screen
						HomeScreen.this.startManualSyncConf();
					break;
					
					//case 4:
						//Security Configuration
					//	HomeScreen.this.startSecurityConf();
					//break;
					
					case 4:
						//Check Cloud Status
						HomeScreen.this.checkCloudStatus();
					break;
				}
			}
		};
		NavigationContext.getInstance().addClickListener(clickListener);
	}
	
	private void setupMenu()
	{
		final ListActivity listApp = (ListActivity)Services.getInstance().getCurrentActivity();
		Menu menu = (Menu)NavigationContext.getInstance().
		getAttribute("options-menu");
		
		if(menu != null)
		{			
			MenuItem item1 = menu.add(Menu.NONE, Menu.NONE, 0, "View Error Log");
			item1.setOnMenuItemClickListener(new OnMenuItemClickListener()
			{
				public boolean onMenuItemClick(MenuItem clickedItem)
				{
					String errorLog = ErrorHandler.getInstance().generateReport();
					
					if(errorLog != null && errorLog.trim().length()>0)
					{
						ViewHelper.getOkModal(listApp, "Error Log", errorLog).
						show();
					}
					else
					{
						ViewHelper.getOkModal(listApp, "Error Log", "Error Log is Empty").
						show();
					}
					
					return true;
				}
			});
			
			MenuItem item2 = menu.add(Menu.NONE, Menu.NONE, 1, "Upload Error Log");
			item2.setOnMenuItemClickListener(new OnMenuItemClickListener()
			{
				public boolean onMenuItemClick(MenuItem clickedItem)
				{
					//Upload this device's ErrorLog to the Cloud for investigation
					CommandContext commandContext = new CommandContext();
					commandContext.setTarget("/console/errorlog");
					Services.getInstance().getCommandService().execute(commandContext);
					return true;
				}
			});
			
			MenuItem item3 = menu.add(Menu.NONE, Menu.NONE, 2, "Clear Error Log");
			item3.setOnMenuItemClickListener(new OnMenuItemClickListener()
			{
				public boolean onMenuItemClick(MenuItem clickedItem)
				{
					ErrorHandler.getInstance().clearAll();
					Toast.makeText(listApp, "Error Log is cleared...", 
					Toast.LENGTH_SHORT).show();
					
					return true;
				}
			});
			
			MenuItem item4 = menu.add(Menu.NONE, Menu.NONE, 3, "Exit");
			item4.setOnMenuItemClickListener(new OnMenuItemClickListener()
			{
				public boolean onMenuItemClick(MenuItem clickedItem)
				{
					listApp.finish();
					return true;
				}
			});
		}
	}
	//-----------------------------------------------------------------------------------------------------------------------------
	private void startActivationWizard()
	{
		Activity currentActivity = Services.getInstance().getCurrentActivity();
		Context context = Registry.getActiveInstance().
		getContext();
		Configuration conf = Configuration.getInstance(context);
		
		if(!conf.isActive())
		{
			//First time activation ever.....
			EditText serverField = new EditText(currentActivity);
			serverField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI);
			
			GenericAttributeManager wizardState = new GenericAttributeManager();
							
			//Setup the Activation Dialog
			wizardState.setAttribute("conf", conf);
			
			
			wizardState.setAttribute("currentLabel", "Server");
			wizardState.setAttribute("currentTextField", serverField);
			new ActivationWizard(wizardState).start();
		}
		else
		{
			//A re-activation
			EditText emailField = new EditText(currentActivity);
			emailField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
			emailField.setText(conf.getEmail());
			
			GenericAttributeManager wizardState = new GenericAttributeManager();
			
			//Setup the Activation Dialog
			wizardState.setAttribute("conf", conf);
			
			
			wizardState.setAttribute("currentLabel", "Email");
			wizardState.setAttribute("currentTextField", emailField);
			new ActivationWizard(wizardState).start();
		}
	}
	
	private void activateDevice(String server, String port, String email, String password)
	{
		Context context = Registry.getActiveInstance().
		getContext();
		Configuration conf = Configuration.getInstance(context);
		CommandContext commandContext = new CommandContext();
		commandContext.setTarget("activate");
		
		//System.out.println("-------------------------------------------------");
		//System.out.println("Server: "+server);
		//System.out.println("Port: "+port);
		//System.out.println("Email: "+email);
		//System.out.println("Password: "+password);
		//System.out.println("-------------------------------------------------");
		
		commandContext.setAttribute(CommandKeys.server, server);
		commandContext.setAttribute(CommandKeys.email, email);
		commandContext.setAttribute(CommandKeys.password, password);
		if(!conf.isActive())
		{
			commandContext.setAttribute("port", port);
		}
		Services.getInstance().getCommandService().execute(commandContext);
	}
			
	private void startPushConfiguration()
	{
		AppResources resources = Services.getInstance().getResources();
		Context context = Registry.getActiveInstance().getContext();
		Configuration conf = Configuration.getInstance(context);
		Activity currentActivity = Services.getInstance().getCurrentActivity();
		if(conf.isActive())
		{
			CommandContext commandContext = new CommandContext();
			commandContext.setTarget("cometStatus");
			Services.getInstance().getCommandService().execute(commandContext);
		}
		else
		{
			Toast.makeText(currentActivity, resources.localize(LocaleKeys.device_inactive, LocaleKeys.device_inactive), 
			Toast.LENGTH_SHORT).show();
		}
	}
	
	private void startSecurityConf()
	{
		AppResources resources = Services.getInstance().getResources();
		Context context = Registry.getActiveInstance().getContext();
		Configuration conf = Configuration.getInstance(context);
		Activity currentActivity = Services.getInstance().getCurrentActivity();
		
		if(conf.isActive())
		{
			Services.getInstance().getNavigationContext().navigate("security");
		}
		else
		{
			Toast.makeText(currentActivity, resources.localize(LocaleKeys.device_inactive, LocaleKeys.device_inactive), 
			Toast.LENGTH_SHORT).show();
		}
	}
	
	private void startManualSyncConf()
	{
		AppResources resources = Services.getInstance().getResources();
		Context context = Registry.getActiveInstance().getContext();
		Configuration conf = Configuration.getInstance(context);
		Activity currentActivity = Services.getInstance().getCurrentActivity();
		
		if(conf.isActive())
		{
			Services.getInstance().getNavigationContext().navigate("manualSync");
		}
		else
		{
			Toast.makeText(currentActivity, resources.localize(LocaleKeys.device_inactive, LocaleKeys.device_inactive), 
			Toast.LENGTH_SHORT).show();
		}
	}
	
	private void startAppStoreConf()
	{
		AppResources resources = Services.getInstance().getResources();
		Context context = Registry.getActiveInstance().getContext();
		Configuration conf = Configuration.getInstance(context);
		Activity currentActivity = Services.getInstance().getCurrentActivity();
		
		if(conf.isActive())
		{
			CommandContext commandContext = new CommandContext();
			commandContext.setTarget("loadAppStore");
			Services.getInstance().getCommandService().execute(commandContext);
		}
		else
		{
			Toast.makeText(currentActivity, resources.localize(LocaleKeys.device_inactive, LocaleKeys.device_inactive), 
			Toast.LENGTH_SHORT).show();
		}
	}
	
	private void checkCloudStatus()
	{
		Activity currentActivity = Services.getInstance().getCurrentActivity();
		AppResources resources = Services.getInstance().getResources();
		Context context = Registry.getActiveInstance().getContext();
		Configuration conf = Configuration.getInstance(context);
		if(conf.isActive())
		{
			CommandContext commandContext = new CommandContext();
			commandContext.setTarget("checkCloudStatus");
			Services.getInstance().getCommandService().execute(commandContext);
		}
		else
		{
			Toast.makeText(currentActivity, resources.localize(LocaleKeys.device_inactive, LocaleKeys.device_inactive), 
			Toast.LENGTH_SHORT).show();
		}
	}
	//-------------------------------------------------------------------------------------------------------------------------------------
	private class ActivationWizard implements DialogInterface.OnClickListener
	{
		private AlertDialog wizard = null;
		private GenericAttributeManager wizardState;
		
		ActivationWizard(GenericAttributeManager wizardState)
		{
			Context context = Registry.getActiveInstance().
			getContext();
			Activity currentActivity = Services.getInstance().getCurrentActivity();
			this.wizardState = wizardState;
			Configuration conf = Configuration.getInstance(context);
			
			this.wizard = new AlertDialog.Builder(currentActivity).
	    	setCancelable(false).
	    	create();
			this.wizard.setButton(DialogInterface.BUTTON_POSITIVE, "OK", this);			
			this.wizard.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", this);
			
			String label = (String)this.wizardState.getAttribute("currentLabel");
			this.wizard.setTitle(label);
			
			EditText textField = (EditText)this.wizardState.getAttribute("currentTextField");
			this.wizard.setView(textField);
			
			if(label.equalsIgnoreCase("server"))
			{
				String value = conf.getServerIp();
				if(value != null && value.trim().length()>0)
				{
					textField.setText(value);
				}
			}
			else if(label.equalsIgnoreCase("port"))
			{
				/*String value = conf.decidePort();
				if(value != null && value.trim().length()>0)
				{
					textField.setText(value);
				}
				else
				{
					textField.setText("1502"); //default port
				}*/
				textField.setText("1502"); //default cleartext port
			}
			else if(label.equalsIgnoreCase("email"))
			{
				String value = conf.getEmail();
				if(value != null && value.trim().length()>0)
				{
					textField.setText(value);
				}
			}
		}
		
		private void start()
		{
			this.wizard.show();
		}
		
		public void onClick(DialogInterface dialog, int status)
		{
			Activity currentActivity = Services.getInstance().getCurrentActivity();
			
			if(status == DialogInterface.BUTTON_NEGATIVE)
			{
				//cancel the wizard
				dialog.cancel();
			}
			else
			{
				Configuration conf = (Configuration)this.wizardState.getAttribute("conf");
				String currentLabel = (String)this.wizardState.getAttribute("currentLabel");
				EditText currentTextField = (EditText)this.wizardState.getAttribute("currentTextField");
				
				//Validation
				if(currentLabel.equalsIgnoreCase("server"))
				{
					String server = currentTextField.getText().toString();
					if(server == null || server.trim().length()==0)
					{
						ViewHelper.getOkModal(currentActivity, "Validation Error", "Server is required. Please try again.").show();
						return;
					}
				}
				
				if(currentLabel.equalsIgnoreCase("port"))
				{
					String port = currentTextField.getText().toString();
					if(port == null || port.trim().length()==0)
					{
						ViewHelper.getOkModal(currentActivity, "Validation Error", "Port is required. Please try again.").show();
						return;
					}
				}
				
				if(currentLabel.equalsIgnoreCase("email"))
				{
					String email = currentTextField.getText().toString();
					if(email == null || email.trim().length()==0)
					{
						ViewHelper.getOkModal(currentActivity, "Validation Error", "Email is required. Please try again.").show();
						return;
					}
				}
				
				if(currentLabel.equalsIgnoreCase("password"))
				{
					String password = currentTextField.getText().toString();
					if(password == null || password.trim().length()==0)
					{
						ViewHelper.getOkModal(currentActivity, "Validation Error", "Password is required. Please try again.").show();
						return;
					}
				}
				
				//go to the next screen
				dialog.cancel();
				if(currentLabel.equalsIgnoreCase("server"))
				{					
					EditText portField = new EditText(currentActivity);
					portField.setInputType(InputType.TYPE_CLASS_NUMBER);	
					
					this.wizardState.setAttribute("currentLabel", "Port");
					this.wizardState.setAttribute("currentTextField", portField);
					this.wizardState.setAttribute("server", currentTextField.getText().toString());
					
					new ActivationWizard(this.wizardState).start();
				}
				else if(currentLabel.equalsIgnoreCase("port"))
				{
					EditText emailField = new EditText(currentActivity);
					emailField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);	
					
					this.wizardState.setAttribute("currentLabel", "Email");
					this.wizardState.setAttribute("currentTextField", emailField);
					this.wizardState.setAttribute("port", currentTextField.getText().toString());
					
					new ActivationWizard(this.wizardState).start();
				}
				else if(currentLabel.equalsIgnoreCase("email"))
				{
					EditText passwordField = new EditText(currentActivity);
					passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
					
					this.wizardState.setAttribute("currentLabel", "Password");
					this.wizardState.setAttribute("currentTextField", passwordField);
					this.wizardState.setAttribute("email", currentTextField.getText().toString());
					
					new ActivationWizard(this.wizardState).start();
				}
				else if(currentLabel.equalsIgnoreCase("password"))
				{
					if(!conf.isActive())
					{
						String server = (String)wizardState.getAttribute("server");
						String port = (String)wizardState.getAttribute("port");
						String email = (String)wizardState.getAttribute("email");
						String password = currentTextField.getText().toString();
						
						HomeScreen.this.activateDevice(server, port, email, password);
					}
					else
					{
						String server = conf.getServerIp();
						String email = (String)wizardState.getAttribute("email");
						String password = currentTextField.getText().toString();						
						HomeScreen.this.activateDevice(server, null, email, password);
					}
				}
			}
		}
	}
}
