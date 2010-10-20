/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.test.sync;

import org.openmobster.core.mobileCloud.rimos.module.bus.Invocation;
import org.openmobster.core.mobileCloud.rimos.module.bus.InvocationHandler;
import org.openmobster.core.mobileCloud.rimos.module.bus.InvocationResponse;

/**
 * @author openmobster@gmail.com
 *
 */
public final class TestStorageListener implements InvocationHandler
{	
	private String uri;
	
	public TestStorageListener(String uri)
	{
		this.uri = uri;
	}
	
	public String getUri()
	{
		return this.uri;
	}
	
	public InvocationResponse handleInvocation(Invocation invocation)
	{
		InvocationResponse response = new InvocationResponse();
		
		String mappedRecordId = invocation.getValue("recordId");		
		if(!mappedRecordId.endsWith("-luid"))
		{
			mappedRecordId += "-luid";
			response.setValue(InvocationResponse.returnValue, mappedRecordId);
		}
		
		return response;
	}	
}
