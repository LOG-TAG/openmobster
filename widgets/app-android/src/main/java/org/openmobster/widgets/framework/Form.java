/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.widgets.framework;

import java.util.List;
import java.util.ArrayList;

import java.io.Serializable;

/**
 * 
 * @author openmobster@gmail.com
 */
public final class Form implements Serializable 
{
	private String layout;
	private String channel;
	
	private List<SpinnerComponent> spinners;
	private List<TextComponent> editTexts;
	private List<CheckBoxComponent> checkboxes;
	private List<RadioButtonComponent> radioButtons;
	private List<ButtonComponent> buttons;
	
	public Form()
	{
		
	}

	public String getLayout() 
	{
		return layout;
	}

	public void setLayout(String layout) 
	{
		this.layout = layout;
	}

	public String getChannel() 
	{
		return channel;
	}

	public void setChannel(String channel) 
	{
		this.channel = channel;
	}
	
	public List<SpinnerComponent> getSpinners()
	{
		if(this.spinners == null)
		{
			this.spinners = new ArrayList<SpinnerComponent>();
		}
		return this.spinners;
	}
	
	public void addSpinner(SpinnerComponent spinner)
	{
		this.getSpinners().add(spinner);
	}
	
	public List<TextComponent> getEditTexts()
	{
		if(this.editTexts == null)
		{
			this.editTexts = new ArrayList<TextComponent>();
		}
		return this.editTexts;
	}
	
	public void addEditText(TextComponent editText)
	{
		this.getEditTexts().add(editText);
	}
	
	public List<CheckBoxComponent> getCheckboxes()
	{
		if(this.checkboxes == null)
		{
			this.checkboxes = new ArrayList<CheckBoxComponent>();
		}
		return this.checkboxes;
	}
	
	public void addCheckbox(CheckBoxComponent checkbox)
	{
		this.getCheckboxes().add(checkbox);
	}
	
	public List<RadioButtonComponent> getRadioButtons()
	{
		if(this.radioButtons == null)
		{
			this.radioButtons = new ArrayList<RadioButtonComponent>();
		}
		return this.radioButtons;
	}
	
	public void addRadioButton(RadioButtonComponent radioButton)
	{
		this.getRadioButtons().add(radioButton);
	}
	
	public List<ButtonComponent> getButtons()
	{
		if(this.buttons == null)
		{
			this.buttons = new ArrayList<ButtonComponent>();
		}
		return this.buttons;
	}
	
	public void addButton(ButtonComponent button)
	{
		this.getButtons().add(button);
	}
}
