/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.push;

import java.util.TimerTask;

import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.module.bus.Bus;
import org.openmobster.core.mobileCloud.android.module.bus.SyncInvocation;

/**
 *
 * @author openmobster@gmail.com
 */
public final class StartSync extends TimerTask
{
	private String channel;
	private String silent;
	
	public StartSync(String channel,String silent)
	{
		this.channel = channel;
		this.silent = silent;
	}
	
	public void run()
	{
		try
		{
			SyncInvocation syncInvocation = new SyncInvocation("org.openmobster.core.mobileCloud.android.invocation.SyncInvocationHandler", 
					SyncInvocation.twoWay, channel);
					
					if(silent == null || silent.equals("false"))
					{
						syncInvocation.activateBackgroundSync();
					}
					else
					{
						syncInvocation.deactivateBackgroundSync();
					}
					
					Bus.getInstance().invokeService(syncInvocation);
		}
		catch(Throwable t)
		{
			t.printStackTrace(System.out);
			SystemException syse = new SystemException(this.getClass().getName(),"run",new Object[]{
				"Exception: "+t.toString(),
				"Message: "+t.getMessage()
			});
			ErrorHandler.getInstance().handle(syse);
		}
		finally
		{
			//makes sure this task does not execute anymore
			this.cancel();
		}
	}
}
