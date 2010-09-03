/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.widgets.framework;

import java.io.Serializable;

/**
 * 
 * @author openmobster@gmail.com
 */
public abstract class FormComponent implements Serializable 
{
	private String id;
	
	public FormComponent()
	{
		
	}

	public String getId() 
	{
		return id;
	}

	public void setId(String id) 
	{
		this.id = id;
	}
}
