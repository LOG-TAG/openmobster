/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.rimos.module.bus;

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Vector;

import net.rim.device.api.system.ApplicationDescriptor;

import org.openmobster.core.mobileCloud.rimos.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.rimos.errors.SystemException;
import org.openmobster.core.mobileCloud.rimos.service.Registry;
import org.openmobster.core.mobileCloud.rimos.service.Service;
import org.openmobster.core.mobileCloud.rimos.storage.Database;
import org.openmobster.core.mobileCloud.rimos.storage.Record;
import org.openmobster.core.mobileCloud.rimos.util.ObjectShareUtil;

/**
 * TODO: (low-priority)
 * 
 * 1/ A way to unregister and individual InvocationHandler from its local bus, and have it propagate through the device
 * 
 * 2/ Enforce device-level uniqueness for InvocationHandler URI
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
	private long busId;
	private Hashtable invocationHandlers;
	
	//RemoteInvoker Thread related state
	private Thread invocationThread;
		
	
	public Bus()
	{				
	}
		
	public void start()
	{
		try
		{
			this.invocationHandlers = new Hashtable();
			
			if(!Database.getInstance().doesTableExist(Database.bus_registration))
			{
				Database.getInstance().createTable(Database.bus_registration);
			}
			
			this.registerBus();
			
			//Start InterApplication Invocation Thread
			long triggerId = this.initializeInvocationThread();
			this.invocationThread = new InterAppInvocationThread(triggerId);
			this.invocationThread.start();
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
			
			if(this.invocationThread != null)
			{
				try
				{
					((InterAppInvocationThread)this.invocationThread).isContainerStopping = true;
					try{this.stopInterAppInvocationThread();}
					catch(Exception e){}
					
					this.finalizeInvocationThread();					
				}
				finally
				{
					try{this.invocationThread.join();}catch(Exception e){}
					this.invocationThread = null;
				}
			}
		}
		catch(Exception e)
		{
			SystemException syse = new SystemException(this.getClass().getName(), "stop", new Object[]{e.getMessage()});
			ErrorHandler.getInstance().handle(syse);
		}
	}
				
	public static Bus getInstance()
	{
		return (Bus)Registry.getInstance().lookup(Bus.class);
	}
	//----------------------------------------------------------------------------------------------------------------------------------------
	public long getBusId()
	{
		return this.busId;
	}
	//-----------------------------------------------------------------------------------------------------------------------------------------
	/**
	 * Registers the InvocationHandler with the Service Bus infrastructure on the device
	 */
	public void register(InvocationHandler handler) throws BusException
	{
		try
		{
			this.invocationHandlers.put(handler.getUri(), handler);
			BusRegistration registration = this.readRegistration(this.busId);
			registration.addHandler(handler);
			this.updateRegistration(registration);
		}
		catch(Exception e)
		{
			BusException be = new BusException(this.getClass().getName(), "register", new Object[]{
				"InvocationHandler="+handler.getClass()+":"+handler.getUri(),
				"Exception="+e.toString(),
				"Message="+e.getMessage()
			});
			ErrorHandler.getInstance().handle(be);
			throw be;
		}
	}
	
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
			
			BusRegistration registration = this.findBus(invocation);
			
			if(registration == null)
			{
				//There is no InvocationHandler found for this invocation
				return null;
			}
			
			if(registration.getBusId() == this.busId)
			{
				//Its a local invocation
				InvocationHandler handler = this.findInvocationHandler(invocation);
				if(handler != null)
				{
					response = handler.handleInvocation(invocation);
				}
			}
			else
			{
				//Its an inter-application invocation
				long triggerId = this.findTriggerId(registration);				
				Object trigger = ObjectShareUtil.get(triggerId);
												
				long invocationId = ObjectShareUtil.start("start");				
				
				synchronized(trigger)
				{												
					((Hashtable)trigger).put(String.valueOf(invocationId), 
					invocation.getShared());
					trigger.notifyAll();						
				}
				
				//Wait for a response
				int retry = 15;
				Object invocationResponse = null;
				do
				{
					invocationResponse = ObjectShareUtil.get(invocationId);
					if(invocationResponse instanceof Hashtable)
					{
						response = InvocationResponse.createFromShared((Hashtable)invocationResponse);
						break;
					}
					retry --;
					
					Thread.currentThread().sleep(1000);
				}while(retry > 0);
				
				//Cleanup
				ObjectShareUtil.close(invocationId);
			}
			
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
			
			InvocationResponse response = null;
			
			Vector busRegistrations = this.findBuses(invocation);
			
			if(busRegistrations == null || busRegistrations.isEmpty())
			{
				//There is no InvocationHandler found for this invocation
				return;
			}
			
			for(int i=0,size=busRegistrations.size();i<size;i++)
			{
				BusRegistration registration = (BusRegistration)busRegistrations.elementAt(i);
				if(registration.getBusId() == this.busId)
				{
					//System.out.println("Invoking Local Bus-----------------------------------------------");					
					//System.out.println("Bus: "+registration.getBusId());
					//System.out.println("------------------------------------------------------------------");
					
					//Its a local invocation
					InvocationHandler handler = this.findInvocationHandler(invocation);
					if(handler != null)
					{
						response = handler.handleInvocation(invocation);
					}
				}
				else
				{
					//Its an inter-application invocation
					long triggerId = this.findTriggerId(registration);				
					Object trigger = ObjectShareUtil.get(triggerId);
					
					if(trigger == null)
					{
						//Stale Bus
						continue;
					}
					
					//System.out.println("Invoking Remote Bus-----------------------------------------------");
					//System.out.println("InterAppInvocationThread started: "+trigger);
					//System.out.println("Bus: "+registration.getBusId());
					//System.out.println("------------------------------------------------------------------");
													
					long invocationId = ObjectShareUtil.start("start");				
					
					synchronized(trigger)
					{												
						((Hashtable)trigger).put(String.valueOf(invocationId), 
						invocation.getShared());
						trigger.notifyAll();						
					}
					
					//Wait for a response
					int retry = 15;
					Object invocationResponse = null;
					do
					{
						invocationResponse = ObjectShareUtil.get(invocationId);
						if(invocationResponse instanceof Hashtable)
						{
							response = InvocationResponse.createFromShared((Hashtable)invocationResponse);
							break;
						}
						retry --;
						
						Thread.currentThread().sleep(1000);
					}while(retry > 0);
					
					//Cleanup
					ObjectShareUtil.close(invocationId);
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
	//-----------------------------------------------------------------------------------------------------------------------------------------
	private void registerBus() throws Exception
	{
		this.busId = ApplicationDescriptor.currentApplicationDescriptor().getModuleHandle();
		BusRegistration registration = this.readRegistration(this.busId);
		if(registration == null)
		{
			registration = new BusRegistration(this.busId);
			this.createRegistration(registration);
		}
	}
	
	private void unregisterBus() throws Exception
	{
		BusRegistration registration = this.readRegistration(this.busId);
		if(registration != null)
		{
			Record record = registration.getRecord();
			Database.getInstance().delete(Database.bus_registration, record);
		}
	}
	
	private BusRegistration readRegistration(long busId) throws Exception
	{
		BusRegistration registration = null;
		
		Record record = Database.getInstance().select(Database.bus_registration, String.valueOf(busId));
		if(record != null)
		{
			registration = new BusRegistration(record);
		}
		
		return registration;
	}
	
	private void createRegistration(BusRegistration registration) throws Exception
	{
		Record record = registration.getRecord();
		Database.getInstance().insert(Database.bus_registration, record);
	}
	
	private void updateRegistration(BusRegistration registration) throws Exception
	{
		Record record = registration.getRecord();
		
		//Dirty Status
		Record curr = Database.getInstance().select(Database.bus_registration, record.getRecordId());
		record.setDirtyStatus(curr.getDirtyStatus());
		
		Database.getInstance().update(Database.bus_registration, record);
	}
	
	private BusRegistration findBus(Invocation invocation) throws Exception
	{
		BusRegistration registration = null;
						
		Vector matchedBuses = this.findBuses(invocation);
		if(matchedBuses != null && matchedBuses.size()>0)
		{
			Enumeration records = matchedBuses.elements();
			
			//System.out.println("------------------------------------------------");
			BusRegistration cour = null;
			while(records.hasMoreElements())
			{
				BusRegistration curr = (BusRegistration)records.nextElement();
				
				//System.out.println("Bus To Invoke: "+curr.getBusId());
				
				if(this.busId == curr.getBusId())
				{
					return curr;
				}
				else
				{
					cour = curr;
				}
			}
			//System.out.println("------------------------------------------------");
			
			return cour;
		}
		
		return registration;
	}
	
	private Vector findBuses(Invocation invocation) throws Exception
	{
		Vector buses = new Vector();
		
		Enumeration records = Database.getInstance().selectAll(Database.bus_registration);
		if(records != null)
		{
			while(records.hasMoreElements())
			{
				Record record = (Record)records.nextElement();
				BusRegistration curr = new BusRegistration(record);
				if(curr.canBeHandled(invocation))
				{
					buses.addElement(curr);
				}
			}
		}
		
		return buses;
	}
	
	private InvocationHandler findInvocationHandler(Invocation invocation)
	{
		InvocationHandler invocationHandler = null;
		
		String target = invocation.getTarget();
		
		invocationHandler = (InvocationHandler)this.invocationHandlers.get(target);
		
		if(invocationHandler == null)
		{
			//Use other ways to match target
			Enumeration targetIds = this.invocationHandlers.keys();
			while(targetIds.hasMoreElements())
			{
				String targetId = (String)targetIds.nextElement();
				if(this.doesTargetMatch(targetId, target))
				{
					return (InvocationHandler)this.invocationHandlers.get(targetId);					
				}
			}
		}
		
		return invocationHandler;
	}
	
	static boolean doesTargetMatch(String registeredTarget, String invocationTarget)
	{
		boolean match = false;		
		
		if(registeredTarget.startsWith(invocationTarget) && registeredTarget.charAt(invocationTarget.length())=='/')
		{
			return true;
		}
		
		return match;
	}
	//---------------------------------------------------------------------------------------------------------------------------------------------
	private long initializeInvocationThread() throws Exception
	{
		long triggerId = ObjectShareUtil.start(new Hashtable());
		
		Database database = Database.getInstance();		
		Enumeration records = database.selectAll(Database.config_table);
		Record record = (Record)records.nextElement();			
		record.setValue(String.valueOf(this.busId), String.valueOf(triggerId));				
		database.update(Database.config_table, record);		
		
		return triggerId;
	}
	
	private void finalizeInvocationThread() throws Exception
	{
		String triggerId = null;
		
		//Remove the shared object reference from the environment
		Database database = Database.getInstance();		
		Enumeration records = database.selectAll(Database.config_table);
		Record record = (Record)records.nextElement();
		triggerId = record.getValue(String.valueOf(this.busId));
				
		//Remove the shared object from the RuntimeStore
		ObjectShareUtil.close(Long.parseLong(triggerId));
	}
			
	private long getTriggerId() throws Exception
	{
		long triggerId = 0;
		
		Database database = Database.getInstance();		
		Enumeration records = database.selectAll(Database.config_table);
		Record record = (Record)records.nextElement();
		String value = (String)record.getValue(String.valueOf(this.busId));
		if(value != null)
		{
			triggerId = Long.parseLong(value);
		}
		
		return triggerId;
	}
	
	private long findTriggerId(BusRegistration registration) throws Exception
	{
		long triggerId = 0;
		
		Database database = Database.getInstance();		
		Enumeration records = database.selectAll(Database.config_table);
		Record record = (Record)records.nextElement();
		String value = (String)record.getValue(String.valueOf(registration.getBusId()));
		if(value != null)
		{
			triggerId = Long.parseLong(value);
		}
		
		return triggerId;
	}
	
	public boolean recycleRemoteInvoker() 
	{		
		this.stopInterAppInvocationThread();
		return true;
	}
	
	private void stopInterAppInvocationThread()
	{
		if(this.invocationThread != null)
		{			
			try
			{
				long triggerId = ((InterAppInvocationThread)this.invocationThread).triggerId;				
				Object trigger = ObjectShareUtil.get(triggerId);												
				synchronized(trigger)
				{					
					this.invocationThread.interrupt();
				}				
			}
			catch(Exception e)
			{				
				//ignore
			}
		}
	}
	
	private void recycleInvocationThread()
	{
		Thread t = new Thread(
			new Runnable()
			{
				public void run()
				{
					try
					{				
						if(Bus.getInstance().invocationThread != null)
						{
							try
							{					
								if(Bus.getInstance().invocationThread.isAlive())
								{
									try{Bus.getInstance().invocationThread.join();}
									catch(Exception e){}
								}
								
								Bus.getInstance().finalizeInvocationThread();
							}
							finally
							{
								try{Bus.getInstance().invocationThread.join();}catch(Exception e){}
								Bus.getInstance().invocationThread = null;
							}
						}
												
						long triggerId = Bus.getInstance().initializeInvocationThread();
						Bus.getInstance().invocationThread = new InterAppInvocationThread(triggerId);
						Bus.getInstance().invocationThread.start();												
					}
					catch(Exception e)
					{
						//This one is a problem. will need to restart the device.
						//This shouldn't happen but its being caught for robustness
						new SystemException(this.getClass().getName(), "restartInvocationThread", new Object[]{
							"Exception="+e.toString(),
							"Error="+e.getMessage()
						});
						ErrorHandler.getInstance().handle(e);
					}					
				}
			}
		);
		
		t.start();
	}
			
	private static final class InterAppInvocationThread extends Thread
	{
		private long triggerId;	
		private boolean isContainerStopping;
		
		public InterAppInvocationThread(long triggerId)
		{
			this.triggerId = triggerId;
		}
		
		public void run()
		{
			try
			{			
				do
				{					
					Object trigger = ObjectShareUtil.get(this.triggerId);
					
					//System.out.println("BusListening-----------------------------------------------");
					//System.out.println("InterAppInvocationThread: "+trigger);
					//System.out.println("Bus: "+Bus.getInstance().busId);
					//System.out.println("-----------------------------------------------------------");
					
					synchronized(trigger)
					{
						trigger.wait();
					}																				
										
					//Lock down the shared object so that no new notifications are received while this particular
					//request is being processed
					synchronized(trigger)
					{
						Hashtable curr = (Hashtable)trigger;
						try
						{							
							//FIXME: improve performance by spawning worker threads to handle invocations in parallel 
							
							//Start processing the invocations
							Enumeration invocationIds = curr.keys();
							while(invocationIds.hasMoreElements())
							{
								try
								{
									String invocationId = (String)invocationIds.nextElement();
									Hashtable invocationShare = (Hashtable)curr.get(invocationId);
									Invocation invocation = Invocation.createFromShared(invocationShare);
									
									String target = invocation.getTarget();
									Bus mybus = Bus.getInstance();
									BusRegistration registration = mybus.findBus(invocation);
									if(registration == null)
									{
										//System.out.println("-------------------------------------");
										//System.out.println("Bus Registration Not Found: "+Bus.getInstance().busId);
										//System.out.println("-------------------------------------");
										continue;
									}
									
									if(registration.getBusId() == Bus.getInstance().getBusId())
									{
										//System.out.println("Bus Registration Matched-------------------------------------");
										
										InvocationHandler handler = mybus.findInvocationHandler(invocation);
										
										//System.out.println("InvocationHandler to invoke: "+handler);
										
										InvocationResponse response = handler.handleInvocation(invocation);		
										if(response != null)
										{
											ObjectShareUtil.replace(Long.parseLong(invocationId), response.getShared());
										}
										//System.out.println("------------------------------------------------------------");
									}
								}
								catch(Exception invocationException)
								{
									//System.out.println("InterAppBusInvocationError-----------------------------------------------");
									//System.out.println("Bus: "+Bus.getInstance().busId);
									//System.out.println("Exception: "+invocationException.getMessage());
									//System.out.println("-----------------------------------------------------------");
									ErrorHandler.getInstance().handle(new SystemException(
											this.getClass().getName(), "run:/InterAppInvocation/IndividualInvocation", new Object[]{												
												"Exception="+invocationException.toString(),
												"Message="+invocationException.getMessage()
											} 
									));
								}
							}
						}
						catch(Exception e)
						{														
							ErrorHandler.getInstance().handle(new SystemException(
									this.getClass().getName(), "run:/InterAppInvocation", new Object[]{
										"Exception="+e.toString(),
										"Message="+e.getMessage()
									} 
							));
						}
						finally
						{
							curr.clear();
						}
					}
					
				}while(true);
			}
			catch(Exception e)
			{				
			}	
			finally
			{					
				//If this thread dies due to a system failure and not a container stopping, recycle 
				//just this thread only
				if(!isContainerStopping)
				{					
					Bus.getInstance().recycleInvocationThread();
				}
			}
		}		
	}
}
