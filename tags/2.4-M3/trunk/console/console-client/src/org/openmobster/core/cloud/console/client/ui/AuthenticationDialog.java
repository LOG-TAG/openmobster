/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.cloud.console.client.ui;

import java.util.List;

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.Button;

import org.openmobster.core.cloud.console.client.rpc.AccountService;
import org.openmobster.core.cloud.console.client.rpc.AccountServiceAsync;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import org.openmobster.core.cloud.console.client.common.Constants;
import org.openmobster.core.cloud.console.client.common.Payload;
import org.openmobster.core.cloud.console.client.flow.FlowServiceRegistry;
import org.openmobster.core.cloud.console.client.flow.TransitionService;
import org.openmobster.core.cloud.console.client.model.AdminAccount;
import org.openmobster.core.cloud.console.client.state.AppContext;
import org.openmobster.core.cloud.console.client.state.ContextRegistry;
import org.openmobster.core.cloud.console.client.ui.apn.UploadCertificateDialog;

/**
 *
 * @author openmobster@gmail.com
 */
public class AuthenticationDialog implements Screen
{
	private TextItem email;
	private PasswordItem password;
	
	public String title()
	{
		return "authenticationDialog";
	}
	
	public Canvas render()
	{
		Window winModal = new Window();
		
		winModal.setWidth(360);
        winModal.setHeight(115);
        winModal.setTitle("Console Login");
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
        
        this.email = new TextItem();
        this.email.setTitle("Email");
        
        this.password = new PasswordItem();
        this.password.setTitle("Password");
        
        form.setFields(email, password);
        formLayout.addChild(form);
        
        HLayout toolbar = new HLayout();
        toolbar.setAlign(Alignment.CENTER);
        
        Button login = new Button();
        login.setTitle("Login");
        login.addClickHandler(new AuthenticationHandler());
        
        Button createAccount = new Button();
        createAccount.setTitle("Create Account");
        createAccount.addClickHandler(new StartCreateAccountHandler());
        
        toolbar.addMember(login);
        toolbar.addMember(createAccount);
        
        
        winModal.addItem(formLayout);
        winModal.addItem(toolbar);
        
		return winModal;
	}
	
	private class AuthenticationHandler implements ClickHandler 
	{
		private final AccountServiceAsync accountService = GWT.create(AccountService.class);
		
	    @Override
	    public void onClick(ClickEvent event) 
	    {
	    	//FIXME: only for hosted development
	    	Object emailVal = AuthenticationDialog.this.email.getValue();
	    	Object passwordVal = AuthenticationDialog.this.password.getValue();
	    	
	    	String email = (emailVal != null)?emailVal.toString():"null";
	    	String password = (passwordVal != null)?passwordVal.toString():"null";
	    	
	    	SC.showPrompt("Login in Progress....");
	    	
	    	String payload = Payload.encode(new String[]{"login",email, password});
	    	accountService.invoke(payload,
					new AsyncCallback<String>() 
					{
						public void onFailure(Throwable caught) 
						{
							SC.clearPrompt();
							SC.say("Login", "Unexpected Network Error. Please try again.",null);
						}

						public void onSuccess(String result) 
						{
							SC.clearPrompt();
							if(result.trim().equalsIgnoreCase("200"))
							{
								TransitionService transitionService = FlowServiceRegistry.getTransitionService();
								transitionService.closeActiveWindow();
		        	        
								MainScreen mainScreen = new MainScreen();
								transitionService.transitionHost(mainScreen);
							}
							else if(result.trim().equalsIgnoreCase("0"))
							{
								SC.say("Login", "Login Failed. Please check your credentials",null);
							}
							else if(result.trim().equalsIgnoreCase("3"))
							{
								SC.say("Login", "Login Failed. This account is not activated yet",null);
							}
							else if(result.trim().equals("500"))
							{
								//validation error
								SC.say("Login", "Internal Server Error. Please try again.",null);
							}
							else
							{
								//A list of all accounts sent back to show some need activation
								List<AdminAccount> accounts = AdminAccount.toList(result);
								ContextRegistry.getAppContext().setAttribute(Constants.admin_accounts, accounts);
								
								TransitionService transitionService = FlowServiceRegistry.getTransitionService();
								transitionService.closeActiveWindow();
		        	        
								MainScreen mainScreen = new MainScreen();
								transitionService.transitionHost(mainScreen);
							}
						}
			});
	    	//SC.say("Login", "Internal Server Error. Please try again.Debug",null);
	    	
	    	/*TransitionService transitionService = FlowServiceRegistry.getTransitionService();
			transitionService.closeActiveWindow();
        
			MainScreen mainScreen = new MainScreen();
			transitionService.transitionHost(mainScreen);*/
	    }
	}
	
	private static class StartCreateAccountHandler implements ClickHandler 
	{
	    @Override
	    public void onClick(ClickEvent event) 
	    {
	        FlowServiceRegistry.getTransitionService().transitionActiveWindow(new CreateAccountDialog());
	    }
	}
}
