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
package org.openmobster.core.mobileCloud.test.api;

import org.openmobster.core.mobileCloud.api.model.MobileBean;
import test.openmobster.core.mobileCloud.rimos.testsuite.AbstractAPITest;

/**
 * @author openmobster@gmail.com
 *
 */
public final class TestBeanUpdateOptimisticLocking extends AbstractAPITest 
{
	public void runTest()
	{		
		try
		{
			this.startBootSync();
			this.waitForBeans();
			
			MobileBean instance1 = MobileBean.readById(this.service, "unique-1");
			MobileBean instance2 = MobileBean.readById(this.service, "unique-1");
			
			String newValueInstance1 = "/instance1/from/Updated";
			instance1.setValue("from", newValueInstance1);
			instance1.save();
			
			boolean exceptionOccured= false;
			String newValueInstance2 = "/instance2/from/Updated";
			instance2.setValue("from", newValueInstance2);
			try
			{
				instance2.save(); //This should throw an exception
			}
			catch(RuntimeException e)
			{				
				exceptionOccured = true;
			}
			instance2.refresh();
			
			assertEquals(instance2.getValue("from"), "/instance1/from/Updated", this.getInfo()+"://Instance2_not_integral");
			assertTrue(exceptionOccured, this.getInfo()+"://LockingException_Should_Occur");			
		}
		catch(Exception e)
		{
			throw new RuntimeException(e.toString());
		}
	}	
}
