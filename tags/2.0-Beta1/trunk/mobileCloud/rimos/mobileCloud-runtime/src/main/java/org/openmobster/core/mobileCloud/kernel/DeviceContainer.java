/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.kernel;

import java.util.Vector;

import org.openmobster.core.mobileCloud.rimos.configuration.Configuration;
import org.openmobster.core.mobileCloud.rimos.errors.SystemException;
import org.openmobster.core.mobileCloud.rimos.module.bus.Bus;
import org.openmobster.core.mobileCloud.rimos.module.connection.CommandProcessor;
import org.openmobster.core.mobileCloud.rimos.module.connection.NetworkConnector;
import org.openmobster.core.mobileCloud.rimos.module.connection.NotificationListener;
import org.openmobster.core.mobileCloud.rimos.module.mobileObject.MobileObjectDatabase;
import org.openmobster.core.mobileCloud.rimos.module.sync.SyncObjectGenerator;
import org.openmobster.core.mobileCloud.rimos.module.sync.SyncService;
import org.openmobster.core.mobileCloud.rimos.module.sync.daemon.Daemon;
import org.openmobster.core.mobileCloud.rimos.module.sync.daemon.LoadProxyDaemon;
import org.openmobster.core.mobileCloud.rimos.module.sync.engine.SyncDataSource;
import org.openmobster.core.mobileCloud.rimos.service.Registry;
import org.openmobster.core.mobileCloud.rimos.storage.Database;

import org.openmobster.core.mobileCloud.invocation.SwitchSecurityMode;
import org.openmobster.core.mobileCloud.invocation.SyncInvocationHandler;
import org.openmobster.core.mobileCloud.invocation.CometConfigHandler;
import org.openmobster.core.mobileCloud.invocation.CometRecycleHandler;
import org.openmobster.core.mobileCloud.invocation.CometStatusHandler;
import org.openmobster.core.mobileCloud.invocation.StartCometDaemon;
import org.openmobster.core.mobileCloud.invocation.ChannelBootupHandler;
import org.openmobster.core.mobileCloud.invocation.StopCometDaemon;

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
	
	private DeviceContainer()
	{
		
	}
	
	/**
	 * Returns the instance of the device container
	 * 
	 * @return
	 */
	public static DeviceContainer getInstance()
	{
		if(DeviceContainer.singleton == null)
		{
			synchronized(DeviceContainer.class)
			{
				if(DeviceContainer.singleton == null)
				{
					Registry.getInstance().setContainer(true);
					DeviceContainer.singleton = new DeviceContainer();
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
			Database.getInstance().connect();
						
			if(this.isContainerActive())
			{
				return;
			}
									
			Vector services = new Vector();						
			
			//Core Low-Level Services																								
			services.addElement(Bus.class);											
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
			LoadProxyDaemon.getInstance().scheduleProxyTask();
		}
		catch(Exception e)
		{
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
			
			Registry.getInstance().stop();
			Database.getInstance().disconnect();
		}
		catch(Exception e)
		{
			throw new SystemException(this.getClass().getName(), "shutdown", new Object[]{
				"Exception="+e.toString(),
				"Message="+e.getMessage()
			});
		}
	}
	
	/**
	 * Used to send notification to the container that the device has been successfully activated on the server
	 */
	public synchronized void notifyDeviceActivated()
	{				
		if(this.isContainerActive() && Configuration.getInstance().isActive())
		{			
			if(NotificationListener.getInstance() == null)
			{
				Registry.getInstance().register(new NotificationListener());
			}
			
			
			if(CommandProcessor.getInstance() == null)
			{
				Registry.getInstance().register(new CommandProcessor());
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
		return Registry.getInstance().isStarted();
	}		
}
