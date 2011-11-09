/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileContainer;

import org.openmobster.core.services.LocationServiceMonitor;

/**
 *
 * @author openmobster@gmail.com
 */
public class LocationInvocationService implements ContainerService
{
	private String id;
	private LocationServiceMonitor locationServiceMonitor;
	
	public LocationInvocationService()
	{
		
	}
	
	public void start()
	{
		
	}
	
	public void stop()
	{
		
	}
	
	
	public LocationServiceMonitor getLocationServiceMonitor()
	{
		return locationServiceMonitor;
	}

	public void setLocationServiceMonitor(
			LocationServiceMonitor locationServiceMonitor)
	{
		this.locationServiceMonitor = locationServiceMonitor;
	}

	public void setId(String id)
	{
		this.id = id;
	}
	
	@Override
	public String getId()
	{
		return this.id;
	}

	@Override
	public InvocationResponse execute(Invocation invocation)
			throws InvocationException
	{
		System.out.println("**********************************");
		System.out.println("LocationService to be invoked!!!!!");
		System.out.println("**********************************");
		return null;
	}
}
