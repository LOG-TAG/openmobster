/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.location.app;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.command.RemoteCommand;

import org.openmobster.android.api.location.Place;
import org.openmobster.android.api.location.Request;
import org.openmobster.android.api.location.Response;
import org.openmobster.android.api.location.LocationContext;
import org.openmobster.android.api.location.LocationService;
import org.openmobster.android.api.location.Address;

import android.content.Context;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

/**
 *
 * @author openmobster@gmail.com
 */
public final class LoadMyMapCommand implements RemoteCommand
{

	@Override
	public void doViewBefore(CommandContext commandContext)
	{	
	}

	@Override
	public void doAction(CommandContext commandContext)
	{
		try
		{
			//Start fetching my location from GPS
			this.startLocationFinder(commandContext);
			this.waitToFindLocation(commandContext);
			
			//Location should be found
			Location location = (Location)commandContext.getAttribute("location");
			if(location == null)
			{
				throw new RuntimeException("Location Not Found!!");
			}
			
			//Construct a request for the RestaurantBean
			Request request = new Request("restaurants");
			LocationContext locationContext = new LocationContext();
			locationContext.setRequest(request);
			
			//Add the Location coordinates
			locationContext.setLatitude(""+location.getLatitude());
			locationContext.setLongitude(""+location.getLongitude());
			
			//Narrow search to restaurants
			List<String> placeTypes = new ArrayList<String>();
			placeTypes.add("food");
			locationContext.setPlaceTypes(placeTypes);
			
			//Set the search radius
			locationContext.setRadius(1000); //1000 meters
			
			//Make the invocation to the Cloud to make a Location Aware search
			LocationContext responseContext = LocationService.invoke(request, locationContext);
			commandContext.setAttribute("locationContext", responseContext);
			
			/*Response response = responseContext.getResponse();
			
			System.out.println("Response Status: "+response.getStatusMsg());
			
			Map<String,String> coupons = response.getMapAttribute("coupons");
			if(coupons != null)
			{
				Set<String> keys = coupons.keySet();
				for(String key:keys)
				{
					String value = coupons.get(key);
					System.out.println("Key: "+key+",Value: "+value);
				}
			}
			
			System.out.println("Latitude: "+responseContext.getLatitude());
			System.out.println("Longitude: "+responseContext.getLongitude());
			
			List<Place> nearbyPlaces = responseContext.getNearbyPlaces();
			for(Place place:nearbyPlaces)
			{
				System.out.println("Name: "+place.getName());
			}*/
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			throw new RuntimeException(e);
		}
	}
	
	private void startLocationFinder(CommandContext commandContext)
	{
		LocationLooper looper = new LocationLooper();
        looper.start();
        
        while(!looper.isReady());
        
        looper.handler.post(new LocationFetcher(commandContext));

	}
	
	private void waitToFindLocation(CommandContext commandContext)
	{
		int counter = 6;
        while(commandContext.getAttribute("location") == null)
        {
                try{Thread.sleep(5000);}catch(Exception e){}
                
                if(counter-- == 0)
                {
                        break;
                }
        }

	}
	
	private class LocationLooper extends Thread
    {
            private Handler handler;
            
            private LocationLooper()
            {
                    
            }
            
            public void run()
            {
                    Looper.prepare();
                    
                    this.handler = new Handler();
                    
                    Looper.loop();
            }
            
            public boolean isReady()
            {
                    return this.handler != null;
            }
    }
	
	private class LocationFetcher implements Runnable
	{
		private CommandContext commandContext;
		
		private LocationFetcher(CommandContext commandContext)
		{
			this.commandContext = commandContext;
		}
		
		public void run()
		{
			Context context = (Context)this.commandContext.getAttribute("activity");
			
			// Acquire a reference to the system Location Manager
            LocationManager locationManager = (LocationManager)context.
            getSystemService(Context.LOCATION_SERVICE);
            
            //This component receives callback with the results
            LocationListener locationListener = new LocationListenerImpl(this.commandContext);
            
            // Register the listener with the Location Manager to receive location updates
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            
            // Register the listener with the Location Manager to receive location updates
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            
            //Stay open for 10 seconds...get the fix in 10 seconds
            try{Thread.sleep(10000);}catch(Exception e){};
            
            locationManager.removeUpdates(locationListener);
		}
	}
	
	private class LocationListenerImpl implements LocationListener
	{	
		private CommandContext commandContext;
		
		private LocationListenerImpl(CommandContext commandContext)
		{
			this.commandContext = commandContext;
		}

		@Override
		public void onLocationChanged(Location location)
		{
			commandContext.setAttribute("location", location);
		}

		@Override
		public void onProviderDisabled(String provider)
		{	
		}

		@Override
		public void onProviderEnabled(String provider)
		{	
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras)
		{
		}
	}

	@Override
	public void doViewAfter(CommandContext commandContext)
	{
		try
		{
			//Setup the data to be used
			Activity currentActivity = (Activity)commandContext.getAttribute("activity");
			LocationContext locationContext = (LocationContext)commandContext.getAttribute("locationContext");
			Response response = locationContext.getResponse();
			
			//Layout the map
			String layoutClass = currentActivity.getPackageName()+".R$layout";
			String mapView = "map";
			Class clazz = Class.forName(layoutClass);
			Field field = clazz.getField(mapView);
			int screenId = field.getInt(clazz);
			currentActivity.setContentView(screenId);
			
			Drawable marker = currentActivity.getResources().getDrawable(ViewHelper.
					findDrawableId(currentActivity, "marker"));
			
			//Setup the Map
			MapView map = (MapView)ViewHelper.findViewById(currentActivity, "map");
			map.setSatellite(true);
			map.setBuiltInZoomControls(true);
			
			Map<String,String> coupons = response.getMapAttribute("coupons");
			List<Place> restaurants = locationContext.getNearbyPlaces();
			
			//Add restaurant markers and corresponding coupon information
			MyItemizedOverlay restaurantMarkers = new MyItemizedOverlay(marker,map);
			for(Place restaurant:restaurants)
			{
				double latitude = Double.parseDouble(restaurant.getLatitude());
				double longitude = Double.parseDouble(restaurant.getLongitude());
				GeoPoint point = new GeoPoint((int)(latitude * 1E6), (int)(longitude * 1E6));
				OverlayItem item = new OverlayItem(point,restaurant.getName(),coupons.get(restaurant.getId()));
				restaurantMarkers.addOverlay(item);
			}
			
			map.getOverlays().add(restaurantMarkers);
			
			//Some map control related logic
			MapController mapControl = map.getController();
			mapControl.setCenter(restaurantMarkers.getCenter());
			mapControl.setZoom(17);
			mapControl.zoomToSpan(restaurantMarkers.getLatSpanE6(), restaurantMarkers.getLonSpanE6());
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void doViewError(CommandContext commandContext)
	{	
	}
}
