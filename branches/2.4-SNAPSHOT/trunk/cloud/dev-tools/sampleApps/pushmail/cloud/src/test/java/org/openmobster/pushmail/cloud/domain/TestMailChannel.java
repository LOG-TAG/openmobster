/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.pushmail.cloud.domain;

import java.util.List;

import org.apache.log4j.Logger;
import junit.framework.TestCase;

import org.openmobster.core.common.ServiceManager;
import org.openmobster.core.common.Utilities;


import org.openmobster.device.agent.frameworks.mobileObject.MobileObject;
import org.openmobster.device.agent.test.framework.MobileBeanRunner;

/**
 * @author openmobster@gmail.com
 */
public class TestMailChannel extends TestCase
{
	private static Logger log = Logger.getLogger(TestMailChannel.class);
	
	protected MobileBeanRunner runner;
	
	public void setUp() throws Exception
	{
		ServiceManager.bootstrap();
		this.runner = (MobileBeanRunner)ServiceManager.locate("mobileBeanRunner");
		this.runner.setApp("testApp");
		this.runner.activateDevice();
		this.runner.bootService();
	}
	
	public void tearDown() throws Exception
	{
		ServiceManager.shutdown();
	}
	
	//Disabling from testsuite as Email credentials don't need to be part of the package
	//for security reasons
	public void testBootUp() throws Exception
	{
		//Assert the state on the device side
		/*List<MobileObject> beans = this.runner.getDeviceDatabase().readByStorage(this.runner.getService());
		assertTrue("On Device pushmail_channel service should not be empty!!!", (beans != null && !beans.isEmpty()));
		
		for(MobileObject mobileObject: beans)
		{
			String encodedMsg = mobileObject.getValue("message");
			String message = new String(Utilities.decodeBinaryData(encodedMsg));
			log.info("BeanId: "+mobileObject.getRecordId());
			log.info("IsFullyLoaded: "+!mobileObject.isProxy());
			if(!mobileObject.isProxy())
			{
				log.info("From: "+mobileObject.getValue("from"));
				log.info("To: "+mobileObject.getValue("to"));
				log.info("Subject: "+mobileObject.getValue("subject"));
				log.info("Date: "+mobileObject.getValue("receivedOn"));
				log.info("Message: "+message);
			}
			log.info("-----------------------------------------------------------------");
		 }*/
	 }
}
