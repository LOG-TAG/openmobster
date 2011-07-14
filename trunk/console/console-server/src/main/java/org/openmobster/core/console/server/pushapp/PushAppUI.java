/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.console.server.pushapp;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author openmobster@gmail.com
 */
public final class PushAppUI implements Serializable 
{
	private String appId;
	private boolean isActive;
	
	public PushAppUI()
	{
		
	}

	public String getAppId() 
	{
		return appId;
	}

	public void setAppId(String appId) 
	{
		this.appId = appId;
	}
	
	public boolean isActive() 
	{
		return isActive;
	}

	public void setActive(boolean isActive) 
	{
		this.isActive = isActive;
	}
	//------xml rendition-----------------------------------------------------------------
	public String toXml()
	{
		StringBuilder buffer = new StringBuilder();
		
		buffer.append("<push-app>\n");
		buffer.append("<app-id>"+this.appId+"</app-id>\n");
		buffer.append("<is-active>"+this.isActive+"</is-active>\n");
		buffer.append("</push-app>");
		
		return buffer.toString();
	}
	
	public static String toXml(List<PushAppUI> pushApps)
	{
		StringBuilder buffer = new StringBuilder();
		
		buffer.append("<push-apps>\n");
		
		if(pushApps != null && !pushApps.isEmpty())
		{
			for(PushAppUI local:pushApps)
			{
				buffer.append("<push-app>\n");
				buffer.append("<app-id>"+local.appId+"</app-id>\n");
				buffer.append("<is-active>"+local.isActive+"</is-active>\n");
				buffer.append("</push-app>\n");
			}
		}
		
		buffer.append("</push-apps>\n");
		
		return buffer.toString();
	}
}
