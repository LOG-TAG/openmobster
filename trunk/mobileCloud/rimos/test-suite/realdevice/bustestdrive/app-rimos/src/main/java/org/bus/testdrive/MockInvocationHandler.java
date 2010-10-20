/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.bus.testdrive;

import org.bus.testdrive.module.bus.Invocation;
import org.bus.testdrive.module.bus.InvocationHandler;
import org.bus.testdrive.module.bus.InvocationResponse;


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
		response.setValue(InvocationResponse.returnValue, this.getUri()+"://"+this.data);				
		return response;
	}	
}
