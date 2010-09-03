/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.widgets.framework;

import java.util.Map;
import java.util.HashMap;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.util.IOUtil;

/**
 * 
 * @author openmobster@gmail.com
 */
public final class WidgetRunner 
{
	private static WidgetRunner singleton;
	
	private Map<String,Form> registeredForms;
	
	private WidgetRunner()
	{
		this.registeredForms = new HashMap<String,Form>();
	}
	
	public static WidgetRunner getInstance()
	{
		if(singleton == null)
		{
			synchronized(WidgetRunner.class)
			{
				if(singleton == null)
				{
					singleton = new WidgetRunner();
					singleton.start();
				}
			}
		}
		return singleton;
	}
	//------------------------------------------------------------------------------------------------------------------------------------------------------------
	public void start()
	{
		try
		{
			//parse the /moblet-app/moblet-app.xml
			InputStream is = WidgetRunner.class.getResourceAsStream("/moblet-app/moblet-app.xml");
			String xml = new String(IOUtil.read(is));
			
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document root = builder.parse(new ByteArrayInputStream(xml.getBytes()));
			
			this.parseFormWidgets(root);
		}
		catch(Exception e)
		{
			SystemException syse = new SystemException(this.getClass().getName(), "start", new Object[]{
				"Exception: "+e.toString(),
				"Message: "+e.getMessage()
			});
			ErrorHandler.getInstance().handle(syse);
			
			throw syse;
		}
	}
	
	public void stop()
	{
		
	}
	
	public Form findForm(String formId)
	{
		return this.registeredForms.get(formId);
	}
	//------------------------------------------------------------------------------------------------------------------------
	private void parseFormWidgets(Document root)
	{
		NodeList formWidgets = root.getElementsByTagName("form");
		if(formWidgets != null && formWidgets.getLength() > 0)
		{
			int length = formWidgets.getLength();
			for(int i=0; i<length; i++)
			{
				Element local = (Element)formWidgets.item(i);
				
				Form form = new Form();
				form.setChannel(local.getAttribute("channel"));
				form.setLayout(local.getAttribute("layout"));
				
				this.parseSpinners(local, form);
				this.parseEditTexts(local, form);
				this.parseCheckBoxes(local, form);
				this.parseRadioButtons(local, form);
				this.parseButtons(local, form);
				
				this.registeredForms.put(form.getLayout(), form);
			}
		}
	}
	
	private void parseSpinners(Element formElem,Form form)
	{
		NodeList spinnerWidgets = formElem.getElementsByTagName("spinner");
		if(spinnerWidgets != null && spinnerWidgets.getLength() > 0)
		{
			int length = spinnerWidgets.getLength();
			for(int i=0; i<length; i++)
			{
				Element local = (Element)spinnerWidgets.item(i);
				
				SpinnerComponent spinner = new SpinnerComponent();
				spinner.setId(local.getAttribute("id"));
				spinner.setProperty(local.getAttribute("property"));
				
				NodeList options = local.getElementsByTagName("option");
				if(options != null && options.getLength() > 0)
				{
					int optionsLength = options.getLength();
					for(int optionIndex=0; optionIndex <optionsLength; optionIndex++)
					{
						Element localOption = (Element)options.item(optionIndex);
						String option = localOption.getFirstChild().getNodeValue();
						spinner.addOption(option);
					}
				}
				
				form.addSpinner(spinner);
			}
		}
	}
	
	private void parseEditTexts(Element formElem,Form form)
	{
		NodeList editTexts = formElem.getElementsByTagName("text");
		if(editTexts != null && editTexts.getLength() > 0)
		{
			int length = editTexts.getLength();
			for(int i=0; i<length; i++)
			{
				Element local = (Element)editTexts.item(i);
				
				TextComponent component = new TextComponent();
				component.setId(local.getAttribute("id"));
				component.setProperty(local.getAttribute("property"));
				
				form.addEditText(component);
			}
		}
	}
	
	private void parseCheckBoxes(Element formElem,Form form)
	{
		NodeList checkBoxes = formElem.getElementsByTagName("checkbox");
		if(checkBoxes != null && checkBoxes.getLength() > 0)
		{
			int length = checkBoxes.getLength();
			for(int i=0; i<length; i++)
			{
				Element local = (Element)checkBoxes.item(i);
				
				CheckBoxComponent component = new CheckBoxComponent();
				component.setId(local.getAttribute("id"));
				component.setProperty(local.getAttribute("property"));
				
				form.addCheckbox(component);
			}
		}
	}
	
	private void parseRadioButtons(Element formElem,Form form)
	{
		NodeList radioButtons = formElem.getElementsByTagName("radio-group");
		if(radioButtons != null && radioButtons.getLength() > 0)
		{
			int length = radioButtons.getLength();
			for(int i=0; i<length; i++)
			{
				Element local = (Element)radioButtons.item(i);
				
				RadioButtonComponent component = new RadioButtonComponent();
				component.setId(local.getAttribute("id"));
				component.setProperty(local.getAttribute("property"));
				
				//Process the buttons belonging to this radio group
				NodeList buttons = formElem.getElementsByTagName("radio");
				if(buttons !=null && buttons.getLength()>0)
				{
					int bLength = buttons.getLength();
					for(int bIndex=0; bIndex<bLength; bIndex++)
					{
						Element bLocal = (Element)buttons.item(bIndex);
						String buttonId = bLocal.getAttribute("id");
						component.addButton(buttonId);
					}
				}
				
				form.addRadioButton(component);
			}
		}
	}
	
	private void parseButtons(Element formElem,Form form)
	{
		NodeList buttons = formElem.getElementsByTagName("button");
		if(buttons != null && buttons.getLength() > 0)
		{
			int length = buttons.getLength();
			for(int i=0; i<length; i++)
			{
				Element local = (Element)buttons.item(i);
				
				ButtonComponent component = new ButtonComponent();
				component.setId(local.getAttribute("id"));
				component.setAction(local.getAttribute("action"));
				component.setTransition(local.getAttribute("transition"));
				String type = local.getAttribute("type");
				if(type.equalsIgnoreCase("submit"))
				{
					component.setType(ButtonType.SUBMIT);
				}
				else if(type.equalsIgnoreCase("cancel"))
				{
					component.setType(ButtonType.CANCEL);
				}
				
				form.addButton(component);
			}
		}
	}
}
