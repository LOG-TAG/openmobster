/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.showcase.cloud.ajax;

import java.util.List;

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
 * @author openmobster@gmail.com
 */
public class TestServiceInvocation extends TestCase
{
	private static Logger log = Logger.getLogger(TestServiceInvocation.class);
	
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
	
	public void testGetList() throws Exception
	{
		Request request = new Request("/asyncserviceapp/getlist");
		
		MobileService service = new MobileService();
		Response response = service.invoke(request);
		
		this.assertNotNull(response);
		
		String statusCode = response.getStatusCode();
		this.assertEquals("200", statusCode);
		
		List<String> subjects = response.getListAttribute("subjects");
		for(String subject:subjects)
		{
			log.info(subject);
			log.info("------------------------------------");
		}
		this.assertTrue(subjects != null && !subjects.isEmpty());
	}
	
	public void testGetDetails() throws Exception
	{
		Request request = new Request("/asyncserviceapp/getdetails");
		request.setAttribute("oid", ""+1);
		
		MobileService service = new MobileService();
		Response response = service.invoke(request);
		
		this.assertNotNull(response);
		
		String statusCode = response.getStatusCode();
		this.assertEquals("200", statusCode);
		
		String oid = response.getAttribute("oid");
		String from = response.getAttribute("from");
		String to = response.getAttribute("to");
		String subject = response.getAttribute("subject");
		String date = response.getAttribute("date");
		log.info("---------------------------------------");
		log.info("Oid: "+oid);
		log.info("From: "+from);
		log.info("To: "+to);
		log.info("Subject: "+subject);
		log.info("Date: "+date);
		
		//Assert
		this.assertEquals("1", oid);
		this.assertEquals("blah1@gmail.com", from);
		this.assertEquals("openmobster@gmail.com", to);
		this.assertEquals("AsyncServiceApp Mock Bean #1", subject);
	}
}
