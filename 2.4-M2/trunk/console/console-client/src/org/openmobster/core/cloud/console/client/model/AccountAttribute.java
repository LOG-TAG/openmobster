/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.cloud.console.client.model;

/**
 * 
 * @author openmobster@gmail.com
 */
public class AccountAttribute 
{
	private long id; //database oid
	private String name;
	private String value;
	
	public AccountAttribute()
	{
		
	}
	
	public AccountAttribute(String name, String value)
	{
		this.name = name;
		this.value = value;
	}

	public long getId() 
	{
		return id;
	}

	public void setId(long id) 
	{
		this.id = id;
	}

	public String getName() 
	{
		return name;
	}

	public void setName(String name) 
	{
		this.name = name;
	}

	public String getValue() 
	{
		return value;
	}

	public void setValue(String value) 
	{
		this.value = value;
	}
	
	public boolean equals(Object o)
	{
		if(o instanceof AccountAttribute)
		{
			AccountAttribute local = (AccountAttribute)o;
			if(local.id > 0 && this.id>0)
			{
				if(local.id == this.id)
				{
					return true;
				}
			}
		}
		
		return false;
	}
}
