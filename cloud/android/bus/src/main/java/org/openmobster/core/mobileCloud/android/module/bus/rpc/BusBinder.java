/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.module.bus.rpc;

import java.io.Serializable;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import org.openmobster.core.mobileCloud.android.module.bus.Bus;
import org.openmobster.core.mobileCloud.android.module.bus.Invocation;
import org.openmobster.core.mobileCloud.android.module.bus.InvocationResponse;
import org.openmobster.core.mobileCloud.android.module.bus.InvocationHandler;
import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;

import android.os.Binder;
import android.os.Parcel;
import android.os.RemoteException;

/**
 * @author openmobster@gmail.com
 *
 */
public final class BusBinder extends Binder implements Serializable
{
	@Override
	protected boolean onTransact(int code, Parcel data, Parcel reply, int flags)
	throws RemoteException
	{
		try
		{	
			Map response = new HashMap();
			Map invocation = data.readHashMap(Thread.currentThread().
			getContextClassLoader());
	
			Invocation in = Invocation.createFromShared(invocation);
			Bus bus = Bus.getInstance();
			
			InvocationHandler handler = bus.findHandler(in.getTarget());
			
			if(handler != null)
			{
				InvocationResponse out = handler.handleInvocation(in);
				
				if(out != null)
				{
					Map<String,String> output = out.getShared();
					if(output != null)
					{
						Set<String> keys = output.keySet();
						for(String key:keys)
						{
							response.put(key, output.get(key));
						}
					}
				}
			}
			
			reply.writeMap(response);
			return true;
		}
		catch(Throwable t)
		{
			t.printStackTrace(System.out);
			
			SystemException se = new SystemException(this.getClass().getName(),"handleInvocation",
			new Object[]{
				"Exception: "+t.toString(),
				"Message: "+t.getMessage()
			});
			ErrorHandler.getInstance().handle(se);
			
			throw new RemoteException();
		}
	}
}
