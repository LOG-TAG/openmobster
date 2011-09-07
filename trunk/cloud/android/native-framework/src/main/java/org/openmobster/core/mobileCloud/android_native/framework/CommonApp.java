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

import org.openmobster.android.api.sync.MobileBean;
import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.module.bus.Bus;
import org.openmobster.core.mobileCloud.android.module.bus.Invocation;
import org.openmobster.core.mobileCloud.android.module.bus.SyncInvocation;
import org.openmobster.core.mobileCloud.android.module.bus.rpc.IBinderManager;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android_native.framework.events.NativeEventBusSPI;
import org.openmobster.core.mobileCloud.api.ui.framework.AppConfig;
import org.openmobster.core.mobileCloud.api.ui.framework.AppPushListener;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandService;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.NavigationContext;
import org.openmobster.core.mobileCloud.api.ui.framework.push.MobilePush;
import org.openmobster.core.mobileCloud.api.ui.framework.push.PushListener;
import org.openmobster.core.mobileCloud.moblet.Moblet;
import org.openmobster.core.mobileCloud.spi.ui.framework.SPIServices;

import system.CometUtil;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.app.Activity;
import android.content.Intent;

/**
 * @author openmobster@gmail.com
 *
 */
public final class CommonApp
{
	static void onCreate(final Activity activity, final Bundle savedInstanceState)
	{
		Services services = Services.getInstance();
		
		//Initialize the UI Framework
		if(AppConfig.getInstance() == null || !AppConfig.getInstance().isFrameworkActive())
		{
			activity.showDialog(1);
		}
		
    	//Load API Services
		services.setResources(new NativeAppResources());
		services.setCommandService(new NativeCommandService());
		
		//Load SPI Services
		SPIServices.getInstance().setNavigationContextSPI(new NativeNavigationContextSPI());
		SPIServices.getInstance().setEventBusSPI(new NativeEventBusSPI());
		
		//Bootstrap the container
		if(Registry.isActive() && Registry.getActiveInstance().isContainer())
		{
			return;
		}
		
		//This is a Moblet...go on and bootstrap it
		bootstrapContainer(activity);   
	}
	
	static void onStart(final Activity activity)
	{
		//Initialize the UI Framework
		if(AppConfig.getInstance() == null || !AppConfig.getInstance().isFrameworkActive())
		{
			activity.showDialog(1);
		}
		
		Registry registry = Registry.getActiveInstance();
		if(!registry.isContainer())
		{
			registry.validateCloud();
			
			Thread t = new Thread(new Runnable()
			{
				public void run()
				{
					try
					{
						bootstrapActivity(activity);
					}
					catch(Exception e)
					{
						//e.printStackTrace(System.out);
						ErrorHandler.getInstance().handle(new SystemException(this.getClass().getName(), "onStart", new Object[]{
							"Message:"+e.getMessage(),
							"Exception:"+e.toString()
						}));
						activity.showDialog(0);
					}
				}
			}
			);
			t.start();
		}
	}
	
	static void onResume(final Activity activity)
	{
		Intent pushIntent = activity.getIntent();
		NavigationContext navigationContext = NavigationContext.getInstance();
		navigationContext.setHome("home");
		navigationContext.home();
		
		if(pushIntent.getBooleanExtra("push", Boolean.FALSE))
		{
			PushListener pushListener = Services.getInstance().getPushListener();
			MobilePush push = pushListener.getPush();
			Services.getInstance().getPushListener().clearNotification();
			
			//Deliver this Push as a call back to the App layer
			if(push != null)
			{
				try
				{
					CommandService service = Services.getInstance().getCommandService();
					CommandContext commandContext = new CommandContext();
					commandContext.setTarget("push");
					commandContext.setPush(push);
					service.execute(commandContext);
				}
				catch(Exception e)
				{
					//if this fails...app should not fail...Life still goes on
					ErrorHandler.getInstance().handle(new SystemException(CommonApp.class.getName(), "onResume", new Object[]{
						"Message:"+e.getMessage(),
						"Exception:"+e.toString()
					}));
				}
			}
		}
	}
	
	static boolean onPrepareOptionsMenu(final Activity activity, final Menu menu)
	{
		NavigationContext.getInstance().setAttribute("options-menu", menu);
	    
	    //This should be a refresh so that current screen is refreshed
	    NavigationContext.getInstance().refresh();
	    
	    return true;
	}
	
	static boolean onKeyDown(final int keyCode, final KeyEvent event)
	{
		NavigationContext navigationContext = NavigationContext.getInstance();
		if(keyCode == KeyEvent.KEYCODE_BACK && !navigationContext.isHome())
		{
			navigationContext.back();
			return true;
		}
		return false;
	}
	//-----------------------------------------------------------------------------------------------------------------------------------------------------------------------
	static void bootstrapContainer(final Activity activity)
	{
		//Initialize the kernel
		Moblet.getInstance(activity.getApplicationContext()).propagateNewContext(activity);
    	Moblet.getInstance(activity.getApplicationContext()).startup(); 
    	
    	((AppPushListener)Services.getInstance().getPushListener()).start();
	}
	
	static void bootstrapActivity(final Activity activity)
	{
		try
		{
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
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	static void showError(final Activity activity)
	{
		activity.showDialog(0);
	}
}
