/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.cloud.console.client.ui.apn;

import java.util.List;

import com.smartgwt.client.widgets.grid.ListGridRecord;

import org.openmobster.core.cloud.console.client.model.PushApp;
import org.openmobster.core.cloud.console.client.common.Constants;
import org.openmobster.core.cloud.console.client.state.ContextRegistry;

/**
 * 
 * @author openmobster@gmail.com
 */
public class PushAppRecord extends ListGridRecord
{
    public PushAppRecord(String activeIcon,boolean isActive,String appId)
    {
        this.setAppId(appId);
        this.setActiveIcon(activeIcon);
        this.setActive(isActive);
    }
    
    public String getAppId()
    {
        return this.getAttributeAsString("appId");
    }
    
    public void setAppId(String appId)
    {
        this.setAttribute("appId", appId);
    }
    
    public String getActiveIcon()
    {
        return this.getAttributeAsString("activeIcon");
    }
    
    public void setActiveIcon(String activeIcon)
    {
        this.setAttribute("activeIcon", activeIcon);
    }
    
    public void setActive(boolean isActive)
    {
    	this.setAttribute("isActive",isActive);
    }
    
    public boolean isActive()
    {
    	return this.getAttributeAsBoolean("isActive");
    }
    
    public static PushAppRecord[] load()
    {
    	List<PushApp> apps = (List<PushApp>)ContextRegistry.getAppContext().getAttribute(Constants.push_app);
    	
    	if(apps != null && apps.size()>0)
    	{
    		int length = apps.size();
    		PushAppRecord[] uiApps = new PushAppRecord[length];
    		for(int i=0; i<length; i++)
    		{
    			PushApp local = apps.get(i);
    			String appId = local.getAppId();
    			boolean isActive = local.isActive();
    			String status = "green";
    			if(!local.isActive())
    			{
    				status = "red";
    			}
    			
    			uiApps[i] = new PushAppRecord(status,isActive,appId);
    		}
    		ContextRegistry.getAppContext().removeAttribute(Constants.push_app);
    		
    		return uiApps;
    	}
    	
    	return null;
    }
}
