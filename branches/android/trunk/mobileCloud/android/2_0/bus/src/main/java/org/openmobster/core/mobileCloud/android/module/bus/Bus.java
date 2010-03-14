/**
 * Copyright (c) {2003,2009} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.module.bus;

import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Method;

import android.content.Context;

import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android.service.Service;

/**
 * TODO: (low-priority)
 * 
 * 1/ A way to unregister and individual InvocationHandler from its local bus, and have it propagate through the device
 * 
 * 2/ Enforce device-level uniqueness for InvocationHandler URI
 * 
 */

/**
 * FIXME: 
 * 
 * Figure out the appropriate BusId (App) that should be sent the Invocation without
 * introducing this knowledge into an Invocation. Invocation should only carry its InvocationHandler
 * as a target (probably some kind of shared bus registry)
 */

/**
 * Represents the Service Bus installed as part of each mobile application's infrastructure stack. It is used for making inter-application Service Bus 
 * Invocations on the mobile device
 * 
 * Memory Marker - Stateful Component (RAM Usage)
 * 
 * @author openmobster@gmail.com
 *
 */
public final class Bus extends Service
{			
	/**
	 * unique Bus Id. Unique with respect to all active Buses on the mobile device
	 */
	private String busId;
	private Map<String, InvocationHandler> invocationHandlers;
	
	
	public Bus()
	{				
	}
		
	public void start()
	{
		try
		{
			this.invocationHandlers = new HashMap<String, InvocationHandler>();
			
			Context context = Registry.getActiveInstance().getContext();
			this.busId = context.getPackageName();
		}
		catch(Exception e)
		{
			throw new SystemException(this.getClass().getName(), "start", new Object[]{e.getMessage()});
		}
	}
	
	public void stop()
	{
		try
		{
			this.invocationHandlers = null;
			this.busId = null;
		}
		catch(Exception e)
		{
			SystemException syse = new SystemException(this.getClass().getName(), "stop", new Object[]{e.getMessage()});
			ErrorHandler.getInstance().handle(syse);
		}
	}
				
	public static Bus getInstance()
	{
		return (Bus)Registry.getActiveInstance().lookup(Bus.class);
	}
	//----------------------------------------------------------------------------------------------------------------------------------------
	public String getBusId()
	{
		return this.busId;
	}
	//-----------------------------------------------------------------------------------------------------------------------------------------
	/**
	 * Invokes a Service registered with the ServiceBus on the device
	 * 
	 * @param input
	 * @return
	 */
	public InvocationResponse invokeService(Invocation invocation) throws BusException
	{
		try
		{
			InvocationResponse response = null;
			
			Runnable r = (Runnable)Class.
			forName("org.openmobster.core.mobileCloud.android.module.bus.rpc.InvocationThread").
			newInstance();
			
			Method setInvocation = r.getClass().getMethod("setInvocation", 
			Invocation.class);
			setInvocation.invoke(r, invocation);
			
			Thread t = new Thread(r);
			t.start();
			
			invocation.startHandshake();
			
			response = (InvocationResponse)invocation.getShared().get("response");
			
			return response;
		}
		catch(Exception e)
		{
			BusException be = new BusException(this.getClass().getName(), "invokeService", new Object[]{
				"Invocation="+invocation.toString(),
				"Exception="+e.toString(),
				"Message="+e.getMessage()
			});
			ErrorHandler.getInstance().handle(be);
			throw be;
		}
	}
	
	/**
	 * Sends an invocation broadcast for multiple invocations on the device. The Invocation responses of the
	 * Handlers are ignored
	 * 
	 * @param input
	 * @return
	 */
	public void broadcast(Invocation invocation) throws BusException
	{
		try
		{
			//TODO: Make this an async call...Not urgent
			//Currently this is invoked by background components....so usability should not be affected
			//by the wait
			
			this.invokeService(invocation);
		}
		catch(Exception e)
		{
			BusException be = new BusException(this.getClass().getName(), "broadcast", new Object[]{
				"Invocation="+invocation.toString(),
				"Exception="+e.toString(),
				"Message="+e.getMessage()
			});
			ErrorHandler.getInstance().handle(be);
			throw be;
		}
	}
	
	public InvocationHandler findHandler(String target)
	{
		InvocationHandler handler = this.invocationHandlers.get(target);
		
		if(handler == null)
		{
			try
			{
				handler = (InvocationHandler)Class.forName(target).newInstance();
				this.invocationHandlers.put(target, handler);
			}
			catch(Exception e)
			{
				//some reason handler cannot be found or initialized...bogus class etc
				return null;
			}
		}
		
		return handler;
	}
}
