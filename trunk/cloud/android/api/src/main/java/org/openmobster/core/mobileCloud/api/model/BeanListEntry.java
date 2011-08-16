/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.api.model;

import java.util.Hashtable;

import org.openmobster.core.mobileCloud.android.util.StringUtil;

/**
 * BeanEntry represents members of a BeanList
 * 
 * @author openmobster@gmail.com
 *
 */
public class BeanListEntry 
{
	private Hashtable<String,String> properties;
	private int index;
	private String listProperty;
	
	public BeanListEntry()
	{
		this(0, new Hashtable<String,String>());
	}
	public BeanListEntry(int index, Hashtable<String,String> properties)
	{
		this.properties = properties;
		this.index = index;
	}		
	String getListProperty()
	{
		return this.listProperty;
	}
	void setListProperty(String listProperty)
	{
		this.listProperty = listProperty;
	}
	//Public API-----------------------------------------------------------------------------------------------------------------------
	/**
	 * Reads a property value on this object using a property expression
	 * 
	 * @return the value of the specified property
	 */
	public String getProperty(String propertyExpression)
	{
		String propertyUri = this.calculatePropertyUri(propertyExpression);		
		return this.properties.get(propertyUri);
	}
	
	/**
	 * Set the property value on this object using a property expression
	 * 
	 * @param propertyExpression expression to signify the property to be set
	 * @param value value to be set
	 */
	public void setProperty(String propertyExpression, String value)
	{
		String propertyUri = this.calculatePropertyUri(propertyExpression);
		this.properties.put(propertyUri, value);
	}
	
	/**
	 * All the properties of this object
	 * 
	 * @return all the properties of this object
	 */
	public Hashtable<String,String> getProperties()
	{		
		return this.properties;
	}
	
	/**
	 * If this object carries a single property, read it using getValue
	 * 
	 * @return the value of this object
	 */
	public String getValue()
	{
		if(this.properties.size() == 1)
		{
			String key = this.properties.keys().nextElement();
			if(key.trim().length()==0 || 
			   this.calculatePropertyUri(this.listProperty).endsWith(key.trim()))
			{
				return this.properties.elements().nextElement();
			}
		}
		return null;
	}
	
	/**
	 * Sets the single property of this object
	 * 
	 * @param value value to set
	 */
	public void setValue(String value)
	{
		this.properties.put("", value);
	}
	//--------------------------------------------------------------------------------------------------------------------------------
	private String calculatePropertyUri(String propertyExpression)
	{
		StringBuffer buffer = new StringBuffer();		
		buffer.append("/"+StringUtil.replaceAll(propertyExpression, ".", "/"));
		return buffer.toString();
	}
}
