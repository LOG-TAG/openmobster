/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileContainer;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmobster.core.services.LocationServiceMonitor;

import org.openmobster.cloud.api.location.LocationContext;
import org.openmobster.cloud.api.location.Response;
import org.openmobster.cloud.api.location.Request;
import org.openmobster.cloud.api.ExecutionContext;

import org.openmobster.core.location.GeoCodeProvider;
import org.openmobster.core.location.PlaceProvider;
import org.openmobster.core.location.AddressSPI;
import org.openmobster.core.location.PlaceSPI;

/**
 *
 * @author openmobster@gmail.com
 */
public class LocationInvocationService implements ContainerService
{
	private String id;
	private LocationServiceMonitor locationServiceMonitor;
	private GeoCodeProvider geocoder;
	private PlaceProvider placeProvider;
	
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
	
	public GeoCodeProvider getGeocoder()
	{
		return geocoder;
	}

	public void setGeocoder(GeoCodeProvider geocoder)
	{
		this.geocoder = geocoder;
	}
	
	public PlaceProvider getPlaceProvider()
	{
		return placeProvider;
	}

	public void setPlaceProvider(PlaceProvider placeProvider)
	{
		this.placeProvider = placeProvider;
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
		try
		{
			LocationContext locationContext = ExecutionContext.getInstance().getLocationContext();
			Request request = invocation.getLocationRequest();
			
			System.out.println("**********************************");
			System.out.println("Service: "+request.getService());
			System.out.println("Latitude: "+locationContext.getLatitude());
			System.out.println("Longitude: "+locationContext.getLongitude());
			System.out.println("**********************************");
			
			String[] names = request.getNames();
			for(String name:names)
			{
				if(name.equals("list") || name.equals("map"))
				{
					continue;
				}
				
				String value = request.getAttribute(name);
				System.out.println(name+":"+value);
			}
			
			List<String> myList = request.getListAttribute("list");
			for(String cour:myList)
			{
				System.out.println(cour);
			}
			
			Map<String,String> myMap = request.getMapAttribute("map");
			Set<String> keys = myMap.keySet();
			for(String key:keys)
			{
				String value = myMap.get(key);
				
				System.out.println(key+":"+value);
			}
			
			//Geocode
			String latitude = locationContext.getLatitude();
			String longitude = locationContext.getLongitude();
			List<AddressSPI> addresses = this.geocoder.reverseGeoCode(latitude, longitude);
			for(AddressSPI address:addresses)
			{
				System.out.println("**************************");
				System.out.println("Street: "+address.getStreet());
			}
			
			InvocationResponse response = InvocationResponse.getInstance();
			Response locationResponse = new Response();
			response.setLocationResponse(locationResponse);
			
			return response;
		}
		catch(Exception e)
		{
			InvocationException ine = new InvocationException(e.getMessage());
			throw ine;
		}
	}
}
