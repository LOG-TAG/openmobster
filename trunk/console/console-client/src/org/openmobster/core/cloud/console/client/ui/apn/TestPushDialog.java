/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.cloud.console.client.ui.apn;

import java.util.List;
import java.util.LinkedHashMap;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Encoding;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;  

import org.openmobster.core.cloud.console.client.rpc.PushAppService;
import org.openmobster.core.cloud.console.client.rpc.PushAppServiceAsync;
import org.openmobster.core.cloud.console.client.state.ContextRegistry;
import org.openmobster.core.cloud.console.client.ui.Screen;
import org.openmobster.core.cloud.console.client.common.Constants;
import org.openmobster.core.cloud.console.client.common.Payload;
import org.openmobster.core.cloud.console.client.flow.FlowServiceRegistry;
import org.openmobster.core.cloud.console.client.flow.TransitionService;
import org.openmobster.core.cloud.console.client.model.Device;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.NamedFrame; 
import com.google.gwt.http.client.URL;

/**
 *
 * @author openmobster@gmail.com
 */
public class TestPushDialog implements Screen
{
	private StaticTextItem appIdText;
	private SelectItem devices;
	private DynamicForm form;
	
	private String appId;
	
	public TestPushDialog(String appId)
	{
		this.appId = appId;
	}
	
	public String title()
	{
		return "testPushDialog";
	}
	
	public Canvas render()
	{
		Window winModal = new Window();
		
		winModal.setWidth(360);
        winModal.setHeight(200);
        winModal.setTitle("Sending Test Push");
        winModal.setShowMinimizeButton(false);
        winModal.setIsModal(true);
        winModal.setShowModalMask(true);
        winModal.centerInPage();
        winModal.setShowCloseButton(false);
        
        VLayout formLayout = new VLayout();
        
        //Create the Form
        form = new DynamicForm();
        form.setHeight100();
        form.setWidth100();
        form.setPadding(5);
        form.setLayoutAlign(VerticalAlignment.BOTTOM);
        
        this.appIdText = new StaticTextItem();
        this.appIdText.setTitle("App Id");
        this.appIdText.setValue(this.appId);
        
        this.devices = new SelectItem();
        this.devices.setTitle("Send Push To");
        
        //Populate the select box
        List<Device> devices = (List<Device>)ContextRegistry.getAppContext().
        getAttribute(Constants.devices);
        LinkedHashMap<String,String> valueMap = new LinkedHashMap<String,String>();
        
        if(devices != null && !devices.isEmpty())
        {
        	for(Device local:devices)
        	{
        		String deviceId = local.getDeviceIdentifier();
        		String account = local.getAccount();
        		valueMap.put(deviceId, account);
        	}
        }
        this.devices.setValueMap(valueMap);
        
        
        Button send = new Button("Push");
        send.addClickHandler(new ClickHandler(){
        	public void onClick(ClickEvent e) 
        	{
        		String selectedDeviceId = TestPushDialog.this.devices.getValue().toString();

        		SC.showPrompt("Loading....");

        		final PushAppServiceAsync service = GWT.create(PushAppService.class);
        		String payload = Payload.encode(new String[]{"testPush",selectedDeviceId, TestPushDialog.this.appId});

        		service.invoke(payload,new AsyncCallback<String>(){
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
        					//Just confirmation
        					SC.say("Send Push", "Your Push was successfully sent",null);
        				}
        			}
        		});
        	}
        });
        
        Button close = new Button("Close");
        close.addClickHandler(new ClickHandler(){
        	public void onClick(ClickEvent e) 
        	{
        		FlowServiceRegistry.getTransitionService().closeActiveWindow();
        	}
        });
        
          
        form.setFields(this.appIdText,this.devices);
        formLayout.addChild(form);
        
        //Button bar
        HLayout toolbar = new HLayout();
        toolbar.setAlign(Alignment.CENTER);
        toolbar.addMember(send);
        toolbar.addMember(close);
        
        NamedFrame frame = new NamedFrame(this.title());
        frame.setWidth("1px");
        frame.setHeight("1px");
        frame.setVisible(false); 
        
        formLayout.addMember(frame);
        
        winModal.addItem(formLayout);
        winModal.addItem(toolbar);
        
		return winModal;
	}
}
