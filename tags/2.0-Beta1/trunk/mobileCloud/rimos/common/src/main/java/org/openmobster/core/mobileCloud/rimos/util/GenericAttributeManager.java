/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.rimos.util;

import java.util.Hashtable;
import java.util.Enumeration;

/**
 * @author openmobster@gmail.com
 */
public final class GenericAttributeManager
{
	private Hashtable attributes;
	
	public GenericAttributeManager()
	{
		this.attributes = new Hashtable();
	}
	
	public void setAttribute(String name, Object value)
	{
		this.attributes.put(name, value);
	}
	
	public Object getAttribute(String name)
	{
		return this.attributes.get(name);
	}
	
	public void removeAttribute(String name)
	{
		this.attributes.remove(name);
	}
	
	public String[] getNames()
	{
		String[] names = new String[this.attributes.size()];
		
		
		Enumeration keys = this.attributes.keys();
		int i = 0;
		while(keys.hasMoreElements())
		{
			names[i++] = (String)keys.nextElement();
		}
		
		return names;
	}
	
	public Object[] getValues()
	{
		Object[] values = new Object[this.attributes.size()];
		
		
		Enumeration elements = this.attributes.elements();
		int i = 0;
		while(elements.hasMoreElements())
		{
			values[i++] = (Object)elements.nextElement();
		}
		
		return values;
	}
	
	public boolean isEmpty()
	{
		if(this.attributes == null || this.attributes.isEmpty())
		{
			return true;
		}
		return false;
	}
}
