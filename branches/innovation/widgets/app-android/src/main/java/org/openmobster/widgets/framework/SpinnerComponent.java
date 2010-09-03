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

/**
 * 
 * @author openmobster@gmail.com
 */
public final class SpinnerComponent extends FormComponent
{
	private String property;
	private List<String> options;
	
	public SpinnerComponent()
	{
		super();
	}

	public String getProperty() 
	{
		return property;
	}

	public void setProperty(String property) 
	{
		this.property = property;
	}

	public List<String> getOptions() 
	{
		if(this.options == null)
		{
			this.options = new ArrayList<String>();
		}
		return options;
	}

	public void addOption(String option)
	{
		this.getOptions().add(option);
	}
}
