/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.icloud.cloud;

import java.util.List;

import org.apache.log4j.Logger;
import junit.framework.TestCase;

import org.openmobster.core.common.ServiceManager;

import org.openmobster.device.agent.frameworks.mobileObject.MobileObject;
import org.openmobster.device.agent.test.framework.MobileBeanRunner;

/**
 * @author openmobster@gmail.com
 */
public class TestCloudChannel extends TestCase
{
	private static Logger log = Logger.getLogger(TestCloudChannel.class);
	
	protected MobileBeanRunner runner;
	
	public void setUp() throws Exception
	{
		ServiceManager.bootstrap();
		
		//Bootstrap the runner
		this.runner = (MobileBeanRunner)ServiceManager.locate("mobileBeanRunner");
		this.runner.setApp("testApp");
		this.runner.activateDevice();
		this.runner.bootService();
	}
	
	public void tearDown() throws Exception
	{
		ServiceManager.shutdown();
	}
	
	public void testBootUp() throws Exception
	{
		//Assert the state on the device side
		List<MobileObject> beans = this.runner.readAll();
		assertTrue("Channel should not be empty!!!", (beans != null && !beans.isEmpty()));
		for(MobileObject bean:beans)
		{
			String name = bean.getValue("name");
			String value = bean.getValue("value");
			
			log.info("Name: "+name);
			log.info("Value: "+value);
			
			this.assertTrue(name.equals("Sync Apps") || name.equals("OpenMobster"));
			this.assertTrue(value.equals("Rock") || value.equals("Just Works"));
			
			//Two Way Sync
			MobileObject newObject = new MobileObject();
			newObject.setCreatedOnDevice(true);
			newObject.setStorageId(this.runner.getService());
			newObject.setValue("name", "TwoWaySyncName");
			newObject.setValue("value", "TwoWaySyncValue");
			this.runner.create(newObject);
			
			this.runner.syncService();
			
			//Bootup and find this data
		}
	}
}
