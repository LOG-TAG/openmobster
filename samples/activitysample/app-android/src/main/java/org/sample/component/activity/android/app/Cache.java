/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sample.component.activity.android.app;

import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;

/**
 *
 * @author openmobster@gmail.com
 */
public class Cache implements Serializable 
{
	private Map<String,Object> cache;
	
	public Cache()
	{
		this.cache = new HashMap<String,Object>();
	}
	
	public Object get(String key)
	{
		return this.cache.get(key);
	}
	
	public void put(String key,Object value)
	{
		this.cache.put(key, value);
	}
}
