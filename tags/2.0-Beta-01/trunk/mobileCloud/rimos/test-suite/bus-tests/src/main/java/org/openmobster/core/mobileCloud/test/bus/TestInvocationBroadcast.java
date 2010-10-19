/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.test.bus;

import test.openmobster.core.mobileCloud.rimos.testsuite.Test;

import org.openmobster.core.mobileCloud.rimos.module.bus.Bus;
import org.openmobster.core.mobileCloud.rimos.module.bus.Invocation;
import org.openmobster.core.mobileCloud.rimos.module.bus.InvocationHandler;
import org.openmobster.core.mobileCloud.rimos.util.GeneralTools;

/**
 * @author openmobster@gmail.com
 *
 */
public final class TestInvocationBroadcast extends Test 
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
						
			InvocationHandler handler = new MultiInvocationHandler("MultiInvocationHandler/"+
			GeneralTools.generateUniqueId(), "bus://MultiInvocationHandler");
			bus.register(handler);
						
			Invocation invocation = new Invocation("MultiInvocationHandler");
			invocation.setValue("input", "test://Invocation");
						
			bus.broadcast(invocation);			
		}
		catch(Exception e)
		{
			throw new RuntimeException(e.getMessage());
		}
	}	
}
