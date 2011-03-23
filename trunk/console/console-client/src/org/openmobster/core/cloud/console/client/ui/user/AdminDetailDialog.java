/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.cloud.console.client.ui.user;

import java.util.List;

import org.openmobster.core.cloud.console.client.common.Constants;
import org.openmobster.core.cloud.console.client.common.Payload;
import org.openmobster.core.cloud.console.client.flow.FlowServiceRegistry;
import org.openmobster.core.cloud.console.client.flow.TransitionService;
import org.openmobster.core.cloud.console.client.model.AdminAccount;
import org.openmobster.core.cloud.console.client.rpc.AccountService;
import org.openmobster.core.cloud.console.client.rpc.AccountServiceAsync;
import org.openmobster.core.cloud.console.client.state.ContextRegistry;
import org.openmobster.core.cloud.console.client.ui.Screen;
import org.openmobster.core.cloud.console.client.ui.TabController;

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
public class AdminDetailDialog implements Screen
{
	private Record record;
	
	public AdminDetailDialog(Record record)
	{
		this.record = record;
	}
	
	public String title()
	{
		return "adminDetailDialog";
	}
	
	public Canvas render()
	{
		Window winModal = new Window();
		
		String icon = "openmobster/"+this.record.getAttribute("active")+".png";
		
		winModal.setWidth(400);
        winModal.setHeight(150);
        winModal.setTitle("Admin Account");
        winModal.setShowMinimizeButton(false);
        winModal.setIsModal(true);
        winModal.setShowModalMask(true);
        winModal.centerInPage();
        winModal.setShowCloseButton(true);
        winModal.setHeaderIcon(icon);
        
        VLayout formLayout = new VLayout();
        
        String usernameVal = this.record.getAttribute("account");
        String nameVal = this.record.getAttribute("name");
        
        //Create the Form
        DynamicForm form = new DynamicForm();
        form.setHeight100();
        form.setWidth100();
        form.setPadding(5);
        form.setLayoutAlign(VerticalAlignment.BOTTOM);
        
        StaticTextItem account = new StaticTextItem();
        account.setTitle("Username");
        account.setValue(usernameVal);
        
        StaticTextItem name = new StaticTextItem();
        name.setTitle("Name");
        name.setValue(nameVal);
        
        
        form.setFields(account, name);
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
        
        toolbar.addMember(toggleActivate);
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
			
			String username = AdminDetailDialog.this.record.getAttribute("account");
			final AccountServiceAsync accountService = GWT.create(AccountService.class);
			String payload = Payload.encode(new String[]{"deactivate", username});
			accountService.invoke(payload,
					new AsyncCallback<String>() 
					{
						public void onFailure(Throwable caught) 
						{
							SC.clearPrompt();
							SC.say("System Error", "Unexpected Network Error. Please try again.",null);
						}

						public void onSuccess(String result)
						{
							SC.clearPrompt();
							
							if(result.trim().equals("500"))
							{
								//validation error
								SC.say("System Error", "Internal Server Error. Please try again.",null);
							}
							if(result.trim().equals("4"))
							{
								//validation error
								SC.say("De-Activate", "There must be atleast one active account",null);
							}
							else
							{
								SC.say("De-Activate", "The account is successfully de-activated", new BooleanCallback() {
					                @Override
					                public void execute(Boolean value) 
					                {
					                	FlowServiceRegistry.getTransitionService().closeActiveWindow();
					                	TabController.getInstance().closeTab(Constants.admin_accounts);
					                	AdminScreenLoader.load(Constants.admin_accounts);
					                }
					            });
							}
						}
					}
			);
		}
	}
	
	private class ActivateClickHandler implements ClickHandler
	{
		@Override
		public void onClick(ClickEvent event) 
		{
			SC.showPrompt("Activating....");
			
			String username = AdminDetailDialog.this.record.getAttribute("account");
			final AccountServiceAsync accountService = GWT.create(AccountService.class);
			String payload = Payload.encode(new String[]{"activate", username});
			accountService.invoke(payload,
					new AsyncCallback<String>() 
					{
						public void onFailure(Throwable caught) 
						{
							SC.clearPrompt();
							SC.say("System Error", "Unexpected Network Error. Please try again.",null);
						}

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
					                	TabController.getInstance().closeTab(Constants.admin_accounts);
					                	AdminScreenLoader.load(Constants.admin_accounts);
					                }
					            });
							}
						}
					}
			);
		}
	}
}
