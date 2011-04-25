/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.app;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

/**
 * 
 * @author openmobster@gmail.com
 */
public class DemoService extends Service
{
	private boolean isRunning = true;
	
	@Override
	public IBinder onBind(Intent intent) 
	{
		//Not implemented...this sample is only for starting and stopping services.
		//Service binding will be covered in another tutorial
		return null;
	}
	
	
	@Override
	public void onCreate() 
	{
		super.onCreate();
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		super.onStartCommand(intent, flags, startId);
		
		//Announcement about starting
		Toast.makeText(this, "Starting the Demo Service", Toast.LENGTH_SHORT).show();
		
		//Start a Background thread
		isRunning = true;
		Thread backgroundThread = new Thread(new BackgroundThread());
		backgroundThread.start();
		
	    // We want this service to continue running until it is explicitly
	    // stopped, so return sticky.
	    return START_STICKY;
	}

	@Override
	public void onDestroy() 
	{
		super.onDestroy();
		
		//Stop the Background thread
		isRunning = false;
		
		//Announcement about stopping
		Toast.makeText(this, "Stopping the Demo Service", Toast.LENGTH_SHORT).show();
	}
	
	private class BackgroundThread implements Runnable
	{
		int counter = 0;
		public void run()
		{
			try
			{
				counter = 0;
				while(isRunning)
				{
					System.out.println(""+counter++);
					Thread.currentThread().sleep(5000);
				}
				
				System.out.println("Background Thread is finished.........");
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
