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
public final class InvocationResponse 
{	
	public static String returnValue = "returnValue";
	
	private Hashtable response;
	
	public InvocationResponse()
	{
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
		
		this.getResponse().put(name, value);
	}
	
	public String getValue(String name)
	{
		String value = (String)this.getResponse().get(name);
		
		if(value != null && value.trim().length() == 0)
		{
			value = null;
		}
		
		return value;
	}
	//----------------------------------------------------------------------------------------------------------------------------------------
	public Hashtable getShared()
	{
		return this.getResponse();
	}
	
	public static InvocationResponse createFromShared(Hashtable shared)
	{
		InvocationResponse response = new InvocationResponse();
		response.response = shared;
		return response;
	}
	//----------------------------------------------------------------------------------------------------------------------------------------
	private Hashtable getResponse() 
	{
		if(this.response == null)
		{
			this.response = new Hashtable();
		}
		return this.response;
	}
}
