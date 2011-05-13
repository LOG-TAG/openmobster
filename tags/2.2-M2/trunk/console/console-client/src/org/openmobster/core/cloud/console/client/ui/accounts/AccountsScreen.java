/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.cloud.console.client.ui.accounts;

import java.util.List;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.grid.events.RecordClickHandler;
import com.smartgwt.client.widgets.grid.events.RecordClickEvent;

import org.openmobster.core.cloud.console.client.common.Constants;
import org.openmobster.core.cloud.console.client.flow.FlowServiceRegistry;
import org.openmobster.core.cloud.console.client.flow.TransitionService;
import org.openmobster.core.cloud.console.client.model.Device;
import org.openmobster.core.cloud.console.client.state.ContextRegistry;
import org.openmobster.core.cloud.console.client.ui.Screen;

/**
 *
 * @author openmobster@gmail.com
 */
public class AccountsScreen implements Screen
{
	public String title()
	{
		return "Devices";
	}
	
	public Canvas render()
	{
	    VLayout canvas = new VLayout();
		
		//Setup the Canvas
	    canvas.setMargin(3);
        canvas.setMembersMargin(10);
        canvas.setShowEdges(true);
		canvas.setAutoWidth();
		canvas.setAutoHeight();
		canvas.setLayoutAlign(Alignment.CENTER);
		
		//Setup the ListGrid
		ListGrid accounts = new ListGrid();
		accounts.setWidth(500);
		accounts.setHeight(224);
		accounts.setShowAllRecords(true);
		
		ListGridField onlineField = new ListGridField("active","Active",50);
		onlineField.setAlign(Alignment.CENTER);
		onlineField.setType(ListGridFieldType.IMAGE);
		onlineField.setImageURLPrefix("openmobster/");
		onlineField.setImageURLSuffix(".png");
		
		ListGridField accountField = new ListGridField("account","Account");
		ListGridField osField = new ListGridField("os","OS");
		
		accounts.setFields(onlineField,accountField, osField);
		accounts.setData(DeviceAccount.load());
		
		//Event handling
		accounts.addRecordClickHandler(new RecordClickHandler()
		{
            @Override
            public void onRecordClick(RecordClickEvent event) 
            {
                Record record = event.getRecord();
                
                TransitionService transitionService = FlowServiceRegistry.getTransitionService();
                transitionService.transitionActiveWindow(new AccountDetailDialog(record));
            }
		});
		
		canvas.addMember(accounts);
		
		/*HLayout toolbar = new HLayout();
        toolbar.setAlign(Alignment.CENTER);
        
        Button previous = new Button();
        previous.setTitle("Previous");
        //previous.addClickHandler(null);
        
        Button next = new Button();
        next.setTitle("Next");
        //next.addClickHandler(new StartCreateAccountHandler());
        
        toolbar.addMember(previous);
        toolbar.addMember(next);
    
        canvas.addMember(toolbar);*/
		
		return canvas;
	}
	
	//Mock Data...replace with cloud integration
	private static class DeviceAccount extends ListGridRecord
	{
	    public DeviceAccount(String active,String account, String os, String deviceId,boolean isActive)
	    {
	        this.setActive(active);
	        this.setAccount(account);
	        this.setOs(os);
	        this.setDeviceId(deviceId);
	        this.setIsActive(isActive);
	    }
	    
	    public String getAccount()
	    {
	        return this.getAttributeAsString("account");
	    }
	    
	    public void setAccount(String account)
	    {
	        this.setAttribute("account", account);
	    }
	    
	    public String getOs()
	    {
	        return this.getAttributeAsString("os");
	    }
	    
	    public void setOs(String os)
	    {
	        this.setAttribute("os", os);
	    }
	    
	    public String getActive()
	    {
	        return this.getAttributeAsString("active");
	    }
	    
	    public void setActive(String active)
	    {
	        this.setAttribute("active", active);
	    }
	    
	    public String getDeviceId()
	    {
	        return this.getAttributeAsString("deviceId");
	    }
	    
	    public void setDeviceId(String deviceId)
	    {
	        this.setAttribute("deviceId", deviceId);
	    }
	    
	   
	    public void setIsActive(boolean isActive)
	    {
	        this.setAttribute("is-active", isActive);
	    }
	    
	    public static DeviceAccount[] load()
	    {
	    	List<Device> devices = (List<Device>)ContextRegistry.getAppContext().getAttribute(Constants.devices);
	    	
	    	if(devices != null && !devices.isEmpty())
	    	{
	    		int length = devices.size();
	    		DeviceAccount[] uiAcct = new DeviceAccount[length];
	    		for(int i=0; i<length; i++)
	    		{
	    			Device local = devices.get(i);
	    			String account = local.getAccount();
	    			String os = local.getOs();
	    			String status = "green";
	    			String deviceId = local.getDeviceIdentifier();
	    			if(!local.isActive())
	    			{
	    				status = "red";
	    			}
	    			
	    			uiAcct[i] = new DeviceAccount(status,account,os,deviceId,local.isActive());
	    		}
	    		ContextRegistry.getAppContext().removeAttribute(Constants.devices);
	    		return uiAcct;
	    	}
	    	
	    	return null;
	    }
	}
}
