/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.perf.framework.unit.test;

import org.apache.log4j.Logger;

import org.openmobster.cloudConnector.api.Configuration;
import org.openmobster.cloudConnector.api.SecurityConfig;
import org.openmobster.cloudConnector.api.service.MobileService;
import org.openmobster.cloudConnector.api.service.Request;
import org.openmobster.cloudConnector.api.service.Response;
import org.openmobster.core.common.ServiceManager;
import org.openmobster.device.agent.test.framework.MobileBeanRunner;

import junit.framework.TestCase;

/**
 *
 * @author openmobster@gmail.com
 */
public class BandwidthRunnerUnit extends TestCase
{
	private static Logger log = Logger.getLogger(BandwidthRunnerUnit.class);
	
	protected MobileBeanRunner runner;
	
	/**
	 * 
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
		ServiceManager.bootstrap();
		this.runner = (MobileBeanRunner)ServiceManager.locate("mobileBeanRunner");
		this.runner.activateDevice();
		
		Configuration configuration = Configuration.getInstance();
		configuration.setSecurityConfig((SecurityConfig)ServiceManager.locate("/cloudConnector/securityConfig"));
		configuration.setDeviceId(this.runner.getDeviceId());
		configuration.setAuthenticationHash(this.runner.getConfiguration().getAuthenticationHash()); //empty
		configuration.setServerIp("localhost");
		configuration.setServerId("localhost");
		configuration.setSecureServerPort("1500");
		configuration.setPlainServerPort("1502");
		configuration.bootup();
	}

	/**
	 * 
	 */
	protected void tearDown() throws Exception
	{
		super.tearDown();
		
		ServiceManager.shutdown();
	}
	
	
	public void testBandwidthRunner() throws Exception
	{
		Request request = new Request("/perf-framework/bandwidthrunner");
		request.setAttribute("payload", "Hello World");
		
		MobileService service = new MobileService();
		Response response = service.invoke(request);
		
		this.assertNotNull(response);
		
		String statusCode = response.getStatusCode();
		this.assertEquals("200", statusCode);
		
		String payload = response.getAttribute("payload");
		
		//Assert
		this.assertEquals("Hello World", payload);
	}
}
