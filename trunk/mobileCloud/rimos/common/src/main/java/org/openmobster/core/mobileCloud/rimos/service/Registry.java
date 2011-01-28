/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.rimos.service;

import java.util.Vector;
import java.util.Enumeration;

import org.openmobster.core.mobileCloud.rimos.errors.SystemException;
import org.openmobster.core.mobileCloud.rimos.storage.Database;
import org.openmobster.core.mobileCloud.rimos.storage.Record;
import org.openmobster.core.mobileCloud.rimos.util.GeneralTools;


/**
 * The services are registered statically with the Registry since the OS does not support Reflection
 * 
 * Memory Marker - Stateful Component (RAM Usage)
 * 
 * @author openmobster@gmail.com
 *
 */
public final class Registry
{	
	private static String command = "command";
	private static String status = "status";
	private static String response = "response";
	private static String success = "200";
	
	private static Registry singleton;
	
	private Vector services;
	private boolean isStarted;
	private boolean isContainer = true;
	
	private Registry()
	{
	}	
	//---------------------------------------------------------------------------------------------------------------------------------------
	/**
	 * Get an instance of the Service Registry
	 * 
	 * @return the instance of the Service Registry in the system
	 */
	public static Registry getInstance()
	{		
		if(Registry.singleton == null)
		{
			synchronized(Registry.class)
			{
				if(Registry.singleton == null)
				{
					Registry.singleton = new Registry();
				}
			}
		}
		return Registry.singleton;
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
	
	/**
	 * Starts the Service Registry. 
	 * 
	 * @param services initial services to be registered
	 */
	public synchronized void start(Vector initialServices)
	{		
		if(!isContainer)
		{
			//Wait for the container on the device to be started
			this.waitForContainer();
		}
		
		try
		{						
			this.services = new Vector();	
						
			if(initialServices != null)
			{
				int size = initialServices.size();
				for(int i=0; i< size; i++)
				{
					Object serviceObject = initialServices.elementAt(i);
					if(serviceObject instanceof Class)
					{
						Class serviceClass = (Class)initialServices.elementAt(i);					
						this.registerService((Service)serviceClass.newInstance());
					}
					else if(serviceObject instanceof String)
					{
						Class serviceClass = Class.forName((String)serviceObject);					
						this.registerService((Service)serviceClass.newInstance());
					}
				}
			}
			
			
			//Services are started....Make a container footprint so other possible waiting mobile applications
			//depending on the successful start of this container can continue booting
			if(isContainer)
			{
				//Make a container footprint
				this.setContainerStatus(true);								
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
				int size = this.services.size();
				for(int i=0; i< size; i++)
				{
					Service service = (Service)this.services.elementAt(i);
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
			if(isContainer)
			{
				//Cleanup the container footprint on the device
				try
				{
					this.setContainerStatus(false);
				}
				catch(Exception e)
				{
					//Ignore
				}
			}
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
		
		int size = this.services.size();
		for(int i=0; i<size; i++)
		{
			Service cour = (Service)this.services.elementAt(i);
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
	//---Stub Registry Code------------------------------------------------------------------------------------------------------------------------------------	
	private void waitForContainer()
	{
		/*int retry = 15;
		do
		{
			try
			{								
				Database database = Database.getInstance();		
				Enumeration records = database.selectAll(Database.config_table);
				
				Record record = (Record)records.nextElement();				
				String status = record.getValue("container-status");								
				if(status.equals(String.valueOf(Boolean.TRUE)))
				{
					return;
				}
												
				retry--;
			}
			catch(Exception e)
			{				
				retry--;
			}
			finally
			{
				//Sleep 1 second and try again
				try{Thread.currentThread().sleep(1000);}catch(Exception e){}
			}
		}while(retry > 0);*/		
		try
		{
			Database database = Database.getInstance();		
			Enumeration records = database.selectAll(Database.config_table);
			
			Record record = (Record)records.nextElement();				
			String status = record.getValue("container-status");								
			if(status.equals(String.valueOf(Boolean.TRUE)))
			{
				return;
			}
			else
			{
				throw new SystemException(this.getClass().getName(), "waitForContainer", new Object[]
	    		{				
	    			"error=Container is stopped!!!"
	    		}
	    		);
			}
		}
		catch(Exception e)
		{
			throw new SystemException(this.getClass().getName(), "waitForContainer", new Object[]
    		{				
    			"error=Container is stopped!!!"
    		}
    		);
		}
	}	
	//------------Container Registry code----------------------------------------------------------------------------------------------------
	private void setContainerStatus(boolean status) throws Exception
	{
		Database database = Database.getInstance();		
		Enumeration records = database.selectAll(Database.config_table);
		Record record = (Record)records.nextElement();		
		record.setValue("container-status", String.valueOf(status));
		database.update(Database.config_table, record);
	}
	
	private void registerService(Service service)
	{							
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
						
		//Possibly unregister a service if it already exists
		Service registered = this.lookup(service.getClass());
		if(registered != null)
		{
			//try to cleanup
			this.services.removeElement(registered);
			try{registered.stop();}catch(Exception e){}			
		}
				
		//Go ahead and register it
		String serviceId = this.getRegistrationId();
		service.setId(serviceId);
		this.services.addElement(service);				
	}
}
