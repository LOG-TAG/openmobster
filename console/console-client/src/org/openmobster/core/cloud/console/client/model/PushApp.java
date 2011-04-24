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
public final class PushApp implements Serializable
{
	private String appId;
	private boolean isActive;
	
	
	public PushApp()
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

	public String getAppId() 
	{
		return appId;
	}

	public void setAppId(String appId) 
	{
		this.appId = appId;
	}
	//---------xml rendition--------------------------------------------------------------------------------------
	public static PushApp toObject(String xml)
	{
		Document document = XMLParser.parse(xml);
		
		Element appNode = (Element)document.getElementsByTagName("push-app").item(0);
		
		return PushApp.toPushApp(appNode);
	}
	
	public static List<PushApp> toList(String xml)
	{
		List<PushApp> apps = new ArrayList<PushApp>();
		
		Document document = XMLParser.parse(xml);
		
		NodeList nodes = document.getElementsByTagName("push-app");
		if(nodes != null && nodes.getLength()>0)
		{
			int length = nodes.getLength();
			for(int i=0; i<length; i++)
			{
				Element appNode = (Element)nodes.item(i);
				PushApp local = PushApp.toPushApp(appNode);
				apps.add(local);
			}
		}
		
		return apps;
	}
	
	public static PushApp toPushApp(Element node)
	{
		PushApp app = new PushApp();
		
		Element isActiveElem = (Element)node.getElementsByTagName("is-active").item(0);
		String value = isActiveElem.getFirstChild().getNodeValue();
		boolean isActive = Boolean.parseBoolean(value);
		app.setActive(isActive);
		
		Element idElem = (Element)node.getElementsByTagName("app-id").item(0);
		value = idElem.getFirstChild().getNodeValue();
		app.setAppId(value);
	
		
		return app;
	}
}
