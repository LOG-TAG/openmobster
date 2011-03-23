/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.module.bus;

import java.util.Map;
import java.util.HashMap;

import org.openmobster.core.mobileCloud.android.util.GeneralTools;

/**
 * @author openmobster@gmail.com
 *
 */
public class Invocation 
{	
	private String target;
	private Map<String,Object> input;
	private Object handshake;
	private String destinationBus;
	
	public Invocation(String target)
	{
		this.target = target;
	}	
		
	public void setValue(String name, String value)
	{
		if(name == null)
		{
			throw new IllegalArgumentException("Name cannot be Null");
		}
		if(value == null)
		{
			value = "";
		}
		
		this.getInput().put(name, value);
	}
	
	public String getValue(String name)
	{
		String value = (String)this.getInput().get(name);
		
		if(value != null && value.trim().length() == 0)
		{
			value = null;
		}
		
		return value;
	}
	
	public String getTarget() 
	{
		return this.target;
	}
	
	public void startHandshake() throws Exception
	{
		this.handshake = GeneralTools.generateUniqueId();
		synchronized(this.handshake)
		{
			this.handshake.wait();
		}
	}
	
	public void stopHandshake() throws Exception
	{
		synchronized(this.handshake)
		{
			this.handshake.notifyAll();
			this.handshake = null;
		}
	}
	
	public boolean isHandshakeActivated()
	{
		return (this.handshake != null);
	}
	//----------------------------------------------------------------------------------------------------------------------------------------
	public Map<String,Object> getShared()
	{
		Map<String,Object> shared = this.getInput();
		input.put("target", this.target);
		return shared;
	}
	
	public static Invocation createFromShared(Map<String,Object> shared)
	{
		String target = (String)shared.get("target");
		
		if(target == null || target.trim().length()==0)
		{
			throw new IllegalArgumentException("Invocation Target is missing!!!");
		}
		
		Invocation invocation = new Invocation(target);
		
		shared.remove("target");
		invocation.input = shared;
		
		return invocation;
	}
	//----------------------------------------------------------------------------------------------------------------------------------------
	private Map<String,Object> getInput() 
	{
		if(this.input == null)
		{
			this.input = new HashMap<String,Object>();
		}
		return this.input;
	}	
	//-------------------------------------------------------------------------------------------------------------------
	public String calculateDestinationBus()
	{
		if(this.destinationBus == null)
		{
			return Bus.getInstance().findBus(this);
		}
		else
		{
			return this.destinationBus;
		}
	}
	
	public void setDestinationBus(String busId)
	{
		this.destinationBus = busId;
	}
}
