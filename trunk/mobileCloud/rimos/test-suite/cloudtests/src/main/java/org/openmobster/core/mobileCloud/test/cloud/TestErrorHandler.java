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
package org.openmobster.core.mobileCloud.test.cloud;

import test.openmobster.core.mobileCloud.rimos.testsuite.Test;

import org.openmobster.core.mobileCloud.rimos.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.rimos.errors.SystemException;

/**
 * @author openmobster@gmail.com
 *
 */
public final class TestErrorHandler extends Test 
{
	public void runTest()
	{		
		try
		{
			String s = null;
			s.toString();
		}
		catch(Exception e)
		{
			ErrorHandler.getInstance().handle(e);
		}
		
		try
		{
			throw new SystemException(this.getClass().getName(), "runTest", new Object[]{
				"Configuration Save Exception"
			});
		}
		catch(Exception e)
		{
			ErrorHandler.getInstance().handle(e);
		}
		
		String report = ErrorHandler.getInstance().generateReport();
		System.out.println(report);
		assertTrue(report!=null && report.trim().length()>0, this.getClass().getName()+"://ReportMustNotBeEmpty");
		
		ErrorHandler.getInstance().clearAll();
		report = ErrorHandler.getInstance().generateReport();
		assertTrue(report==null || report.trim().length()==0, this.getClass().getName()+"://ReportMustBeEmpty");
	}
}
