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

import org.openmobster.core.mobileCloud.api.service.Request;
import org.openmobster.core.mobileCloud.api.service.Response;
import org.openmobster.core.mobileCloud.api.service.MobileService;

import test.openmobster.core.mobileCloud.rimos.testsuite.AbstractAPITest;


/**
 * @author openmobster@gmail.com
 *
 */
public final class TestMobileServiceInvocation extends AbstractAPITest 
{
	public void runTest()
	{		
		try
		{
			Request request = new Request("mockMobileService");
			request.setAttribute("test1", "mockValueTest1");
			request.setAttribute("test2", "mockValueTest2");
			
			Response response = MobileService.invoke(request);
			
			System.out.println("---------------------------------------------");
			String test1 = response.getAttribute("test1");
			String test2 = response.getAttribute("test2");
			System.out.println("test1="+test1);
			System.out.println("test2="+test2);
			
			this.assertEquals(test1, "response://mockValueTest1", this.getInfo()+"/test1MustMatch");
			this.assertEquals(test2, "response://mockValueTest2", this.getInfo()+"/test2MustMatch");
		}
		catch(Exception e)
		{
			throw new RuntimeException(e.toString());
		}
	}
}
