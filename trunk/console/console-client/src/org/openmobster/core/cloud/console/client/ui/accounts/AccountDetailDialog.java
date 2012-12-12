/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.cloud.console.client.ui.accounts;

import org.openmobster.core.cloud.console.client.common.Constants;
import org.openmobster.core.cloud.console.client.common.Payload;
import org.openmobster.core.cloud.console.client.flow.FlowServiceRegistry;
import org.openmobster.core.cloud.console.client.flow.TransitionService;
import org.openmobster.core.cloud.console.client.rpc.AccountService;
import org.openmobster.core.cloud.console.client.rpc.AccountServiceAsync;
import org.openmobster.core.cloud.console.client.rpc.DeviceService;
import org.openmobster.core.cloud.console.client.rpc.DeviceServiceAsync;
import org.openmobster.core.cloud.console.client.ui.Screen;
import org.openmobster.core.cloud.console.client.ui.TabController;
import org.openmobster.core.cloud.console.client.ui.user.AdminDetailDialog;
import org.openmobster.core.cloud.console.client.ui.user.AdminScreenLoader;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.data.Record;


/**
 *
 * @author openmobster@gmail.com
 */
public class AccountDetailDialog implements Screen
{
	private Record record;
	
	public AccountDetailDialog(Record record)
	{
		this.record = record;
	}
	
	public String title()
	{
		return "accountDetailDialog";
	}
	
