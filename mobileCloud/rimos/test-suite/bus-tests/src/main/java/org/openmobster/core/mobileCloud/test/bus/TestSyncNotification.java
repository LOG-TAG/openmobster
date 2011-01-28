/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.test.bus;

import test.openmobster.core.mobileCloud.rimos.testsuite.Test;

import org.openmobster.core.mobileCloud.rimos.module.bus.Bus;
import org.openmobster.core.mobileCloud.rimos.module.bus.SyncInvocation;
import org.openmobster.core.mobileCloud.api.model.MobileBean;

/**
 * @author openmobster@gmail.com
 *
 */
public final class TestSyncNotification extends Test
{
	public void runTest()
	{		
		try
		{
			MobileBean[] beans = MobileBean.readAll("testServerBean");
			this.assertNull(beans, this.getInfo()+"/testServerBean/mustBeNull");
			
			SyncInvocation syncInvocation = new SyncInvocation("org.openmobster.core.mobileCloud.invocation.SyncInvocationHandler", 
			SyncInvocation.slow, "testServerBean");		
			Bus.getInstance().invokeService(syncInvocation);
			
			beans = MobileBean.readAll("testServerBean");
			this.assertNotNull(beans, this.getInfo()+"/testServerBean/mustNotBeNull");
			this.assertEquals(String.valueOf(beans.length), "2", this.getInfo()+"/testServerBean/mustHave2Beans");
		}
		catch(Exception e)
		{
			throw new RuntimeException(e.toString());
		}
	}
}
