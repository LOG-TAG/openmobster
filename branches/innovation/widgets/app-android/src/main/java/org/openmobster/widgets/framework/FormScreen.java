/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.widgets.framework;

import java.util.List;
import java.lang.reflect.Field;

import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;
import org.openmobster.core.mobileCloud.api.model.MobileBean;

import org.openmobster.core.mobileCloud.api.ui.framework.navigation.Screen;
import org.openmobster.core.mobileCloud.api.ui.framework.AppConfig;
import org.openmobster.core.mobileCloud.api.ui.framework.ScreenConfig;

import org.openmobster.widgets.framework.WidgetRunner;
import org.openmobster.widgets.framework.Form;
import org.openmobster.widgets.framework.SpinnerComponent;
import org.openmobster.widgets.framework.SpinnerAdapter;
import org.openmobster.widgets.framework.ButtonComponent;
import org.openmobster.widgets.framework.ButtonClickHandler;

import android.app.Activity;
import android.widget.Spinner;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

/**
 * @author openmobster@gmail.com
 */
public class FormScreen extends Screen
{
	private Integer screenId;
	private Form form;
	private WidgetRunner widgetRunner;
	
	@Override
	public void render()
	{
		try
		{
			final Activity currentActivity = (Activity)Registry.getActiveInstance().
			getContext();
			
			ScreenConfig myConfig = (ScreenConfig)AppConfig.getInstance().getScreenConfig().get(this.getId());
			String form = (String)myConfig.getConfiguration().getAttribute("form");
			
			this.widgetRunner = WidgetRunner.getInstance();
			this.form = this.widgetRunner.findForm(form);
			
			String layoutClass = currentActivity.getPackageName()+".R$layout";
			Class clazz = Class.forName(layoutClass);
			Field field = clazz.getField(form);
			
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
	public Object getContentPane()
	{
		return this.screenId;
	}
	
	@Override
	public void postRender()
	{
		final Activity currentActivity = (Activity)Registry.getActiveInstance().
		getContext();
		
		this.renderForm(currentActivity);
	}
	
	private void renderForm(Activity currentActivity)
    {
		//Handle the spinners
		if(this.form.getSpinners() != null)
		{
			List<SpinnerComponent> spinners = this.form.getSpinners();
			for(SpinnerComponent local:spinners)
			{
		    	final Spinner spinner = (Spinner)ViewHelper.findViewById(currentActivity, local.getId());
			
		    	if(spinner != null)
		    	{
			    	SpinnerAdapter adapter = new SpinnerAdapter(currentActivity,android.R.layout.simple_spinner_item,local.getOptions());
			    	
			    	System.out.println(adapter);
			    	
			    	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				    spinner.setAdapter(adapter);
		    	}
			}
		}
		
		//Add Button Click Listeners
		if(this.form.getButtons() != null)
		{
			List<ButtonComponent> buttons = this.form.getButtons();
			for(ButtonComponent local:buttons)
			{
				Button button = (Button)ViewHelper.findViewById(currentActivity, local.getId());
				if(button != null)
				{
					button.setOnClickListener(local.getType().handler(this.form,local));
				}
			}
		}
	    
	    //Add event listeners
	    /*Button ok = (Button)ViewHelper.findViewById(currentActivity, "ok");
	    ok.setOnClickListener(new OnClickListener() {
	        public void onClick(View v) 
	        {
	        	final Activity currentActivity = (Activity)Registry.getActiveInstance().
				getContext();
	        	
	            // Perform action on clicks
	           System.out.println("-----------------------------");
	           
	           EditText edittext = (EditText)ViewHelper.findViewById(currentActivity, "edittext");
	           String text = edittext.getText().toString();
	           System.out.println("Edit Text: "+text);
	           
	           CheckBox checkBox = (CheckBox)ViewHelper.findViewById(currentActivity, "checkbox");
	           boolean isChecked = checkBox.isChecked();
	           System.out.println("IsChecked: "+isChecked);
	           
	           //TextView selectedView = (TextView)spinner.getSelectedView();
	           //System.out.println("Spinner: "+selectedView.getText());
	           
	           if(MobileBean.isBooted("offlineapp_demochannel"))
	   		   {
	   				MobileBean[] demoBeans = MobileBean.readAll("offlineapp_demochannel");
	   				for(MobileBean bean:demoBeans)
	   				{
	   					System.out.println("Bean: "+bean.getValue("demoString"));
	   				}
	   		   }
	           
	           System.out.println("-----------------------------");
	        }
	    });*/
    }
}
