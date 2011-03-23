/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.pushmail.cloud.channel;

import java.util.List;
import java.util.ArrayList;

/**
 * 
 * @author openmobster@gmail.com
 */
public class PushFilter
{
	private String deviceId;
	private List<String> oids;
	
	public PushFilter(String deviceId)
	{
		this.deviceId = deviceId;
		this.oids = new ArrayList<String>();
	}
	
	public void addToFilter(String oid)
	{
		oids.add(oid);
	}
	
	public boolean isOnDevice(String oid)
	{
		if(this.oids.contains(oid))
		{
			return true;
		}
		return false;
	}
}
