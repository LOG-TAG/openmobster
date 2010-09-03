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
public final class ButtonComponent extends FormComponent 
{
	private String action;
	private String transition;
	private ButtonType type;
	
	public ButtonComponent()
	{
		
	}

	public String getAction() 
	{
		return action;
	}

	public void setAction(String action) 
	{
		this.action = action;
	}

	public String getTransition() 
	{
		return transition;
	}

	public void setTransition(String transition) 
	{
		this.transition = transition;
	}

	public ButtonType getType() 
	{
		return type;
	}

	public void setType(ButtonType type) 
	{
		this.type = type;
	}
}
