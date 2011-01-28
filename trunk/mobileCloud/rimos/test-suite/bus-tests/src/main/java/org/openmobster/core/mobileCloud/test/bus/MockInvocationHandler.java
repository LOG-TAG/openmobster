/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.test.bus;

import org.openmobster.core.mobileCloud.rimos.module.bus.Invocation;
import org.openmobster.core.mobileCloud.rimos.module.bus.InvocationHandler;
import org.openmobster.core.mobileCloud.rimos.module.bus.InvocationResponse;

/**
 * @author openmobster@gmail.com
 *
 */
public final class MockInvocationHandler implements InvocationHandler
{
	private String uri = null;
	private String data = null;
	
	public MockInvocationHandler(String uri, String data)
	{
		this.uri = uri;
		this.data = data;
	}
	
	public String getUri()
	{
		return this.uri;
	}
	
	public InvocationResponse handleInvocation(Invocation invocation)
	{
		InvocationResponse response = new InvocationResponse();
		System.out.println("Successfully Invoked on ---------------------------------------"+this.getUri());
		System.out.println("Input="+invocation.getValue("input"));
		System.out.println("-----------------------------------------------------------");
		response.setValue(InvocationResponse.returnValue, this.getUri()+"://"+this.data);
		return response;
	}	
}
