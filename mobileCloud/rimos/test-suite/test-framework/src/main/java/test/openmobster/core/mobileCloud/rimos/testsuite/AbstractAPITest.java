/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

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
		int retries = 90;
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
			
			String request =
				"<request>" +
						"<header>" +
						"<name>processor</name>"+
						"<value>testsuite</value>"+
					"</header>"+
				"</request>";
			
			String response = netSession.sendTwoWay(request);
			
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
