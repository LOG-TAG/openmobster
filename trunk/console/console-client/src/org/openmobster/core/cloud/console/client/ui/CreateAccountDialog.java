/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.cloud.console.client.ui;

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

import org.openmobster.core.cloud.console.client.common.Payload;
import org.openmobster.core.cloud.console.client.flow.FlowServiceRegistry;
import org.openmobster.core.cloud.console.client.flow.TransitionService;
import org.openmobster.core.cloud.console.client.rpc.AccountService;
import org.openmobster.core.cloud.console.client.rpc.AccountServiceAsync;

/**
 *
 * @author openmobster@gmail.com
 */
public class CreateAccountDialog implements Screen
{
	private TextItem firstName;
	private TextItem lastName;
	private TextItem email;
	private PasswordItem password;
	
	public String title()
	{
		return "createAccountDialog";
	}
	
	public Canvas render()
	{
		Window winModal = new Window();
		
		winModal.setWidth(360);
        winModal.setHeight(200);
        winModal.setTitle("Create Account");
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
        
        this.firstName = new TextItem();
        this.firstName.setTitle("First Name");
        
        this.lastName = new TextItem();
        this.lastName.setTitle("Last Name");
        
        this.email = new TextItem();
        this.email.setTitle("Email");
        
        this.password = new PasswordItem();
        this.password.setTitle("Password");
        
        form.setFields(this.firstName, this.lastName, this.email, this.password);
        formLayout.addChild(form);
        
        HLayout toolbar = new HLayout();
        toolbar.setAlign(Alignment.CENTER);
        
        Button createAccount = new Button();
        createAccount.setTitle("Create Account");
        createAccount.addClickHandler(new CreateAccountHandler());
        
        Button cancel = new Button();
        cancel.setTitle("Cancel");
        cancel.addClickHandler(new CancelHandler());
        
        toolbar.addMember(createAccount);
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
	        FlowServiceRegistry.getTransitionService().transitionActiveWindow(new AuthenticationDialog());
	    }
	}
	
	private class CreateAccountHandler implements ClickHandler
	{
		private final AccountServiceAsync accountService = GWT.create(AccountService.class);
		
	    @Override
        public void onClick(ClickEvent event) 
	    {
	    	Object firstNameVal = CreateAccountDialog.this.firstName.getValue();
	    	Object lastNameVal = CreateAccountDialog.this.lastName.getValue();
	    	Object emailVal = CreateAccountDialog.this.email.getValue();
	    	Object passwordVal = CreateAccountDialog.this.password.getValue();
	    	
	    	//Client side validation
	    	if(emailVal == null || 
			   passwordVal == null || 
			   firstNameVal == null || 
			   lastNameVal == null)
	    	{
				SC.say("Create Account", "All the fields are \"required\" fields.",null);
				return;
	    	}
	    	
	    	String email = emailVal.toString();
	    	String password = passwordVal.toString();
	    	String firstName = firstNameVal.toString();
	    	String lastName = lastNameVal.toString();
	    	
	    	//Client side validation
	    	if(email == null || email.trim().length() == 0 || 
			   password == null || password.trim().length() == 0 || 
			   firstName == null || firstName.trim().length()==0 || 
			   lastName == null || lastName.trim().length() == 0)
	    	{
				SC.say("Create Account", "All the fields are \"required\" fields.",null);
				return;
	    	}
	    	
	    	String payload = Payload.encode(new String[]{"createAccount", email, password,firstName,lastName});
	    	accountService.invoke(payload,
					new AsyncCallback<String>() 
					{
						public void onFailure(Throwable caught) 
						{
							SC.say("Create Account", "Unexpected Network Error. Please try again.",null);
						}

						public void onSuccess(String result) 
						{
							if(result.trim().equals("200"))
							{
								//success
								final TransitionService transitionService = FlowServiceRegistry.getTransitionService();
						        transitionService.closeActiveWindow();
						        SC.say("Create Account", "Your account is successfully created", new BooleanCallback() {
					                @Override
					                public void execute(Boolean value) 
					                {
					                    transitionService.transitionActiveWindow(new AuthenticationDialog());
					                }
						        });
							}
							if(result.trim().equals("201"))
							{
								//success
								final TransitionService transitionService = FlowServiceRegistry.getTransitionService();
						        transitionService.closeActiveWindow();
						        SC.say("Create Account", "Your account is successfully created. You will have to wait till it is activated by another administrator", new BooleanCallback() {
					                @Override
					                public void execute(Boolean value) 
					                {
					                    transitionService.transitionActiveWindow(new AuthenticationDialog());
					                }
						        });
							}
							else if(result.trim().equals("1"))
							{
								//validation error
								SC.say("Create Account", "Data Validation Error. Please try again", null);
							}
							else if(result.trim().equals("2"))
							{
								//validation error
								SC.say("Create Account", "Account already exists. Please try again with a different \"Email\"", null);
							}
							else if(result.trim().equals("5"))
							{
								//validation error
								SC.say("Create Account", "Invalid Email Address. Please try again.", null);
							}
							else if(result.trim().equals("500"))
							{
								//validation error
								SC.say("Create Account", "Internal Server Error. Please try again.",null);
							}
						}
					});
	    }
	}
}
