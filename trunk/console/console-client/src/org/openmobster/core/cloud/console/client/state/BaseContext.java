/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.cloud.console.client.state;

import org.openmobster.core.cloud.console.client.common.AttributeManager;

/**
 *
 * @author openmobster@gmail.com
 */
public class BaseContext 
{
	private AttributeManager mgr;
	
	public BaseContext()
	{
		this.mgr = new AttributeManager();
	}
	
	public Object getAttribute(String name)
	{
		return this.mgr.getAttribute(name);
	}
	
	public void setAttribute(String name,Object value)
	{
		this.mgr.setAttribute(name, value);
	}
	
	public void removeAttribute(String name)
	{
		this.mgr.removeAttribute(name);
	}
}
