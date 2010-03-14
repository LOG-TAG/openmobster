/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.android.module.bus.rpc;

import java.util.Map;
import java.util.Set;

import org.openmobster.core.mobileCloud.android.module.bus.Bus;
import org.openmobster.core.mobileCloud.android.module.bus.Invocation;
import org.openmobster.core.mobileCloud.android.module.bus.InvocationResponse;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.content.ServiceConnection;

/**
 * @author openmobster@gmail.com
 *
 */
public class InvocationThread implements Runnable,ServiceConnection
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
			
			//FIXME: add support of inter-app exchange
			Intent busIntent = new Intent();
			busIntent.setClassName(
			Bus.getInstance().getBusId(), 
			"org.openmobster.core.mobileCloud.android.module.bus.rpc.BusService");
			Context context = Registry.getActiveInstance().getContext();
			context.bindService(busIntent, this, Context.BIND_AUTO_CREATE);
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
	}
	
	public void onServiceConnected(ComponentName component, IBinder binder)
	{
		try
		{
			IBusHandler handler = IBusHandler.Stub.asInterface(binder);
			Map result = handler.handleInvocation(this.invocation.getShared());
			
			InvocationResponse response = new InvocationResponse();
			
			if(result != null && !result.isEmpty())
			{
				Set keys = result.keySet();
				for(Object key:keys)
				{
					String value = (String)result.get(key);
					response.setValue((String)key, value);
				}
			}
			
			Map<String, Object> invocationState = this.invocation.getShared();
			invocationState.put("response", response);						
		}
		catch(Exception e)
		{
			SystemException se = new SystemException(this.getClass().getName(),
					"onServiceConnected", new Object[]{
						"Exception: "+e.toString(),
						"Message: "+e.getMessage()
					});
					ErrorHandler.getInstance().handle(se);
		}	
		finally
		{
			try{this.invocation.stopHandshake();}catch(Exception e){}
			
			//Disconnect from the service
			Context context = Registry.getActiveInstance().getContext();
			context.unbindService(this);
		}
	}

	public void onServiceDisconnected(ComponentName component)
	{
		//Nothing to do
	}
}
