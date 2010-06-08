/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.android_native.framework;

import java.util.List;
import java.util.ArrayList;
import java.util.Timer;
import java.util.Vector;
import java.util.TimerTask;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;

import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.moblet.Moblet;

import org.openmobster.core.mobileCloud.api.model.MobileBean;
import org.openmobster.core.mobileCloud.api.system.CometUtil;
import org.openmobster.core.mobileCloud.api.ui.framework.AppConfig;
import org.openmobster.core.mobileCloud.api.ui.framework.AppPushListener;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.NavigationContext;
import org.openmobster.core.mobileCloud.spi.ui.framework.SPIServices;
import org.openmobster.core.mobileCloud.android.module.bus.Bus;
import org.openmobster.core.mobileCloud.android.module.bus.Invocation;
import org.openmobster.core.mobileCloud.android.module.bus.InvocationResponse;
import org.openmobster.core.mobileCloud.android.module.bus.SyncInvocation;
import org.openmobster.core.mobileCloud.android_native.framework.events.NativeEventBusSPI;

/**
 * @author openmobster@gmail.com
 * 
 */
public class App extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		try
		{
			super.onCreate(savedInstanceState);
									
			//Initialize the UI Framework
        	//Load API Services
			Services.getInstance().setResources(new NativeAppResources());
			Services.getInstance().setCommandService(new NativeCommandService());
			
			//Load SPI Services
			SPIServices.getInstance().setNavigationContextSPI(new NativeNavigationContextSPI());
			SPIServices.getInstance().setEventBusSPI(new NativeEventBusSPI());
			
			//Bootstrap the container
			this.bootstrapContainer();      	        	
		} 
		catch (Exception e)
		{
			ErrorHandler.getInstance().handle(new SystemException(this.getClass().getName(), "onCreate", new Object[]{
				"Message:"+e.getMessage(),
				"Exception:"+e.toString()
			}));
			ShowError.showBootstrapError(this);
		}
	}
	
	@Override
	protected void onDestroy()
	{								
    	try
    	{    		    		    		    		    		    		
    		super.onDestroy();
    	}
    	catch(Exception e)
    	{
    		ErrorHandler.getInstance().handle(new SystemException(this.getClass().getName(), "onDestroy", new Object[]{
				"Message:"+e.getMessage(),
				"Exception:"+e.toString()
			}));
    	}
	}
	
	

	@Override
	protected void onResume()
	{
		try
		{
			//Loads the home screen
			super.onResume();
			
			NavigationContext.getInstance().setHome("home");
	    	NavigationContext.getInstance().home();
		}
		catch(Exception e)
		{
			ErrorHandler.getInstance().handle(new SystemException(this.getClass().getName(), 
			"onResume", new Object[]{
				"Message:"+e.getMessage(),
				"Exception:"+e.toString()
			}));
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{				
		menu.clear();
		
		super.onPrepareOptionsMenu(menu);
		
	    NavigationContext.getInstance().setAttribute("options-menu", menu);
	    
	    //This should be a refresh so that current screen is refreshed
	    NavigationContext.getInstance().refresh();
	    
	    return true;
	}
	
	protected void bootstrapContainer() throws Exception
	{
		//Initialize the kernel
		Moblet.getInstance(this).propagateNewContext(this);
    	Moblet.getInstance(this).startup(); 
    	
    	//bootup channels for this app
    	boolean isChannelBootActive = CometUtil.subscribeChannels();
    	
    	//Handle auto sync checking upon App launch, only if channels are not
    	//being initialized
    	if(!isChannelBootActive)
    	{
    		Timer timer = new Timer();
			timer.schedule(new BackgroundSync(), 
			5000);
    	}
    	
    	((AppPushListener)Services.getInstance().getPushListener()).start();
		Services.getInstance().getPushListener().clearNotification();
	}
	
	protected void showError()
	{
		ShowError.showBootstrapError(this);
	}
	//-----------------------------------------------------------------------------------------
	private static class BackgroundSync extends TimerTask
	{
		public void run()
		{			
			try
			{
				List<String> channelsToSync = this.findChannelsToSync();
				if(channelsToSync == null || channelsToSync.isEmpty())
				{
					return;
				}
				
				Invocation invocation = new Invocation("org.openmobster.core.mobileCloud.android.invocation.CometStatusHandler");
				InvocationResponse response = Bus.getInstance().invokeService(invocation);
				String status = response.getValue("status");
				
				//Do this only if the Push Daemon is inactive...this means may be Cloud updates have not been pushed
				//this will synchronize the changes silently
				if(status != null && status.equalsIgnoreCase(""+Boolean.FALSE))
				{
					for(String channel:channelsToSync)
					{
						//check for any new updates, since they may not have been pushed as the push daemon seems to be off
						SyncInvocation syncInvocation = new SyncInvocation(
						"org.openmobster.core.mobileCloud.android.invocation.SyncInvocationHandler", 
						SyncInvocation.oneWayServerOnly, channel);
						syncInvocation.deactivateBackgroundSync(); //so that there are no push notifications...just a quiet sync
						Bus.getInstance().invokeService(syncInvocation);
					}
				}
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
	//-----Global Event Handling------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		NavigationContext navigationContext = NavigationContext.getInstance();
		if(keyCode == KeyEvent.KEYCODE_BACK && !navigationContext.isHome())
		{
			navigationContext.back();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
}
