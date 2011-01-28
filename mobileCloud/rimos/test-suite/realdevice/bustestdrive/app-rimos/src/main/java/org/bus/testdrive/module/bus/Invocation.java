/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.bus.testdrive.module.bus;

import java.util.Hashtable;

/**
 * @author openmobster@gmail.com
 *
 */
public class Invocation 
{	
	private String target;
	private Hashtable input;
	
	public Invocation(String target)
	{
		this.target = target;
	}	
		
	public void setValue(String name, String value)
	{
		if(name == null)
		{
			throw new IllegalArgumentException("Name cannot be Null");
		}
		if(value == null)
		{
			value = "";
		}
		
		this.getInput().put(name, value);
	}
	
	public String getValue(String name)
	{
		String value = (String)this.getInput().get(name);
		
		if(value != null && value.trim().length() == 0)
		{
			value = null;
		}
		
		return value;
	}
	
	public String getTarget() 
	{
		return this.target;
	}
	//----------------------------------------------------------------------------------------------------------------------------------------
	public Hashtable getShared()
	{
		Hashtable shared = this.getInput();
		input.put("target", this.target);
		return shared;
	}
	
	public static Invocation createFromShared(Hashtable shared)
	{
		String target = (String)shared.get("target");
		
		if(target == null || target.trim().length()==0)
		{
			throw new IllegalArgumentException("Invocation Target is missing!!!");
		}
		
		Invocation invocation = new Invocation(target);
		
		shared.remove("target");
		invocation.input = shared;
		
		return invocation;
	}
	//----------------------------------------------------------------------------------------------------------------------------------------
	private Hashtable getInput() 
	{
		if(this.input == null)
		{
			this.input = new Hashtable();
		}
		return this.input;
	}	
}
