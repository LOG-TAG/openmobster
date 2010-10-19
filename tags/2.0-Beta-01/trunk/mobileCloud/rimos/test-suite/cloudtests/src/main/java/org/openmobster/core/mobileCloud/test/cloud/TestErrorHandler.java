/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

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
