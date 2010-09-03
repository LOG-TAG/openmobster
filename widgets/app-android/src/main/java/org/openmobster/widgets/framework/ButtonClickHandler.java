/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.widgets.framework;

import java.util.List;

import android.view.View;
import android.view.View.OnClickListener;
import android.app.Activity;
import android.widget.EditText;
import android.widget.CheckBox;

import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;

/**
 * 
 * @author openmobster@gmail.com
 */
public final class ButtonClickHandler implements OnClickListener
{
	private Form form;
	private ButtonComponent button;
	
	public ButtonClickHandler(Form form,ButtonComponent button)
	{
		this.form = form;
		this.button = button;
	}
	
	@Override
	public void onClick(View button) 
	{	
		System.out.println("-----------------------------------------------------");
		System.out.println("ClickHandler invoked on "+this.button.getType().name());
		System.out.println("-----------------------------------------------------");
		
		final Activity currentActivity = (Activity)Registry.getActiveInstance().
		getContext();
		if(this.button.getType() == ButtonType.SUBMIT)
		{
			//prepare state from text boxes
			List<TextComponent> editTexts = this.form.getEditTexts();
			if(editTexts != null)
			{
				for(TextComponent local:editTexts)
				{
					EditText edittext = (EditText)ViewHelper.findViewById(currentActivity, local.getId());
			        String text = edittext.getText().toString();
			        System.out.println("Edit Text: "+text);
				}
			}
			
			//prepare state from check boxes
			List<CheckBoxComponent> checkboxes = this.form.getCheckboxes();
			if(checkboxes != null)
			{
				for(CheckBoxComponent local:checkboxes)
				{
					CheckBox checkBox = (CheckBox)ViewHelper.findViewById(currentActivity, local.getId());
			        boolean isChecked = checkBox.isChecked();
			        System.out.println(local.getId()+" :"+isChecked);
				}
			}
			
			//prepare state from radio buttons
			
			//prepare state from spinner
		}
		else if(this.button.getType() == ButtonType.CANCEL)
		{
			
		}
	}
}
