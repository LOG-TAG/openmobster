/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.invocation;

import org.openmobster.core.mobileCloud.rimos.module.bus.Bus;
import org.openmobster.core.mobileCloud.rimos.module.bus.Invocation;
import org.openmobster.core.mobileCloud.rimos.module.bus.InvocationHandler;
import org.openmobster.core.mobileCloud.rimos.module.bus.InvocationResponse;
import org.openmobster.core.mobileCloud.rimos.module.bus.SyncInvocation;
import org.openmobster.core.mobileCloud.rimos.module.sync.SyncException;
import org.openmobster.core.mobileCloud.rimos.module.sync.SyncService;
import org.openmobster.core.mobileCloud.rimos.module.sync.daemon.Daemon;
import org.openmobster.core.mobileCloud.rimos.service.Service;
import org.openmobster.core.mobileCloud.rimos.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.rimos.errors.SystemException;

/**
 * @author openmobster@gmail.com
 *
 */
public class SyncInvocationHandler extends Service implements InvocationHandler
{	
	public SyncInvocationHandler()
	{
		
	}	
	
	public void start() 
	{
		try
		{
			Bus.getInstance().register(this);
		}
		catch(Exception e)
		{
			throw new SystemException(this.getClass().getName(), "start", new Object[]{e.getMessage()});
		}
	}
	
	public void stop() 
	{		
	}
	//-------------------------------------------------------------------------------------------------------------------------------------------
	public String getUri() 
	{	
		return this.getClass().getName();
	}

	public InvocationResponse handleInvocation(Invocation invocation) 
	{		
		int syncType = Integer.parseInt(invocation.getValue("type"));
		String service = invocation.getValue("service");
		String recordId = invocation.getValue("recordId");
		boolean isBackground = false;
		String backgroundStatus = invocation.getValue("backgroundSync");
		if(backgroundStatus != null && backgroundStatus.equalsIgnoreCase("true"))
		{
			isBackground = true;
		}
		
		try
		{			
			switch(syncType)
			{
				case SyncInvocation.slow:
					SyncService.getInstance().performSlowSync(service, service, isBackground);
				break;
				
				case SyncInvocation.twoWay:
					SyncService.getInstance().performTwoWaySync(service, service, isBackground);
				break;
				
				case SyncInvocation.oneWayDeviceOnly:
					SyncService.getInstance().performOneWayClientSync(service, service, isBackground);
				break;
				
				case SyncInvocation.oneWayServerOnly:
					SyncService.getInstance().performOneWayServerSync(service, service, isBackground);
				break;
				
				case SyncInvocation.stream:
					SyncService.getInstance().performStreamSync(service, recordId, isBackground);
				break;
				
				case SyncInvocation.bootSync:
					SyncService.getInstance().performBootSync(service, service, isBackground);
				break;
				
				case SyncInvocation.updateChangeLog:
					//Schedule device initiated sync session
					Daemon.getInstance().scheduleSyncInitiation();
					SyncService.getInstance().updateChangeLog(service, invocation.getValue("operation"), recordId);
				break;
			}
		}
		catch(SyncException synce)
		{
			ErrorHandler.getInstance().handle(synce);
		}
		catch(Exception e)
		{
			ErrorHandler.getInstance().handle(new SystemException(
					this.getClass().getName(), "handleInvocation", new Object[]{
						"SyncType="+syncType,
						"SyncService="+service,
						"SyncRecord="+recordId,
						"Exception="+e.toString(),
						"Message="+e.getMessage()
					} 
			));
		}
		return null;
	}	
}
