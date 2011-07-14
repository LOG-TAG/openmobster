/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.moblet;

import java.util.List;
import java.util.ArrayList;

import android.content.Context;
import android.app.Activity;

import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.module.bus.Bus;
import org.openmobster.core.mobileCloud.android.module.bus.rpc.IBinderManager;
import org.openmobster.core.mobileCloud.android.module.connection.NetworkConnector;
import org.openmobster.core.mobileCloud.android.module.mobileObject.MobileObjectDatabase;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android.service.Service;
import org.openmobster.core.mobileCloud.android.storage.Database;
import org.openmobster.core.mobileCloud.api.ui.framework.state.AppStateManager;
import org.openmobster.core.mobileCloud.api.push.AppNotificationInvocationHandler;
import org.openmobster.core.mobileCloud.api.push.PushRPCInvocationHandler;

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
	
	private Activity context;
	
	private Moblet(Activity context)
	{
		this.context = context;
	}
	
	/**
	 * Returns the instance of the device container
	 * 
	 * @return
	 */
	public static Moblet getInstance(Activity context)
	{
		if(Moblet.singleton == null)
		{
			synchronized(Moblet.class)
			{
				if(Moblet.singleton == null)
				{
					Registry.getInstance(context).setContainer(false);
					Moblet.singleton = new Moblet(context);
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
			Database.getInstance(this.context).connect();
			
			if(this.isContainerActive())
			{
				return;
			}
									
			List<Service> services = new ArrayList<Service>();
						
			services.add(new Bus());
			services.add(new IBinderManager());
												
			//add this for handling Push notifications
			services.add(new AppNotificationInvocationHandler());
			
			services.add(new PushRPCInvocationHandler());
			
			services.add(new NetworkConnector());
			
			services.add(new MobileObjectDatabase());
			
			//Moblet App State management service			
			services.add(new AppStateManager());
										
			Registry.getActiveInstance().start(services);
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
			
			Registry.getActiveInstance().stop();
			Database.getInstance(this.context).disconnect();
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
			Moblet.singleton = null;
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
	
	public synchronized void propagateNewContext(Activity context)
	{
		this.context = context;
		Registry.getActiveInstance().setContext(context);
	}
}
