/**
 * Copyright (c) {2003,2009} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.hello.mobster.rpc;

import org.apache.log4j.Logger;
import junit.framework.TestCase;

import org.openmobster.core.common.ServiceManager;

import org.openmobster.device.agent.test.framework.MobileBeanRunner;

import org.openmobster.cloudConnector.api.Configuration;
import org.openmobster.cloudConnector.api.SecurityConfig;
import org.openmobster.cloudConnector.api.service.MobileService;
import org.openmobster.cloudConnector.api.service.Request;
import org.openmobster.cloudConnector.api.service.Response;

/**
 * Unit testing the Twitter Service components
 * 
 * @author openmobster@gmail.com
 */
public class TestTwitterService extends TestCase
{
	private static Logger log = Logger.getLogger(TestTwitterService.class);
	
	protected MobileBeanRunner runner;
	
	public void setUp() throws Exception
	{
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
	
	public void tearDown() throws Exception
	{
		ServiceManager.shutdown();
	}
	
	public void testReadTweets() throws Exception
	{
		Request request = new Request("/twitter/tweets");
		request.setAttribute("account", "blah@twitter.com");
		
		MobileService service = new MobileService();
		Response response = service.invoke(request);
		
		this.assertNotNull(response);
		
		assertNotNull(response.getListAttribute("title"));
		assertNotNull(response.getListAttribute("detail"));
		assertNotNull(response.getListAttribute("time"));
	}
}
