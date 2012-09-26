/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.module.bus;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;

import android.content.Context;

import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android.service.Service;
import org.openmobster.core.mobileCloud.android.module.bus.rpc.InvocationThread;

/**
 * TODO: (low-priority)
 * 
 * 1/ A way to unregister and individual InvocationHandler from its local bus, and have it propagate through the device
 * 
 * 2/ Enforce device-level uniqueness for InvocationHandler URI
 * 
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
			
			//Load the BusRegistration
			BusRegistration reg = BusRegistration.query(this.busId);
			if(reg == null)
			{
				//create this registration
				reg = new BusRegistration(this.busId);
				reg.save();
			}
			else
			{
				//load the handlers based on registration
				Set<String> handlers = reg.getInvocationHandlers();
				if(handlers != null)
				{
					for(String handler:handlers)
					{
						this.invocationHandlers.put(handler, 
						(InvocationHandler)Class.forName(handler).newInstance());
					}
				}
			}
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
			
			InvocationThread invoker = new InvocationThread();
			invoker.setInvocation(invocation);
	
			Thread t = new Thread(invoker);
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
			String targetHandler = invocation.getTarget();
			Set<BusRegistration> all = BusRegistration.queryAll();
			if(all != null)
			{
				for(BusRegistration currentBus:all)
				{
					if(currentBus.getInvocationHandlers().contains(targetHandler))
					{
						//Start an invocation
						InvocationThread invoker = new InvocationThread();
						invocation.setDestinationBus(currentBus.getBusId());
						invoker.setInvocation(invocation);
				
						Thread t = new Thread(invoker);
						t.start();
									
						invocation.startHandshake();
						invocation.setDestinationBus(null);
						invocation.getShared().remove("response");
					}
				}
			}
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
	
	public void register(InvocationHandler handler) throws BusException
	{
		try
		{
			String target = handler.getClass().getName();
			this.invocationHandlers.put(target, handler);
			
			
			BusRegistration reg = BusRegistration.query(busId);
			reg.addInvocationHandler(target);
			reg.save();
		}
		catch(Exception e)
		{						
			BusException be = new BusException(this.getClass().getName(),"register",
			new Object[]{
				"InvocationHandler: "+handler.getClass().getName(),
				"Exception: "+e.toString(),
				"Message: "+e.getMessage()
			});
			ErrorHandler.getInstance().handle(be);
			throw be;
		}
	}
	//---------------------------------------------------------------------------------------------
	public InvocationHandler findHandler(String target)
	{					
		return this.invocationHandlers.get(target);
	}
	
	public String findBus(Invocation invocation)
	{
		try
		{
			Set<BusRegistration> allBuses = BusRegistration.queryAll();
			String targetHandler = invocation.getTarget();
			
			if(allBuses != null)
			{
				for(BusRegistration cour:allBuses)
				{
					if(cour.getInvocationHandlers().contains(targetHandler))
					{
						return cour.getBusId();
					}
				}								
			}
			
			return null;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
