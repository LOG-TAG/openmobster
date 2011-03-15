/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.cloud.console.client.ui.user;

import java.util.List;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.grid.events.RecordClickHandler;
import com.smartgwt.client.widgets.grid.events.RecordClickEvent;

import org.openmobster.core.cloud.console.client.common.Constants;
import org.openmobster.core.cloud.console.client.flow.FlowServiceRegistry;
import org.openmobster.core.cloud.console.client.flow.TransitionService;
import org.openmobster.core.cloud.console.client.ui.Screen;
import org.openmobster.core.cloud.console.client.state.AppContext;
import org.openmobster.core.cloud.console.client.state.ContextRegistry;
import org.openmobster.core.cloud.console.client.model.AdminAccount;

/**
 *
 * @author openmobster@gmail.com
 */
public class ApproveAdminScreen implements Screen
{
	public String title()
	{
		return "Approve Admins";
	}
	
	public Canvas render()
	{
	    HLayout canvas = new HLayout();
		
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
		
		ListGridField activeField = new ListGridField("active","Active",50);
		activeField.setAlign(Alignment.CENTER);
		activeField.setType(ListGridFieldType.IMAGE);
		activeField.setImageURLPrefix("openmobster/");
		activeField.setImageURLSuffix(".png");
		
		ListGridField accountField = new ListGridField("account","Administrator");
		ListGridField nameField = new ListGridField("name","Name");
		
		accounts.setFields(activeField,accountField, nameField);
		accounts.setData(AdministratorAccount.load());
		
		//Event handling
		accounts.addRecordClickHandler(new RecordClickHandler()
		{
            @Override
            public void onRecordClick(RecordClickEvent event) 
            {
            	Record record = event.getRecord();
                
                TransitionService transitionService = FlowServiceRegistry.getTransitionService();
                transitionService.transitionActiveWindow(new AdminDetailDialog(record));
            }
		});
		
		canvas.addChild(accounts);
		
		return canvas;
	}
	
	//Mock Data...replace with cloud integration
	private static class AdministratorAccount extends ListGridRecord
	{
	    /*static MockAccount[] mockData = new MockAccount[] {
	        new MockAccount("green","blah@gmail.com","John Jay"),
	        new MockAccount("red","blah2@gmail.com","Jack Russell")
	    };*/
	    
	    public AdministratorAccount(String active,String account, String name,boolean isActive)
	    {
	        this.setActive(active);
	        this.setAccount(account);
	        this.setName(name);
	        this.setAttribute("is-active", isActive);
	    }
	    
	    public String getAccount()
	    {
	        return this.getAttributeAsString("account");
	    }
	    
	    public void setAccount(String account)
	    {
	        this.setAttribute("account", account);
	    }
	    
	    public String getActive()
	    {
	        return this.getAttributeAsString("active");
	    }
	    
	    public void setActive(String active)
	    {
	        this.setAttribute("active", active);
	    }
	    
	    public String getName()
	    {
	        return this.getAttributeAsString("name");
	    }
	    
	    public void setName(String name)
	    {
	        this.setAttribute("name", name);
	    }
	    
	    public static AdministratorAccount[] load()
	    {
	    	List<AdminAccount> accounts = (List<AdminAccount>)ContextRegistry.getAppContext().getAttribute(Constants.admin_accounts);
	    	
	    	if(accounts != null && accounts.size()>0)
	    	{
	    		int length = accounts.size();
	    		AdministratorAccount[] uiAcct = new AdministratorAccount[length];
	    		for(int i=0; i<length; i++)
	    		{
	    			AdminAccount local = accounts.get(i);
	    			String firstName = local.getFirstName();
	    			String lastName = local.getLastName();
	    			String username = local.getUsername();
	    			String status = "green";
	    			if(!local.isActive())
	    			{
	    				status = "red";
	    			}
	    			
	    			uiAcct[i] = new AdministratorAccount(status,username, firstName+" "+lastName,local.isActive());
	    		}
	    		ContextRegistry.getAppContext().removeAttribute(Constants.admin_accounts);
	    		return uiAcct;
	    	}
	    	
	    	return null;
	    }
	}
}
