/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.widgets.framework;

/**
 * 
 * @author openmobster@gmail.com
 */
public final class CheckBoxComponent extends FormComponent
{
	private String property;
	
	public CheckBoxComponent()
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
}
