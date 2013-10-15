package org.crud.android.system;

import java.util.Timer;
import java.util.TimerTask;

import org.openmobster.core.mobileCloud.android_native.framework.CloudService;
import org.openmobster.core.mobileCloud.moblet.BootupConfiguration;

import android.app.Activity;
import android.util.Log;

public class MyBootstrapper
{
	public static MyBootstrapper myBootstrapper = null;
	public final String TAG = "org.crud.android.system";
	
	private MyBootstrapper()
	{
		
	}
	
	public static MyBootstrapper getInstance()
	{
		if(MyBootstrapper.myBootstrapper == null)
		{
			synchronized(MyBootstrapper.class)
			{
				if(MyBootstrapper.myBootstrapper == null)
				{
					MyBootstrapper.myBootstrapper = new MyBootstrapper();
				}
			}
		}
		return MyBootstrapper.myBootstrapper;
	}
	
	public synchronized void bootstrapUIContainer(Activity mainActivity)
	{
		CloudService.getInstance().start(mainActivity);
	}
	public synchronized void bootstrapUIContainerOnly(Activity activity)
	{
		CloudService.getInstance().startContainer(activity);
		
		if(!this.isDeviceActivated() && CheckConnection.getInstance().isUp(activity.getApplicationContext()))
		{
			Timer timer = new Timer();
			timer.schedule(new DownloadConfigTask(), 0);
		}
	}
	
	private class DownloadConfigTask extends TimerTask
	{
		@Override
		public void run() 
		{
			try
			{
				String server = CloudService.getInstance().getServer();
				String port = CloudService.getInstance().getPort();
				BootupConfiguration.bootup(server, port);
			}
			catch(Exception e)
			{				
				Log.e(TAG, e.getMessage(), e);
			}
		}
	}
	public synchronized boolean isDeviceActivated()
	{
		return CloudService.getInstance().isDeviceActivated();
	}
}
