/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.cloud.api.location;

import java.io.Serializable;
import java.util.List;

import org.openmobster.core.common.InVMAttributeManager;

/**
 *
 * @author openmobster@gmail.com
 */
public final class LocationContext implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6163601371562344355L;
	private InVMAttributeManager attributeManager;
	
	private LocationContext()
	{
		this.attributeManager = new InVMAttributeManager();
	}
	
	public static LocationContext getInstance()
	{
		return new LocationContext();
	}
	
	public void setAttribute(String name, Object value)
	{
		this.attributeManager.setAttribute(name, value);
	}
	
	public Object getAttribute(String name)
	{
		return this.attributeManager.getAttribute(name);
	}
	
	public String[] getNames()
	{
		return this.attributeManager.getNames();
	}
	
	public Object[] getValues()
	{
		return this.attributeManager.getValues();
	}
	
	public void removeAttribute(String name)
	{
		this.attributeManager.removeAttribute(name);
	}
	
	public String getLatitude()
	{
		return (String)this.attributeManager.getAttribute("lat");
	}
	
	public void setLatitude(String latitude)
	{
		this.attributeManager.setAttribute("lat", latitude);
	}
	
	public String getLongitude()
	{
		return (String)this.attributeManager.getAttribute("lng");
	}
	
	public void setLongitude(String longitude)
	{
		this.attributeManager.setAttribute("lng", longitude);
	}
	
	public Address getAddress()
	{
		return (Address)this.attributeManager.getAttribute("address");
	}
	
	public void setAddress(Address address)
	{
		this.attributeManager.setAttribute("address", address);
	}
	
	public List<Place> getNearbyPlaces()
	{
		return (List<Place>)this.attributeManager.getAttribute("places");
	}
	
	public void setNearbyPlaces(List<Place> places)
	{
		this.attributeManager.setAttribute("places", places);
	}
}
