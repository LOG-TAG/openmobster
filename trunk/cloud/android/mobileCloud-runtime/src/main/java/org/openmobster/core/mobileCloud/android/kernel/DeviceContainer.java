/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.kernel;

import java.util.List;
import java.util.ArrayList;

import android.content.Context;

import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android.service.Service;
import org.openmobster.core.mobileCloud.android.storage.Database;
import org.openmobster.core.mobileCloud.android.configuration.Configuration;
import org.openmobster.core.mobileCloud.android.module.bus.Bus;
import org.openmobster.core.mobileCloud.android.module.sync.SyncObjectGenerator;
import org.openmobster.core.mobileCloud.android.module.sync.SyncService;
import org.openmobster.core.mobileCloud.android.module.sync.daemon.Daemon;
import org.openmobster.core.mobileCloud.android.module.sync.daemon.LoadProxyDaemon;
import org.openmobster.core.mobileCloud.android.module.sync.engine.SyncDataSource;
import org.openmobster.core.mobileCloud.api.ui.framework.push.PushRPCInvocationHandler;
import org.openmobster.core.mobileCloud.api.ui.framework.state.AppStateManager;
import org.openmobster.core.mobileCloud.android.module.connection.NetworkConnector;
import org.openmobster.core.mobileCloud.android.module.connection.NotificationListener;
import org.openmobster.core.mobileCloud.android.module.connection.CommandProcessor;
import org.openmobster.core.mobileCloud.android.module.mobileObject.MobileObjectDatabase;
import org.openmobster.core.mobileCloud.android.module.dm.DeviceManager;

import org.openmobster.core.mobileCloud.android.invocation.MockInvocationHandler;
import org.openmobster.core.mobileCloud.android.module.bus.MockBroadcastInvocationHandler;
import org.openmobster.core.mobileCloud.android.invocation.SyncInvocationHandler;
import org.openmobster.core.mobileCloud.android.invocation.StartCometDaemon;
import org.openmobster.core.mobileCloud.android.invocation.CometRecycleHandler;
import org.openmobster.core.mobileCloud.android.invocation.ChannelBootupHandler;
import org.openmobster.core.mobileCloud.android.invocation.CometStatusHandler;
import org.openmobster.core.mobileCloud.android.invocation.CometConfigHandler;
import org.openmobster.core.mobileCloud.android.invocation.SwitchSecurityMode;
import org.openmobster.core.mobileCloud.android.invocation.StopCometDaemon;
import org.openmobster.core.mobileCloud.android.crypto.CryptoManager;
import org.openmobster.core.mobileCloud.android.configuration.AppSystemConfig;


/**
 * Device Container. There should be only a single container instance running on the entire device and is included with the Device Agent installed
 * on the device
 * 
 * @author openmobster@gmail.com
 *
 */
public final class DeviceContainer 
{
	private static DeviceContainer singleton;
	
	private Context context;
	
	private DeviceContainer(Context context)
	{
		this.context = context;
	}
	
