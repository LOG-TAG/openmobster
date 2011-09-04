/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.device.agent.sync;

import java.util.List;
import org.apache.log4j.Logger;

import org.openmobster.core.common.ServiceManager;
import org.openmobster.core.security.device.Device;
import org.openmobster.core.security.device.DeviceAttribute;
import org.openmobster.core.security.device.DeviceController;
import org.openmobster.device.agent.frameworks.mobileObject.MobileObject;
import org.openmobster.device.agent.test.framework.MobileBeanRunner;

/**
 * @author openmobster@gmail.com
 */
public class TestSyncEventPropagation extends AbstractSync 
{
	private static Logger log = Logger.getLogger(TestSyncEventPropagation.class);
	
	MobileBeanRunner device;
	
	public void setUp() throws Exception
	{
		super.setUp();
		
		//Setup the device '12345'
		this.device = (MobileBeanRunner)ServiceManager.locate("IMEI:0");
		this.device.setApp("testApp");
		this.device.activateDevice();
		
		//Setup other devices for this user
		for(int i=1; i<5; i++)
		{
			Device device = new Device("IMEI:"+i, identityController.read("blah2@gmail.com"));
			device.addAttribute(new DeviceAttribute("nonce", "blahblah"));
			this.deviceController.create(device);
		}
	}
	
	public void testSyncEventTwoWaySync() throws Exception
	{
		log.info("Starting testSyncEventTwoWaySync.............");
		
		this.device.bootService();
		this.print(this.device.readAll());
		
		//Update from device1
		MobileObject unique1 = this.device.read("unique-1");
		unique1.setValue("from", "updated by device 1");
		this.device.update(unique1);
		this.device.syncService();
		
		//Assert
		MobileObject deviceObject = this.device.read("unique-1");
		String deviceFrom = deviceObject.getValue("from");
		this.assertEquals("updated by device 1", deviceFrom);
	}
	
	public void print(List<MobileObject> mobileObjects)
	{
		log.info("***********************************");
		for(MobileObject local:mobileObjects)
		{
			log.info("OID: "+local.getRecordId());
			log.info("From: "+local.getValue("from"));
		}
		log.info("***********************************");
	}
}
