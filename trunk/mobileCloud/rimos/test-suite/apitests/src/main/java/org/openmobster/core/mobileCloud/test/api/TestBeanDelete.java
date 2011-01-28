/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.test.api;

import org.openmobster.core.mobileCloud.api.model.MobileBean;
import test.openmobster.core.mobileCloud.rimos.testsuite.AbstractAPITest;

/**
 * @author openmobster@gmail.com
 *
 */
public final class TestBeanDelete extends AbstractAPITest 
{
	public void runTest()
	{		
		try
		{
			this.startBootSync();
			this.waitForBeans();
			
			MobileBean instance1 = MobileBean.readById(this.service, "unique-1");
			assertTrue(instance1.isInitialized(), this.getInfo()+"://Bean_must_be_initialized");
			
			instance1.delete();
			
			instance1 = MobileBean.readById(this.service, "unique-1");
			assertNull(instance1, this.getInfo()+"://Bean_must_not_be_found");
		}
		catch(Exception e)
		{
			throw new RuntimeException(e.toString());
		}
	}	
}
