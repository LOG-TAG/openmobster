/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.app;

import java.util.List;
import java.util.ArrayList;

import android.content.Context;
import android.location.Location;
import android.location.Geocoder;
import android.location.Address;

/**
 * Geo codes the input Location and provides an address associated with the location
 *
 * @author openmobster@gmail.com
 */
public class GeoFinder 
{
	public List<String> geocode(Context context, Location location)
	{
		//Get the android Geocoder
		Geocoder geocoder = new Geocoder(context);
		try
		{
			List<String> addresses = new ArrayList<String>();
			
			//Get the addresses corresponding to the input location
			List<Address> addr = geocoder.getFromLocation(location.getLatitude(),
			location.getLongitude(), 3);
			
			if(addr != null)
			{
				for(Address address:addr)
				{
					String placeName = address.getLocality();
					String featureName = address.getFeatureName();
					String country = address.getCountryName();
					String road = address.getThoroughfare();
					String locInfo = String.format("\n[%s] [%s] [%s] [%s]", placeName,featureName,road,country);
					addresses.add(locInfo);
				}
			}
			
			return addresses;
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			throw new RuntimeException(e);
		}
	}
}
