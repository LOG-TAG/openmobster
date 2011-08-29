/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.app;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

/**
 * This class fixes the current Location coordinates of the device
 * 
 * @author openmobster@gmail.com
 */
public class LocationFinder 
{
	private Location location;
	
	
	/**
	 * Start the process of asking the hardware for location coordinates
	 */
	public void startFind(Context context)
	{
		LocationLooper looper = new LocationLooper();
		looper.start();
		
		while(!looper.isReady());
		
		looper.handler.post(new LocationBootstrapper(context));
	}
	
	/**
	 * End the process of getting the location and cleanup
	 * 
	 * @return the location obtained from the hardware
	 */
	public Location endFind()
	{
		int counter = 6;
		while(this.location == null)
		{
			try{Thread.sleep(5000);}catch(Exception e){}
			
			if(counter-- == 0)
			{
				break;
			}
		}
		
		return this.location;
	}
	
	/**
	 * Used to run location related code on the main thread
	 *
	 * @author openmobster@gmail.com
	 */
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
	
	/**
	 * Bootstraps the Location fetching process
	 *
	 * @author openmobster@gmail.com
	 */
	private class LocationBootstrapper implements Runnable
	{	
		private Context context;
		
		private LocationBootstrapper(Context context)
		{
			this.context = context;
		}
		
		public void run()
		{
			// Acquire a reference to the system Location Manager
			LocationManager locationManager = (LocationManager)context.
			getSystemService(Context.LOCATION_SERVICE);
			
			//This component receives callback with the results
			LocationListener locationListener = new LocationListenerImpl();
			
			// Register the listener with the Location Manager to receive location updates
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
			
			// Register the listener with the Location Manager to receive location updates
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
			
			//Stay open for 10 seconds...get the fix in 10 seconds
			try{Thread.sleep(10000);}catch(Exception e){};
			
			locationManager.removeUpdates(locationListener);
		}
	}
	
	/**
	 * LocationListener that receives the results of reading the location from the hardware
	 *
	 * @author openmobster@gmail.com
	 */
	private class LocationListenerImpl implements LocationListener
	{
		private LocationListenerImpl()
		{
		}

		@Override
		public void onLocationChanged(Location location) 
		{	
			//Set this location
			LocationFinder.this.location = location;
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
}
