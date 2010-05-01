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
public final class RemoteInvocationHandler implements InvocationHandler
{	
	public RemoteInvocationHandler()
	{		
	}
	
	public String getUri()
	{
		return this.getClass().getName();
	}
	
	public InvocationResponse handleInvocation(Invocation invocation)
	{
		InvocationResponse response = new InvocationResponse();						
		response.setValue(InvocationResponse.returnValue, this.getUri()+"://remoteInvocation");				
		return response;
	}	
}