	/**
	 * Returns the instance of the device container
	 * 
	 * @return
	 */
	public static DeviceContainer getInstance(Context context)
	{
		if(DeviceContainer.singleton == null)
		{
			synchronized(DeviceContainer.class)
			{
				if(DeviceContainer.singleton == null)
				{
					Registry.getInstance(context).setContainer(true);
					DeviceContainer.singleton = new DeviceContainer(context);
				}
			}
		}
		return DeviceContainer.singleton;
	}
	//------------------------------------------------------------------------------------------------------------------------------------------\
	/**
	 * Starts the Container
	 */
	public synchronized void startup()
	{
		try
		{
			if(this.isContainerActive())
			{
				return;
			}
			
			AppSystemConfig.getInstance().start();
			CryptoManager.getInstance().start();
			Registry.getInstance(this.context);
			Database.getInstance(this.context).connect();
									
			List<Service> services = new ArrayList<Service>();						
			
			//Core Low-Level Services																								
			/*services.addElement(Bus.class);											
			services.addElement(Daemon.class);	
			services.addElement(LoadProxyDaemon.class);	
										
			//Network/Connection services			
			services.addElement(NetworkConnector.class);
												
			//Synchronization Services					
			services.addElement(SyncDataSource.class);								
			services.addElement(SyncObjectGenerator.class);							
			services.addElement(SyncService.class);									
						
			//MobileObject Database services			
			services.addElement(MobileObjectDatabase.class);						
			
			//InvocationHandlers						
			services.addElement(SyncInvocationHandler.class);							
			services.addElement("org.openmobster.core.mobileCloud.api.push.AppNotificationInvocationHandler");
			services.addElement(CometConfigHandler.class);
			services.addElement(StartCometDaemon.class);
			services.addElement(SwitchSecurityMode.class);
			services.addElement(CometRecycleHandler.class);
			services.addElement(CometStatusHandler.class);
			services.addElement(ChannelBootupHandler.class);
			services.addElement(StopCometDaemon.class);
																											
			Registry.getInstance().start(services);									
			
			this.notifyDeviceActivated();	
			
			//Schedules a background task that silently loads proxies from the server
			LoadProxyDaemon.getInstance().scheduleProxyTask();*/
			
			//Core Low-Level Services		
			services.add(new Bus());			
			services.add(new Daemon());	
			services.add(new LoadProxyDaemon());	
			
			//Network/Connection services			
			services.add(new NetworkConnector());
			
			//Synchronization Services					
			services.add(new SyncDataSource());								
			services.add(new SyncObjectGenerator());							
			services.add(new SyncService());					
			
			//MobileObject Database services			
			services.add(new MobileObjectDatabase());
				
			//Moblet App State management service			
			services.add(new AppStateManager());
			
			//Invocation Handlers
			services.add(new MockInvocationHandler());
			services.add(new MockBroadcastInvocationHandler());
			services.add(new SyncInvocationHandler());									
			services.add(new CometConfigHandler());
			services.add(new StartCometDaemon());
			services.add(new SwitchSecurityMode());
			services.add(new CometRecycleHandler());
			services.add(new CometStatusHandler());
			services.add(new ChannelBootupHandler());
			services.add(new StopCometDaemon());
			services.add(new PushRPCInvocationHandler());
			
			//Add the DeviceManager service
			services.add(new DeviceManager());
									
			Registry.getActiveInstance().start(services);
			
			//Start Push Service
			this.notifyDeviceActivated();
			
			//Schedules a background task that silently loads proxies from the server
			LoadProxyDaemon.getInstance().scheduleProxyTask();
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			throw new SystemException(this.getClass().getName(), "startup", new Object[]{
				"Exception="+e.toString(),
				"Message="+e.getMessage()
			});
		}
	}
	
	/**
	 * Shuts down the container
	 */
	public synchronized void shutdown()
	{
		try
		{	
			if(!this.isContainerActive())
			{
				return;
			}
			
			Registry.getActiveInstance().stop();
			Database.getInstance(this.context).disconnect();
			CryptoManager.getInstance().stop();
			AppSystemConfig.stop();
		}
		catch(Exception e)
		{
			throw new SystemException(this.getClass().getName(), "shutdown", new Object[]{
				"Exception="+e.toString(),
				"Message="+e.getMessage()
			});
		}
		finally
		{
			DeviceContainer.singleton = null;
		}
	}
	
	/**
	 * Used to send notification to the container that the device has been successfully activated on the server
	 */
	public synchronized void notifyDeviceActivated()
	{				
		if(this.isContainerActive() && Configuration.getInstance(this.context).isActive())
		{			
			if(NotificationListener.getInstance() == null)
			{
				Registry.getActiveInstance().register(new NotificationListener());
			}
						
			if(CommandProcessor.getInstance() == null)
			{
				Registry.getActiveInstance().register(new CommandProcessor());
			}
		}		
	}
	
	/**
	 * Checks if the Container is currently running on the device
	 * 
	 * @return boolean true: if container is running, false: otherwise
	 */
	public boolean isContainerActive()
	{
		return Registry.getActiveInstance().isStarted();
	}	
	
	public synchronized void propagateNewContext(Context context)
	{
		this.context = context;
		Registry.getActiveInstance().setContext(context);
	}
}
