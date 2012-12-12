/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.location.cloud;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.openmobster.cloud.api.location.LocationContext;
import org.openmobster.cloud.api.location.LocationServiceBean;
import org.openmobster.cloud.api.location.BeanURI;
import org.openmobster.cloud.api.location.Request;
import org.openmobster.cloud.api.location.Response;
import org.openmobster.cloud.api.location.Place;

/**
 *
 * @author openmobster@gmail.com
 */
@BeanURI(uri="restaurants")
public class RestaurantBean implements LocationServiceBean
{
	private static final String[] coupondb = new String[]{
		"10% off",
		"Buy One Get One Free",
		"Hamburger for a dollar",
		"25% off",
		"50% off",
		"Kids Eat Free",
		"All you can eat buffet"
	};
	
	private Random random;
	
	public void start()
	{
		this.random = new Random();
	}
	
	@Override
	public Response invoke(LocationContext locationContext, Request request)
	{
		Response response = new Response();
		
		//Get coupons associated with each place
		List<Place> nearbyPlaces = locationContext.getNearbyPlaces();
		if(nearbyPlaces != null && !nearbyPlaces.isEmpty())
		{
			Map<String,String> coupons = new HashMap<String,String>();
			for(Place place:nearbyPlaces)
			{
				String placeId = place.getId();
				
				//In a real implementation, you can lookup the coupon in the database based on the Place object
				int couponIndex = (this.random.nextInt())%7;
				couponIndex = Math.abs(couponIndex);
				String coupon = coupondb[couponIndex];
				
				coupons.put(placeId, coupon);
			}
			response.setMapAttribute("coupons", coupons);
		}
		
		return response;
	}
}
