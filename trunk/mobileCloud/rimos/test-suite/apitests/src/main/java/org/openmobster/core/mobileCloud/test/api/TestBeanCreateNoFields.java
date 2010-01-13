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
public final class TestBeanCreateNoFields extends AbstractAPITest 
{
	public void runTest()
	{		
		try
		{
			MobileBean newBean = MobileBean.newInstance(this.service);
			
			assertTrue(newBean.isInitialized(), this.getInfo()+"://NewBean_should_be_initialized");
			assertTrue(newBean.isCreateOnDevice(), this.getInfo()+"://NewBean_should_be_created_ondevice");
			assertTrue(newBean.getId()==null, this.getInfo()+"://NewBean_id_should_be_null");
			assertTrue(newBean.getServerId()==null, this.getInfo()+"://NewBean_serverId_should_be_null");
			
			newBean.save();
			assertTrue(newBean.isCreateOnDevice(), this.getInfo()+"://NewBean_should_be_created_ondevice");
			assertTrue(newBean.getId()!=null, this.getInfo()+"://NewBean_id_should_not_be_null");
			assertTrue(newBean.getServerId()==null, this.getInfo()+"://NewBean_serverId_should_be_null");
			
			System.out.println("New Ondevice RecordId="+newBean.getId());			
		}
		catch(Exception e)
		{
			throw new RuntimeException(e.toString());
		}
	}	
}
