/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.core.security.identity;

import org.apache.log4j.Logger;

import java.util.Set;
import java.util.HashSet;

import junit.framework.TestCase;

import org.openmobster.core.common.ServiceManager;

import org.openmobster.core.security.identity.IdentityController;
import org.openmobster.core.security.identity.Identity;
import org.openmobster.core.security.identity.IdentityAttribute;
import org.openmobster.core.security.device.DeviceController;
import org.openmobster.core.security.device.Device;


/**
 * @author openmobster@gmail.com
 */
public class TestMultiDeviceUsecases extends TestCase
{
	private static Logger log = Logger.getLogger(TestMultiDeviceUsecases.class);
	
	private IdentityController identityController;
	private DeviceController deviceController;
	
	/**
	 * 
	 */
	public void setUp()
	{
		ServiceManager.bootstrap();
		this.identityController = (IdentityController)ServiceManager.locate("security://IdentityController");
		this.deviceController = (DeviceController)ServiceManager.locate("security://DeviceController");
	}
	
	/**
	 * 
	 */
	public void tearDown()
	{
		ServiceManager.shutdown();
		this.identityController = null;
		this.deviceController = null;
	}
	
	
	public void testCreate() throws Exception
	{
		//Assert
		assertNotNull(this.identityController);
		Identity stored = this.identityController.read("admin");
		assertNull(stored);
		
		Identity identity = new Identity();
		identity.setPrincipal("admin");
		identity.setCredential("adminPassword");
		
		//Add Identity Attributes
		Set<IdentityAttribute> attributes = new HashSet<IdentityAttribute>();
		attributes.add(new IdentityAttribute("email", "blah@gmail.com"));
		identity.setAttributes(attributes);
		
		//Add Devices
		for(int i=0; i<5; i++)
		{
			Device device = new Device("imei:"+i,identity);
			identity.addDevice(device);
		}
		
		//Add this identity to the database
		this.identityController.create(identity);
		
		//Assert the state of the database
		stored = this.identityController.read("admin");
		assertNotNull(stored);
		
		assertEquals("Principal does not match", "admin", stored.getPrincipal());
		assertEquals("Credential does not match", "adminPassword", stored.getCredential());
		assertTrue("Improper ID assigned", stored.getId()>0);
		
		assertNotNull("Identity Attributes Not Found", stored.getAttributes());
		IdentityAttribute storedAttribute = stored.getAttributes().iterator().next();
		assertEquals("Email Attribute Not Found", "email", storedAttribute.getName());
		assertEquals("Email Value does not match", "blah@gmail.com", storedAttribute.getValue());
		assertTrue("Improper ID assigned", storedAttribute.getId()>0);
		
		//Assert device from identity side
		Set<Device> devices = stored.getDevices();
		assertTrue(devices != null && devices.size()==5);
		for(Device device:devices)
		{
			String identifier = device.getIdentifier();
			Identity owner = device.getIdentity();
			
			assertTrue(identifier.contains("imei:"));
			assertNotNull(owner);
			assertEquals("admin", owner.getPrincipal());
		}
		
		//Assert identity from device side
		for(int i=0; i<5; i++)
		{
			Device device = this.deviceController.read("imei:"+i);
			Identity owner = device.getIdentity();
			String identifier = device.getIdentifier();
			
			assertTrue(identifier.contains("imei:"));
			assertNotNull(owner);
			assertEquals("admin", owner.getPrincipal());
		}
	}	
}
