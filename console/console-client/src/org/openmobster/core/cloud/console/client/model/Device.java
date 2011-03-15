/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.cloud.console.client.model;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

import com.google.gwt.xml.client.XMLParser;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NodeList;

/**
 * 
 * @author openmobster@gmail.com
 */
public final class Device implements Serializable
{
	private boolean isActive;
	private String account;
	private String os;
	private String deviceIdentifier;
	
	public Device()
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
	public static Device toObject(String xml)
	{
		Document document = XMLParser.parse(xml);
		
		Element deviceNode = (Element)document.getElementsByTagName("device").item(0);
		
		return Device.toDevice(deviceNode);
	}
	
	public static List<Device> toList(String xml)
	{
		List<Device> devices = new ArrayList<Device>();
		
		Document document = XMLParser.parse(xml);
		
		NodeList deviceNodes = document.getElementsByTagName("device");
		if(deviceNodes != null && deviceNodes.getLength()>0)
		{
			int length = deviceNodes.getLength();
			for(int i=0; i<length; i++)
			{
				Element deviceNode = (Element)deviceNodes.item(i);
				Device local = Device.toDevice(deviceNode);
				devices.add(local);
			}
		}
		
		return devices;
	}
	
	public static Device toDevice(Element node)
	{
		Device device = new Device();
		
		Element isActiveElem = (Element)node.getElementsByTagName("isActive").item(0);
		String value = isActiveElem.getFirstChild().getNodeValue();
		boolean isActive = Boolean.parseBoolean(value);
		device.setActive(isActive);
		
		Element accountElem = (Element)node.getElementsByTagName("account").item(0);
		value = accountElem.getFirstChild().getNodeValue();
		device.setAccount(value);
		
		Element osElem = (Element)node.getElementsByTagName("os").item(0);
		value = osElem.getFirstChild().getNodeValue();
		device.setOs(value);
		
		Element deviceElem = (Element)node.getElementsByTagName("device-identifier").item(0);
		value = deviceElem.getFirstChild().getNodeValue();
		device.setDeviceIdentifier(value);
		
		return device;
	}
}
