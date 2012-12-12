/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.service;

import java.util.List;
import java.util.ArrayList;

import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.util.GeneralTools;

import android.content.Context;


/**
 * Memory Marker - Stateful Component (RAM Usage)
 * 
 * @author openmobster@gmail.com
 *
 */
public final class Registry
{
	private static Registry singleton;
	
	private List<Service> services;
	private boolean isStarted;
	private boolean isContainer = true;
	private Context context;
	
	private Registry(Context context)
	{
		this.context = context;
	}	
	//---------------------------------------------------------------------------------------------------------------------------------------
	/**
	 * Get an instance of the Service Registry
	 * 
	 * @return the instance of the Service Registry in the system
	 */
	public static Registry getInstance(Context context)
	{		
		if(Registry.singleton == null)
		{
			synchronized(Registry.class)
			{
				if(Registry.singleton == null)
				{
					Registry.singleton = new Registry(context);
				}
			}
		}
		return Registry.singleton;
	}
	
	public static Registry getActiveInstance()
	{
		if(Registry.singleton == null)
		{
			throw new IllegalStateException("Registry is not active yet!!");
		}
		return Registry.singleton;
	}
	
	public static boolean isActive()
	{
		return (Registry.singleton != null);
	}
	
	public void setContainer(boolean isContainer)
	{
		this.isContainer = isContainer;
	}
	
	public boolean isContainer()
	{
		return this.isContainer;
	}
	
	public boolean isStarted()
	{
		return this.isStarted;
	}	
	
	public Context getContext()
	{
		return this.context;
	}
	
	public void setContext(Context context)
	{
		this.context = context;
	}
	
	public synchronized void validateCloud()
	{
		//Not Applicable...there just so that the API does not break
	}
	
	/**
	 * Starts the Service Registry. 
	 * 
	 * @param services initial services to be registered
	 */
	public synchronized void start(List<Service> initialServices)
	{		
		try
		{	
			this.services = new ArrayList<Service>();
			
			if(initialServices != null)
			{
				for(Service service: initialServices)
				{
					this.registerService(service);
				}
			}
			
			this.isStarted = true;
		}
		catch(Exception e)
		{
			throw new SystemException(Registry.class.getName(), "start", new Object[]{
				"error="+e.getMessage(),
				"message=Registry failed during Startup"
			});
		}
	}
	
	/**
	 * Starts the Registry
	 */
	public synchronized void start()
	{
		this.start(null);
	}
	
	/**
	 * Stops the Registry
	 */
	public synchronized void stop()
	{
		try
		{			
			if(this.services != null)
			{
				for(Service service: this.services)
				{
					try
					{
						service.stop();
					}
					catch(Exception e)
					{
						//ignore
					}
				}				
			}
		}
		finally
		{			
			this.isStarted = false;
			Registry.singleton = null;
		}
	}
	
	/**
	 * Registers a Service with the Registry
	 * 
	 * @param service the Service to be registered
	 */
	public synchronized void register(Service service)
	{		
		if(!this.isStarted())
		{
			throw new IllegalStateException("The Registry is uninitialized");
		}
				
		this.registerService(service);				
	}
	
	public synchronized void restart(Service service)
	{
		if(!this.isStarted())
		{
			throw new IllegalStateException("The Registry is uninitialized");
		}
				
		this.registerService(service);
	}
	
	/**
	 * Searches for an instance of the specified Service within the Registry
	 * 
	 * @param serviceClass Class of the Service that needs to be looked up
	 * @return an instance of the Service registered with the Registry. It returns a null value if the Service is not found
	 * 
	 */
	public Service lookup(Class serviceClass)
	{
		if(this.services == null)
		{
			throw new IllegalStateException("The Registry is uninitialized");
		}
		
		Service service = null;
		for(Service cour: this.services)
		{
			if(cour.getClass() == serviceClass)
			{
				service = cour;
				break;
			}
		}
		
		return service;
	}
	private synchronized String getRegistrationId()
	{
		return String.valueOf(GeneralTools.generateUniqueId());
	}
	
	private void registerService(Service service)
	{	
		//Possibly unregister a service if it already exists
		Service registered = this.lookup(service.getClass());
		if(registered != null)
		{
			//try to cleanup
			this.services.remove(registered);
			try{registered.stop();}catch(Exception e){}			
		}
				
		//Go ahead and register it
		String serviceId = this.getRegistrationId();
		service.setId(serviceId);
		this.services.add(service);	
		
		try
		{
			service.start();
		}
		catch(Exception e)
		{
			throw new SystemException(Registry.class.getName(), "register", new Object[]{
				service.getClass().getName(),
				"message=Service failed during Startup",
				"ErrorType="+e,
				"ErrorMessage="+e.getMessage()
			});
		}			
	}
}
