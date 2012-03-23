/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.console.server.device;

import java.io.Serializable;
import java.util.List;

import org.openmobster.core.console.server.admin.AccountAttribute;

/**
 * 
 * @author openmobster@gmail.com
 */
public final class DeviceUI implements Serializable
{
	private boolean isActive;
	private String account;
	private String os;
	private String deviceIdentifier;
	
	public DeviceUI()
	{
		
	}

	public boolean isActive() 
	{
		return isActive;
	}

	public void setActive(boolean isActive) 
	{
		this.isActive = isActive;
	}

	public String getAccount() 
	{
		return account;
	}

	public void setAccount(String account) 
	{
		this.account = account;
	}

	public String getOs() 
	{
		return os;
	}

	public void setOs(String os) 
	{
		this.os = os;
	}

	public String getDeviceIdentifier() 
	{
		return deviceIdentifier;
	}

	public void setDeviceIdentifier(String deviceIdentifier) 
	{
		this.deviceIdentifier = deviceIdentifier;
	}
	//---------xml rendition--------------------------------------------------------------------------------------
	public String toXml()
	{
		StringBuilder xmlBuffer = new StringBuilder();
		
		xmlBuffer.append("<device>\n");
		
		xmlBuffer.append("<isActive>"+this.isActive+"</isActive>\n");
		
		xmlBuffer.append("<account>"+this.account+"</account>\n");
		
		xmlBuffer.append("<os>"+this.os+"</os>\n");
		
		xmlBuffer.append("<device-identifier>"+this.deviceIdentifier+"</device-identifier>\n");
		
		xmlBuffer.append("</device>\n");
		
		return xmlBuffer.toString();
	}
	
	public static String toXml(List<DeviceUI> devices)
	{
		StringBuilder xmlBuffer = new StringBuilder();
		
		xmlBuffer.append("<devices>\n");
		
		if(devices != null && !devices.isEmpty())
		{
			for(DeviceUI local:devices)
			{
				xmlBuffer.append("<device>\n");
			
				xmlBuffer.append("<isActive>"+local.isActive+"</isActive>\n");
			
				xmlBuffer.append("<account>"+local.account+"</account>\n");
			
				xmlBuffer.append("<os>"+local.os+"</os>\n");
			
				xmlBuffer.append("<device-identifier>"+local.deviceIdentifier+"</device-identifier>\n");
			
				xmlBuffer.append("</device>\n");
			}
		}
		
		xmlBuffer.append("</devices>\n");
		
		return xmlBuffer.toString();
	}
}