	public Canvas render()
	{
		Window winModal = new Window();
		
		String icon = "openmobster/"+this.record.getAttribute("active")+".png";
		
		winModal.setWidth(525);
        winModal.setHeight(150);
        winModal.setTitle("Account");
        winModal.setShowMinimizeButton(false);
        winModal.setIsModal(true);
        winModal.setShowModalMask(true);
        winModal.centerInPage();
        winModal.setShowCloseButton(true);
        winModal.setHeaderIcon(icon);
        
        VLayout formLayout = new VLayout();
        
        //Create the Form
        DynamicForm form = new DynamicForm();
        form.setHeight100();
        form.setWidth100();
        form.setPadding(5);
        form.setLayoutAlign(VerticalAlignment.BOTTOM);
        
        StaticTextItem account = new StaticTextItem();
        account.setTitle("Account");
        account.setValue(record.getAttribute("account"));
        
        StaticTextItem deviceId = new StaticTextItem();
        deviceId.setTitle("Device Id");
        deviceId.setValue(record.getAttribute("deviceId"));
        
        StaticTextItem os = new StaticTextItem();
        os.setTitle("OS");
        os.setValue(record.getAttribute("os"));
        
        form.setFields(account, deviceId, os);
        formLayout.addChild(form);
        
        HLayout toolbar = new HLayout();
        toolbar.setAlign(Alignment.CENTER);
        
        boolean isActive = this.record.getAttributeAsBoolean("is-active");
        Button toggleActivate = new Button();
        if(isActive)
        {
	        toggleActivate.setTitle("De-activate");
	        toggleActivate.addClickHandler(new DeActivateClickHandler());
        }
        else
        {
        	toggleActivate.setTitle("Activate");
	        toggleActivate.addClickHandler(new ActivateClickHandler());
        }
        
        //Re-assign button
        Button reassign = new Button();
        reassign.setTitle("Re-Assign");
        reassign.addClickHandler(new ReAssignClickHandler());
        
        Button close = new Button();
        close.setTitle("Close");
        close.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event) 
            {
                FlowServiceRegistry.getTransitionService().closeActiveWindow();
            }
        });
        
        //Reset Password button
        Button resetPassword = new Button();
        resetPassword.setTitle("Reset Password");
        resetPassword.addClickHandler(new ResetPasswordClickHandler());
        
        toolbar.addMember(toggleActivate);
        toolbar.addMember(reassign);
        toolbar.addMember(resetPassword);
        
        String platform = record.getAttribute("os");
        boolean activateDeviceManagement = platform.toLowerCase().contains("android");
        
        if(activateDeviceManagement)
        {
        	final String accountName = record.getAttribute("account");
	        Button deviceManagement = new Button();
	        deviceManagement.setTitle("Manage Device");
	        deviceManagement.addClickHandler(new ClickHandler() {
	
	            @Override
	            public void onClick(ClickEvent event) 
	            {
	                TabController.getInstance().openTab("/device/"+accountName,true,
	                new ManageDeviceScreen("Device: "+accountName,accountName));
	                FlowServiceRegistry.getTransitionService().closeActiveWindow();
	            }
	        });
	        toolbar.addMember(deviceManagement);
        }
        
        toolbar.addMember(close);
        
        
        winModal.addItem(formLayout);
        winModal.addItem(toolbar);
        
		return winModal;
	}
	
	private class DeActivateClickHandler implements ClickHandler
	{
		@Override
		public void onClick(ClickEvent event) 
		{
			SC.showPrompt("Deactivating....");
			
			String deviceId = AccountDetailDialog.this.record.getAttribute("deviceId");
			final DeviceServiceAsync service = GWT.create(DeviceService.class);
			String payload = Payload.encode(new String[]{"deactivate", deviceId});
			
			service.invoke(payload, 
			new AsyncCallback<String>(){
				@Override
				public void onFailure(Throwable t) 
				{
					SC.clearPrompt();
					SC.say("System Error", "Unexpected Network Error. Please try again.",null);
				}

				@Override
				public void onSuccess(String result) 
				{
					SC.clearPrompt();
					
					if(result.trim().equals("500"))
					{
						//validation error
						SC.say("System Error", "Internal Server Error. Please try again.",null);
					}
					else
					{
						SC.say("De-Activate", "The account is successfully de-activated", new BooleanCallback() {
			                @Override
			                public void execute(Boolean value) 
			                {
			                	FlowServiceRegistry.getTransitionService().closeActiveWindow();
			                	TabController.getInstance().closeTab(Constants.devices);
			                	DeviceScreenLoader.load();
			                }
			            });
					}
				}
			});
		}
	}
	
	private class ActivateClickHandler implements ClickHandler
	{
		@Override
		public void onClick(ClickEvent event) 
		{
			SC.showPrompt("Activating....");
			
			String deviceId = AccountDetailDialog.this.record.getAttribute("deviceId");
			final DeviceServiceAsync service = GWT.create(DeviceService.class);
			String payload = Payload.encode(new String[]{"activate", deviceId});
			
			service.invoke(payload, 
			new AsyncCallback<String>(){
				@Override
				public void onFailure(Throwable t) 
				{
					SC.clearPrompt();
					SC.say("System Error", "Unexpected Network Error. Please try again.",null);
				}

				@Override
				public void onSuccess(String result) 
				{
					SC.clearPrompt();
					
					if(result.trim().equals("500"))
					{
						//validation error
						SC.say("System Error", "Internal Server Error. Please try again.",null);
					}
					else
					{
						SC.say("Activate", "The account is successfully activated", new BooleanCallback() {
			                @Override
			                public void execute(Boolean value) 
			                {
			                	FlowServiceRegistry.getTransitionService().closeActiveWindow();
			                	TabController.getInstance().closeTab(Constants.devices);
			                	DeviceScreenLoader.load();
			                }
			            });
					}
				}
			});
		}
	}
	
	private class ReAssignClickHandler implements ClickHandler
	{
		@Override
		public void onClick(ClickEvent event) 
		{
			SC.ask("Are you sure you want to re-assign this device to another user", 
            new BooleanCallback(){
				@Override
				public void execute(Boolean value) 
				{	
					if(value)
					{
						//Do the reassign
						SC.showPrompt("Reassigning....");
						
						String deviceId = AccountDetailDialog.this.record.getAttribute("deviceId");
						final DeviceServiceAsync service = GWT.create(DeviceService.class);
						String payload = Payload.encode(new String[]{"reassign", deviceId});
						
						service.invoke(payload, new ReAssignAsyncCallback());
					}
					else
					{
						FlowServiceRegistry.getTransitionService().closeActiveWindow();
					}
				}
            });
		}
	}
	
	private class ReAssignAsyncCallback implements AsyncCallback<String>
	{
		@Override
		public void onFailure(Throwable t) 
		{
			SC.clearPrompt();
			SC.say("System Error", "Unexpected Network Error. Please try again.",null);
		}

		@Override
		public void onSuccess(String result) 
		{	
			SC.clearPrompt();
			
			if(result.trim().equals("500"))
			{
				//validation error
				SC.say("System Error", "Internal Server Error. Please try again.",null);
			}
			else
			{
				SC.say("Reassign", "The account is reassigned. Login from the CloudManager App to activate it", new BooleanCallback() {
	                @Override
	                public void execute(Boolean value) 
	                {
	                	FlowServiceRegistry.getTransitionService().closeActiveWindow();
	                	TabController.getInstance().closeTab(Constants.devices);
	                	DeviceScreenLoader.load();
	                }
	            });
			}
		}
	}
	
	private class ResetPasswordClickHandler implements ClickHandler
	{
		@Override
		public void onClick(ClickEvent event) 
		{
			TransitionService transitionService = FlowServiceRegistry.getTransitionService();
            transitionService.transitionActiveWindow(new ResetPasswordDialog(record));
		}
	}
}
