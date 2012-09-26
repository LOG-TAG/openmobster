/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.cloud.console.client.ui.accounts;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;  
import com.smartgwt.client.util.SC;
import com.smartgwt.client.util.BooleanCallback;

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

import com.smartgwt.client.data.Record;

/**
 *
 * @author openmobster@gmail.com
 */
public class ResetPasswordDialog implements Screen
{
	private PasswordItem newPassword;
	
	private Record record;
	
	public ResetPasswordDialog(Record record)
	{
		this.record = record;
	}
	
	public String title()
	{
		return "resetPasswordDialog";
	}
	
	public Canvas render()
	{
		Window winModal = new Window();
		
		String account = this.record.getAttribute("account");
		
		winModal.setWidth(360);
        winModal.setHeight(200);
        winModal.setTitle("Reset Password: "+account);
        winModal.setShowMinimizeButton(false);
        winModal.setIsModal(true);
        winModal.setShowModalMask(true);
        winModal.centerInPage();
        winModal.setShowCloseButton(false);
        
        VLayout formLayout = new VLayout();
        
        //Create the Form
        DynamicForm form = new DynamicForm();
        form.setHeight100();
        form.setWidth100();
        form.setPadding(5);
        form.setLayoutAlign(VerticalAlignment.BOTTOM);
        
        
        this.newPassword = new PasswordItem();
        this.newPassword.setTitle("New Password");
        
        form.setFields(this.newPassword);
        formLayout.addChild(form);
        
        HLayout toolbar = new HLayout();
        toolbar.setAlign(Alignment.CENTER);
        
        Button resetPasswordButton = new Button();
        resetPasswordButton.setTitle("Reset Password");
        resetPasswordButton.addClickHandler(new ResetPasswordHandler());
        
        Button cancel = new Button();
        cancel.setTitle("Cancel");
        cancel.addClickHandler(new CancelHandler());
        
        toolbar.addMember(resetPasswordButton);
        toolbar.addMember(cancel);
        
        winModal.addItem(formLayout);
        winModal.addItem(toolbar);
        
		return winModal;
	}
	
	private class CancelHandler implements ClickHandler
	{
	    @Override
	    public void onClick(ClickEvent event) 
	    {
	        FlowServiceRegistry.getTransitionService().closeActiveWindow();
	    }
	}
	
	private class ResetPasswordHandler implements ClickHandler
	{
	    @Override
        public void onClick(ClickEvent event) 
	    {
	    	//client side validation
	    	Object password = newPassword.getValue();
	    	if(password == null)
	    	{
	    		SC.say("Reset Password", "New Password is required",null);
				return;
	    	}
	    	
	    	//Do the reassign
			SC.showPrompt("Resetting the Password.....");
			
			String deviceId = record.getAttribute("deviceId");
			final DeviceServiceAsync service = GWT.create(DeviceService.class);
			String payload = Payload.encode(new String[]{"resetpassword", deviceId, password.toString()});
			
			service.invoke(payload, new ResetPasswordAsyncCallback());
	    }
	}
	
	private class ResetPasswordAsyncCallback implements AsyncCallback<String>
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
				SC.say("Reset Password", "The password is successfully reset. The user will need to re-activate the device", new BooleanCallback() {
	                @Override
	                public void execute(Boolean value) 
	                {
	                	FlowServiceRegistry.getTransitionService().closeActiveWindow();
	                }
	            });
			}
		}
	}
}
