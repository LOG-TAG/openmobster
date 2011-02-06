/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.server.api;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;


/**
 * Provides Contextual information to a Channel about the Invocation such as
 * 
 * Synchronization Context which provides information about the device connecting etc
 * 
 * @author openmobster@gmail.com
 */
public class ExecutionContext 
{
	/**
	 * Name of the Synchronization Contextual Object
	 */
	private static final String SYNC_CONTEXT = "SYNC_CONTEXT";
	
	private Map<String, Object> attributes = null;
	
	private ExecutionContext()
	{
		this.attributes = new HashMap<String, Object>();
	}
	
	/**
	 * Provides Contextual Data corresponding to the Name
	 * 
	 * @param name
	 * @return Contextual Data
	 */
	public Object getAttribute(String name)
	{
		Object object = null;
		
		if(this.attributes.containsKey(name))
		{
			object = this.attributes.get(name);
		}
		
		return object;
	}
	
	/**
	 * Sets the Contextual Data corresponding to the Name
	 * 
	 * @param name Name of the Contextual Data
	 * @param value Contextual Data
	 */
	public void setAttribute(String name, Object value)
	{
		this.attributes.put(name, value);
	}
	
	/**
	 * Provides all the Names of the Contextual Objects stored within this instance of
	 * the ExecutionContext
	 * 
	 * @return Names
	 */
	public String[] getNames()
	{
		String[] names = null;
		
		if(!this.attributes.isEmpty())
		{
			Set<String> keys = this.attributes.keySet();
			names = keys.toArray(new String[0]);
		}
		
		return names;
	}
	
	/**
	 * Provides all the Contextual Objects stored within this instance of the ExecutionContext
	 * 
	 * @return All the Contextual Objects
	 */
	public Object[] getValues()
	{
		Object[] values = null;
		
		if(!this.attributes.isEmpty())
		{
			values = this.attributes.values().toArray();
		}
		
		return values;
	}
	//--------ThreadLocal aspect-----------------------------------------------------------------------------
	private static ThreadLocal threadLocal = null;	
	static
	{
		ExecutionContext.threadLocal = new ThreadLocal();
	}
	
	public static ExecutionContext getInstance()
	{
		ExecutionContext exeContext = null;
		
		exeContext = (ExecutionContext)threadLocal.get();
		if(exeContext == null)
		{
			threadLocal.set(new ExecutionContext());
			exeContext = (ExecutionContext)threadLocal.get();
		}
		
		return exeContext;
	}
	//---------------------------------------------------------------------------------------------------------
	public Object getSyncContext()
	{
		return this.getAttribute(SYNC_CONTEXT);
	}
	
	public void setSyncContext(Object syncContext)
	{
		this.setAttribute(SYNC_CONTEXT, syncContext);
	}
}
