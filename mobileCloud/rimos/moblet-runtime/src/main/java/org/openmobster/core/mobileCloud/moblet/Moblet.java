/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.moblet;

import java.util.Vector;

import org.openmobster.core.mobileCloud.rimos.errors.SystemException;
import org.openmobster.core.mobileCloud.rimos.module.bus.Bus;
import org.openmobster.core.mobileCloud.rimos.module.connection.NetworkConnector;
import org.openmobster.core.mobileCloud.rimos.module.mobileObject.MobileObjectDatabase;
import org.openmobster.core.mobileCloud.rimos.service.Registry;
import org.openmobster.core.mobileCloud.rimos.storage.Database;
import org.openmobster.core.mobileCloud.api.push.AppNotificationInvocationHandler;
import org.openmobster.core.mobileCloud.api.ui.framework.state.AppStateManager;

/**
 * Application Container. There is one instance of an Application Container deployed per Application. Application Container provides
 * services that proxy system level requests to the Device Container to optimize resource usage like 
 * "Open a Comet Socket to the Server, so that each application does not have an Open Comet Socket for notifications" etc 
 * 
 * @author openmobster@gmail.com
 *
 */
public final class Moblet 
{
	private static Moblet singleton;
	
	private Moblet()
	{
		
	}
	
	/**
	 * Returns the instance of the device container
	 * 
	 * @return
	 */
	public static Moblet getInstance()
	{
		if(Moblet.singleton == null)
		{
			synchronized(Moblet.class)
			{
				if(Moblet.singleton == null)
				{
					Registry.getInstance().setContainer(false);
					Moblet.singleton = new Moblet();
				}
			}
		}
		return Moblet.singleton;
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
						
			services.addElement(Bus.class);
						
			services.addElement(MobileObjectDatabase.class);
			services.addElement(AppNotificationInvocationHandler.class);
			
			services.addElement(NetworkConnector.class);
			
			//Moblet App State management service			
			services.addElement(AppStateManager.class);
										
			Registry.getInstance().start(services);
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
	 * Checks if the Container is currently running on the device
	 * 
	 * @return boolean true: if container is running, false: otherwise
	 */
	public boolean isContainerActive()
	{
		return Registry.getInstance().isStarted();
	}
}
