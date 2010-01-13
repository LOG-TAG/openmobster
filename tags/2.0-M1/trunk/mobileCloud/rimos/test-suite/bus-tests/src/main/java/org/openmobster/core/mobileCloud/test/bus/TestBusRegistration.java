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
package org.openmobster.core.mobileCloud.test.bus;

import test.openmobster.core.mobileCloud.rimos.testsuite.Test;

import org.openmobster.core.mobileCloud.rimos.module.bus.Bus;
import org.openmobster.core.mobileCloud.rimos.module.bus.BusRegistration;
import org.openmobster.core.mobileCloud.rimos.module.bus.InvocationHandler;
import org.openmobster.core.mobileCloud.rimos.storage.Database;
import org.openmobster.core.mobileCloud.rimos.storage.Record;

/**
 * @author openmobster@gmail.com
 *
 */
public final class TestBusRegistration extends Test 
{	
	public String getInfo() 
	{	
		return this.getClass().getName();
	}
	
	public void runTest() 
	{
		try
		{
			this.testStart();
			
			this.testAddInvocationHandler();
		}
		catch(Exception e)
		{
			throw new RuntimeException(e.getMessage());
		}
	}
	
	private void testStart() throws Exception
	{
		Bus bus = Bus.getInstance();
		
		System.out.println("-----------------------------------------------------");
		System.out.println("Bus Id ="+bus.getBusId());
		System.out.println("-----------------------------------------------------");
		
		this.assertTrue(bus.getBusId() != 0, this.getInfo()+"/Bus/RegistrationSuccess");
		
		BusRegistration myRegistration = this.readRegistration(bus.getBusId());
		this.assertNotNull(myRegistration, this.getInfo()+"/testStart/RegistrationShouldNotBeNull");
		
		long currId = myRegistration.getBusId();
		this.assertTrue(bus.getBusId() == currId, this.getInfo()+"/testStart/BusIdsMustMatch");
	}
	
	private void testAddInvocationHandler() throws Exception
	{
		Bus bus = Bus.getInstance();
		
		InvocationHandler handler = new MockInvocationHandler("MockInvocationHandler", "Output/Hello_World");
		bus.register(handler);
		
		BusRegistration myRegistration = this.readRegistration(bus.getBusId());
		Record record = myRegistration.getRecord();
		
		String handlerCriteria = record.getValue("handler[0]");
		this.assertEquals(handlerCriteria, handler.getUri(), this.getInfo()+"/testAddInvocationHandler/AddSuccess");
	}
	
	private void testStop() throws Exception
	{
		Bus bus = Bus.getInstance();
		long busId = bus.getBusId();
		bus.stop();
		
		BusRegistration myRegistration = this.readRegistration(busId);
		this.assertNull(myRegistration, this.getInfo()+"/testStop/RegistrationShouldBeUnregistered");
	}
	//-------------------------------------------------------------------------------------------------------------------------------------------
	private BusRegistration readRegistration(long busId) throws Exception
	{
		BusRegistration registration = null;
		
		Record record = Database.getInstance().select(Database.bus_registration, String.valueOf(busId));
		if(record != null)
		{
			registration = new BusRegistration(record);
		}
		
		return registration;
	}
}
