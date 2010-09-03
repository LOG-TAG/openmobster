/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.api.ui.framework;

import org.openmobster.core.mobileCloud.android.util.GenericAttributeManager;

/**
 * 
 * @author openmobster@gmail.com
 */
public class ScreenConfig 
{
	private String id;
	private Class screenClass;
	private GenericAttributeManager configuration;
	
	public ScreenConfig(String id,Class screenClass)
	{
		this.id = id;
		this.screenClass = screenClass;
		this.configuration = new GenericAttributeManager();
	}
		
	public String getId() 
	{
		return id;
	}

	public Class getScreenClass() 
	{
		return screenClass;
	}

	public GenericAttributeManager getConfiguration() 
	{
		return configuration;
	}

	public void addConfigOption(String option,String value)
	{
		this.configuration.setAttribute(option, value);
	}
}
