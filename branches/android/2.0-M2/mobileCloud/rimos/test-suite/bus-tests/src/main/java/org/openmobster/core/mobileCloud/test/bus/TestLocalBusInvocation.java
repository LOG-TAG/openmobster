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
import org.openmobster.core.mobileCloud.rimos.module.bus.Invocation;
import org.openmobster.core.mobileCloud.rimos.module.bus.InvocationHandler;
import org.openmobster.core.mobileCloud.rimos.module.bus.InvocationResponse;

/**
 * @author openmobster@gmail.com
 *
 */
public final class TestLocalBusInvocation extends Test 
{	
	public String getInfo() 
	{	
		return this.getClass().getName();
	}
	
	public void runTest() 
	{
		try
		{
			Bus bus = Bus.getInstance();
			
			InvocationHandler handler = new MockInvocationHandler("MockInvocationHandler", "Output/Hello_World");
			bus.register(handler);
			
			Invocation invocation = new Invocation("MockInvocationHandler");
			invocation.setValue("input", "test://Invocation");
			
			InvocationResponse response = bus.invokeService(invocation);
			String returnValue = response.getValue(InvocationResponse.returnValue);
			
			System.out.println("-------------------------------------------");
			System.out.println("Response="+returnValue);
			System.out.println("-------------------------------------------");
			
			this.assertNotNull(returnValue, this.getInfo()+"/InvocationSuccessful");
		}
		catch(Exception e)
		{
			throw new RuntimeException(e.getMessage());
		}
	}	
}
