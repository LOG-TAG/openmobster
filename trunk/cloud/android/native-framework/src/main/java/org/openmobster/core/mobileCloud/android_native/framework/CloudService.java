/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android_native.framework;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.module.bus.Bus;
import org.openmobster.core.mobileCloud.android.module.bus.Invocation;
import org.openmobster.core.mobileCloud.android.module.bus.SyncInvocation;
import org.openmobster.core.mobileCloud.android.module.bus.rpc.IBinderManager;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android_native.framework.events.NativeEventBusSPI;
import org.openmobster.core.mobileCloud.api.model.MobileBean;
import org.openmobster.core.mobileCloud.api.system.CometUtil;
import org.openmobster.core.mobileCloud.api.ui.framework.AppConfig;
import org.openmobster.core.mobileCloud.api.ui.framework.AppPushListener;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.moblet.Moblet;
import org.openmobster.core.mobileCloud.spi.ui.framework.SPIServices;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

/**
 * @author openmobster@gmail.com
 *
 */
public class CloudService
{
	private static CloudService singleton;
	
	private CloudService()
	{
	}
	
	public static CloudService getInstance()
	{
		if(singleton == null)
		{
			synchronized(CloudService.class)
			{
				if(singleton == null)
				{
					singleton = new CloudService();
				}
			}
		}
	
		return singleton;
	}
			
	public void start(final Context context)
	{
		//short-fast boostrapping of the kernel
		if(!Moblet.getInstance(context).isContainerActive())
		{
			this.bootstrapContainer(context);
		}
		
		//longer background services to be executed in a background thread to not hold up the App launch
		Thread t = new Thread(new Runnable()
		{
			public void run()
			{
				try
				{
					bootstrapApplication(context);
				}
				catch(Exception e)
				{
					//e.printStackTrace(System.out);
					ErrorHandler.getInstance().handle(new SystemException(this.getClass().getName(), "onStart", new Object[]{
						"Message:"+e.getMessage(),
						"Exception:"+e.toString()
					}));
					
					//FIXME: Find a way to notify the user of this error
					//This is on a non-GUI thread with no handle on the current Activity
					/*ShowErrorLooper looper = new ShowErrorLooper();
					looper.start();
					
					while(!looper.isReady());
					
					looper.handler.post(new ShowError());*/
				}
			}
		}
		);
		t.start();
	}
	
	public void stop(final Context context)
	{
		
	}
	//---------------------------------------------------------------------------------------------------------------------
	private void bootstrapContainer(final Context context)
	{
		//Initialize some of the higher level services
		Services services = Services.getInstance();
		//Load API Services
		services.setResources(new NativeAppResources());
		services.setCommandService(new NativeCommandService());
		
		//Load SPI Services
		SPIServices.getInstance().setNavigationContextSPI(new NativeNavigationContextSPI());
		SPIServices.getInstance().setEventBusSPI(new NativeEventBusSPI());
		
		//Initialize the kernel
		Moblet.getInstance(context).propagateNewContext(context);
    	Moblet.getInstance(context).startup(); 
    	
    	((AppPushListener)Services.getInstance().getPushListener()).start();
	}
	
	private void bootstrapApplication(final Context context) throws Exception
	{
		Registry registry = Registry.getActiveInstance();
		
		if(!registry.isContainer())
		{
			registry.validateCloud();
			
			//Wait till connected to cloud
			int waitBeforeAbort = 10;
			IBinderManager bm = IBinderManager.getInstance();
			while(!bm.isConnectedToCloud())
			{
				if(waitBeforeAbort-- > 0)
				{
					Thread.currentThread().sleep(1000);
				}
				else
				{
					throw new RuntimeException("Cloud Not Found!!");
				}
			}
			
			Invocation invocation = new Invocation("org.openmobster.core.mobileCloud.android.invocation.RegisterIBinder");
			invocation.setValue("packageName", Bus.getInstance().getBusId());
			Bus.getInstance().invokeService(invocation);
			
	    	//Handle auto sync checking upon App launch, only if channels are not
	    	//being initialized
	    	Timer timer = new Timer();
			timer.schedule(new BackgroundSync(), 
			5000);
		}
	}
	
	private static class BackgroundSync extends TimerTask
	{
		public void run()
		{			
			try
			{
				//bootup channels for this app
		    	CometUtil.subscribeChannels();
		    	
				List<String> channelsToSync = this.findChannelsToSync();
				if(channelsToSync == null || channelsToSync.isEmpty())
				{
					return;
				}
				
				//Do this only if the Push Daemon is inactive...this means may be Cloud updates have not been pushed
				//this will synchronize the changes silently
				for(String channel:channelsToSync)
				{
					//start the app session with a two way sync
					SyncInvocation syncInvocation = new SyncInvocation(
					"org.openmobster.core.mobileCloud.android.invocation.SyncInvocationHandler", 
					SyncInvocation.twoWay, channel);
					syncInvocation.deactivateBackgroundSync(); //so that there are no push notifications...just a quiet sync
					Bus.getInstance().invokeService(syncInvocation);
				}
				
				//Do a proxy sync
				SyncInvocation syncInvocation = new SyncInvocation(
						"org.openmobster.core.mobileCloud.android.invocation.SyncInvocationHandler", 
				SyncInvocation.proxySync);
				syncInvocation.deactivateBackgroundSync(); //so that there are no push notifications...just a quiet sync
				Bus.getInstance().invokeService(syncInvocation);
			}
			catch(Throwable t)
			{
				SystemException syse = new SystemException(this.getClass().getName(),"run",new Object[]{
					"Exception: "+t.toString(),
					"Message: "+t.getMessage()
				});
				ErrorHandler.getInstance().handle(syse);
			}
			finally
			{
				//makes sure this task does not execute anymore
				this.cancel();
			}
		}
		
		private List<String> findChannelsToSync()
		{
			List<String> channelsToSync = new ArrayList<String>();
			
			AppConfig appConfig = AppConfig.getInstance();
			Vector appChannels = appConfig.getChannels();
			if(appChannels != null)
			{
				int size = appChannels.size();
				for(int i=0; i<size; i++)
				{
					String channel = (String)appChannels.get(i);
					
					if(MobileBean.isBooted(channel))
					{
						channelsToSync.add(channel);
					}
				}
			}
			
			return channelsToSync;
		}
	}
	
	private class ShowErrorLooper extends Thread
	{
		private Handler handler;
		
		private ShowErrorLooper()
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
	
	private class ShowError implements Runnable
	{
		public void run()
		{
		}
	}
}
