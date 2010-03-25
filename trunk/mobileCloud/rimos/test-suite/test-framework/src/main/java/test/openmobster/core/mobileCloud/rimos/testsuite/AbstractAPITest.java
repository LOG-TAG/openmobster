/******************************************************************************
 * OpenMobster                                                                *
 * Copyright 2008, OpenMobster, and individual                                *
 * contributors as indicated by the @authors tag. See the                     *
 * copyright.txt in the distribution for a full listing of                    *
 * individual contributors.                                                   *
 *                                                                            *
 * This is free software; you can redistribute it and/or modify it            *
 * under the terms of the GNU Lesser General Public License as                *
 * published by the Free Software Foundation; either version 2.1 of           *
 * the License, or (at your option) any later version.                        *
 *                                                                            *
 * This software is distributed in the hope that it will be useful,           *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU           *
 * Lesser General Public License for more details.                            *
 *                                                                            *
 * You should have received a copy of the GNU Lesser General Public           *
 * License along with this software; if not, write to the Free                *
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA         *
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.                   *
 ******************************************************************************/
package test.openmobster.core.mobileCloud.rimos.testsuite;

import java.util.Vector;

import org.openmobster.core.mobileCloud.rimos.configuration.Configuration;

import org.openmobster.core.mobileCloud.rimos.module.bus.Bus;
import org.openmobster.core.mobileCloud.rimos.module.bus.SyncInvocation;
import org.openmobster.core.mobileCloud.rimos.module.connection.NetSession;
import org.openmobster.core.mobileCloud.rimos.module.connection.NetworkConnector;
import org.openmobster.core.mobileCloud.rimos.module.mobileObject.MobileObjectDatabase;

import test.openmobster.core.mobileCloud.rimos.testsuite.Test;


/**
 * @author openmobster@gmail.com
 *
 */
public abstract class AbstractAPITest extends Test 
{
	protected final String syncService = "sync://SyncService";
	protected final String service = "testServerBean";
	
	public void setUp() 
	{		
		MobileObjectDatabase.getInstance().deleteAll(service);
		this.resetServerAdapter("setUp="+this.getClass().getName()+"/SetUpAPITestSuite\n");
	}
	
	public void tearDown() 
	{
		MobileObjectDatabase.getInstance().deleteAll(service);
		this.resetServerAdapter("setUp="+this.getClass().getName()+"/CleanUp\n");	
	}
	
	protected void startBootSync() throws Exception
	{		
		SyncInvocation syncInvocation = new SyncInvocation("org.openmobster.core.mobileCloud.invocation.SyncInvocationHandler", 
		SyncInvocation.bootSync, this.service);		
		Bus.getInstance().invokeService(syncInvocation);		
	}
		
	protected void startOneWayDeviceSync() throws Exception
	{		
		SyncInvocation syncInvocation = new SyncInvocation("org.openmobster.core.mobileCloud.invocation.SyncInvocationHandler", 
		SyncInvocation.oneWayDeviceOnly, this.service);		
		Bus.getInstance().invokeService(syncInvocation);		
	}
	
	protected void startOneWayServerSync() throws Exception
	{		
		SyncInvocation syncInvocation = new SyncInvocation("org.openmobster.core.mobileCloud.invocation.SyncInvocationHandler", 
		SyncInvocation.oneWayServerOnly, this.service);		
		Bus.getInstance().invokeService(syncInvocation);		
	}
		
	protected void waitForBeans() throws Exception
	{
		Vector all = new Vector();
		int retries = 30;
		do
		{
			all = MobileObjectDatabase.getInstance().readAll(this.service);
			
			if(all != null && !all.isEmpty())
			{
				break;
			}
							
			Thread.currentThread().sleep(1000);
		}while(retries-- > 0);
	}	
	
	protected void resetServerAdapter(String payload)
	{		
		NetSession netSession = null;
		try
		{
			boolean secure = Configuration.getInstance().isSSLActivated();
			netSession = NetworkConnector.getInstance().openSession(secure);
			
			String response = netSession.sendTwoWay("processorid=testsuite");
			
			if(response.indexOf("status=200")!=-1)
			{
				netSession.sendOneWay(payload);				
			}
		}
		catch(Exception e)
		{
			throw new RuntimeException(e.toString());
		}
		finally
		{
			if(netSession != null)
			{
				netSession.close();
			}
		}
	}
}
