/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.module.bus.rpc;

import java.util.Map;
import java.util.Set;

import org.openmobster.core.mobileCloud.android.module.bus.BusException;
import org.openmobster.core.mobileCloud.android.module.bus.Invocation;
import org.openmobster.core.mobileCloud.android.module.bus.InvocationResponse;
import org.openmobster.core.mobileCloud.android.module.bus.rpc.IBinderManager;
import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;

import android.os.IBinder;
import android.os.Parcel;

/**
 * @author openmobster@gmail.com
 *
 */
public class InvocationThread implements Runnable
{
	private Invocation invocation;
	
	public InvocationThread()
	{
		
	}
	
	public void setInvocation(Invocation invocation)
	{
		this.invocation = invocation;
	}
	
	public void run()
	{
		try
		{
			while(!invocation.isHandshakeActivated());
			this.makeInvocation();
		}
		catch(Exception e)
		{
			SystemException se = new SystemException(this.getClass().getName(),
			"run", new Object[]{
				"Exception: "+e.toString(),
				"Message: "+e.getMessage()
			});
			ErrorHandler.getInstance().handle(se);
		}
		finally
		{
			try{this.invocation.stopHandshake();}catch(Exception e){}
		}
	}
	
	private void makeInvocation()
	{
		try
		{
			IBinderManager binderManager = IBinderManager.getInstance();
			
			//Make the appropriate invocation on the proper Bus
			//IBinder binder = binderManager.getBinder(Bus.getInstance().getBusId());
			//IBinder binder = binderManager.getBinder("org.openmobster.core.mobileCloud.android.remote.bus");
			String destinationBus = invocation.calculateDestinationBus();
			if(destinationBus == null)
			{
				BusException busException = new BusException(this.getClass().getName(),
				"makeInvocation", new Object[]{"DestinationBus not found for handler: "+
				invocation.getTarget()});
				throw busException;
			}
			
			IBinder binder = binderManager.getBinder(destinationBus);
			
			if(binder != null)
			{
				InvocationResponse response = new InvocationResponse();
				
				Parcel in = Parcel.obtain();
				Parcel out = Parcel.obtain();
				
				Map<String,Object> invocationState = this.invocation.getShared();
				in.writeMap(invocationState);
				
				//Make the call
				binder.transact(IBinder.FIRST_CALL_TRANSACTION, in, out, 
				0);
				
				//Process the response				
				Map result = out.readHashMap(Thread.currentThread().
				getContextClassLoader());
				if(result != null && !result.isEmpty())
				{
					Set keys = result.keySet();
					for(Object key:keys)
					{
						String value = (String)result.get(key);
						response.setValue((String)key, value);
					}
				}
				
				invocationState.put("response", response);
			}
		}
		catch(Exception e)
		{
			SystemException se = new SystemException(this.getClass().getName(),
					"onServiceConnected", new Object[]{
						"Exception: "+e.toString(),
						"Message: "+e.getMessage()
					});
			ErrorHandler.getInstance().handle(se);
			throw se;
		}			
	}
}
