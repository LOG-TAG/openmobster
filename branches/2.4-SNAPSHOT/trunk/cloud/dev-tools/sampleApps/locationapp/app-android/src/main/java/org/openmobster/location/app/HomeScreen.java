/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.location.app;

import java.lang.reflect.Field;

import org.openmobster.core.mobileCloud.android.configuration.Configuration;
import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android.util.GenericAttributeManager;
import org.openmobster.core.mobileCloud.android_native.framework.events.ListItemClickEvent;
import org.openmobster.core.mobileCloud.android_native.framework.events.ListItemClickListener;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.NavigationContext;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.Screen;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.content.Intent;

/**
 *
 * @author openmobster@gmail.com
 */
public class HomeScreen extends Screen
{
	private Integer screenId;
	
	@Override
	public Object getContentPane()
	{
		return this.screenId;
	}
	
	@Override
	public void render()
	{
		try
		{
			final Activity currentActivity = Services.getInstance().getCurrentActivity();
			
			//Layout the Home Screen. The layout is specified in 'res/layout/main.xml'
			String layoutClass = currentActivity.getPackageName()+".R$layout";
			String home = "main";
			Class clazz = Class.forName(layoutClass);
			Field field = clazz.getField(home);
			
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

	@Override
	public void postRender()
	{
		//Get an instance of the currently active Activity
		ListActivity listApp = (ListActivity)Services.getInstance().getCurrentActivity();
		
		//Populate the List with Actions to be performed
		String[] ui = new String[]{"Map by Address","Map by My Location"};
		
		listApp.setListAdapter(new ArrayAdapter(listApp, 
			    android.R.layout.simple_list_item_1, 
			    ui));
		
		ListItemClickListener clickListener = new ClickListener();
		NavigationContext.getInstance().addClickListener(clickListener);
	}
	
	private class ClickListener implements ListItemClickListener
	{
		private ClickListener()
		{
		}
		
		public void onClick(ListItemClickEvent clickEvent)
		{
			int selectedIndex = clickEvent.getPosition();
			Activity currentActivity = Services.getInstance().getCurrentActivity();
			
			switch(selectedIndex)
			{
				case 0:
					//Start Map By Supplied Address
					HomeScreen.this.startAddressWizard();
				break;
				
				case 1:
					//Start Map by My Current Location
					Intent intent = new Intent(currentActivity,LocationMapActivity.class);
					intent.putExtra("target", "/load/my/map");
					currentActivity.startActivity(intent);
				break;
				
				default:
				break;
			}
		}
	}
	
	private void startAddressWizard()
	{
		Activity currentActivity = Services.getInstance().getCurrentActivity();
		
		EditText field = new EditText(currentActivity);
		field.setInputType(InputType.TYPE_CLASS_TEXT);
		
		GenericAttributeManager wizardState = new GenericAttributeManager();
		
		wizardState.setAttribute("currentLabel", "Street");
		wizardState.setAttribute("currentTextField", field);
		new AddressWizard(wizardState).start();
	}
	
	private class AddressWizard implements DialogInterface.OnClickListener
	{
		private AlertDialog wizard = null;
		private GenericAttributeManager wizardState;
		
		AddressWizard(GenericAttributeManager wizardState)
		{
			Activity currentActivity = Services.getInstance().getCurrentActivity();
			this.wizardState = wizardState;
			
			this.wizard = new AlertDialog.Builder(currentActivity).
	    	setCancelable(false).
	    	create();
			this.wizard.setButton(DialogInterface.BUTTON_POSITIVE, "OK", this);			
			this.wizard.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", this);
			
			String label = (String)this.wizardState.getAttribute("currentLabel");
			this.wizard.setTitle(label);
			
			EditText textField = (EditText)this.wizardState.getAttribute("currentTextField");
			this.wizard.setView(textField);
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
				String currentLabel = (String)this.wizardState.getAttribute("currentLabel");
				EditText currentTextField = (EditText)this.wizardState.getAttribute("currentTextField");
				
				//Validation
				
				
				//go to the next screen
				dialog.cancel();
				if(currentLabel.equalsIgnoreCase("street"))
				{					
					EditText field = new EditText(currentActivity);
					field.setInputType(InputType.TYPE_CLASS_TEXT);	
					
					this.wizardState.setAttribute("currentLabel", "City");
					this.wizardState.setAttribute("currentTextField", field);
					this.wizardState.setAttribute("street", currentTextField.getText().toString());
					
					new AddressWizard(this.wizardState).start();
				}
				else if(currentLabel.equalsIgnoreCase("city"))
				{
					EditText field = new EditText(currentActivity);
					field.setInputType(InputType.TYPE_CLASS_TEXT);	
					
					this.wizardState.setAttribute("currentLabel", "Zip");
					this.wizardState.setAttribute("currentTextField", field);
					this.wizardState.setAttribute("city", currentTextField.getText().toString());
					
					new AddressWizard(this.wizardState).start();
				}
				else if(currentLabel.equalsIgnoreCase("zip"))
				{
					String street = (String)wizardState.getAttribute("street");
					String city = (String)wizardState.getAttribute("city");
					String zip = currentTextField.getText().toString();
					
					//Setup the intent to load the map
					Intent intent = new Intent(currentActivity,LocationMapActivity.class);
					intent.putExtra("street", street);
					intent.putExtra("city", city);
					intent.putExtra("zip", zip);
					intent.putExtra("target", "/load/address/map");
					
					//make the call
					currentActivity.startActivity(intent);
				}
			}
		}
	}
}
