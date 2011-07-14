/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.cloud.console.client.ui.apn;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Encoding;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;  
import com.smartgwt.client.util.SC;
import com.smartgwt.client.util.BooleanCallback;

import org.openmobster.core.cloud.console.client.ui.Screen;
import org.openmobster.core.cloud.console.client.ui.TabController;
import org.openmobster.core.cloud.console.client.common.Constants;
import org.openmobster.core.cloud.console.client.flow.FlowServiceRegistry;

import com.smartgwt.client.widgets.form.fields.UploadItem; 
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler; 
import com.google.gwt.user.client.ui.NamedFrame; 

/**
 *
 * @author openmobster@gmail.com
 */
public class UploadCertificateDialog implements Screen
{
	private StaticTextItem appIdText;
	private UploadItem fileItem;
	private DynamicForm form;
	private PasswordItem password;
	
	private PushAppRecord record;
	
	public UploadCertificateDialog(PushAppRecord record)
	{
		this.record = record;
	}
	
	public String title()
	{
		return "uploadCertificateDialog";
	}
	
	public Canvas render()
	{
		this.initComplete(this);
		
		Window winModal = new Window();
		
		winModal.setWidth(360);
        winModal.setHeight(200);
        winModal.setTitle("Upload Push Certificate");
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
        form.setTarget(this.title());
        form.setEncoding(Encoding.MULTIPART);
        form.setAction(GWT.getModuleBaseURL() + "apn");
        
        this.appIdText = new StaticTextItem("appId");
        this.appIdText.setTitle("App Id");
        this.appIdText.setValue(record.getAppId());
        
        this.fileItem = new UploadItem("certificate");
        this.fileItem.setTitle("File");
        this.fileItem.setWidth(300);
        
        this.password = new PasswordItem("password");
        this.password.setTitle("Password");
        
        
        Button uploadButton = new Button("Upload");
        uploadButton.addClickHandler(new ClickHandler(){
        public void onClick(ClickEvent e) {
        	Object obj = fileItem.getDisplayValue();
        	Object passwordVal = password.getValue();
        	if (obj != null && passwordVal != null) 
        	{
        		SC.showPrompt("Upload Certificate", "Upload in Progress");
        		form.submitForm();
        	} 
        	else
        	{
        		SC.say("All fields are required");
        	}
           }
        });
        
        Button close = new Button();
        close.setTitle("Close");
        close.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event) 
            {
            	FlowServiceRegistry.getTransitionService().closeActiveWindow();
            	TabController.getInstance().closeTab(Constants.push_app);
            	PushAppLoader.load(Constants.push_app);
            }
        });
          
        form.setFields(this.appIdText,fileItem,password);
        formLayout.addChild(form);
        
        //Button bar
        HLayout toolbar = new HLayout();
        toolbar.setAlign(Alignment.CENTER);
        toolbar.addMember(uploadButton);
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
	
	private native void initComplete(UploadCertificateDialog upload) /*-{
	//$wnd.alert(upload);
	$wnd.uploadComplete = function (fileName) {
	upload.@org.openmobster.core.cloud.console.client.ui.apn.UploadCertificateDialog::uploadComplete(Ljava/lang/String;)(fileName);
	};
	}-*/;
	
	public void uploadComplete(String fileName) 
	{
		SC.clearPrompt();
		SC.say("Certificate is stored successfully", new BooleanCallback(){
			@Override
			public void execute(Boolean value) 
			{	
				FlowServiceRegistry.getTransitionService().closeActiveWindow();
		    	TabController.getInstance().closeTab(Constants.push_app);
		    	PushAppLoader.load(Constants.push_app);
			}
		});
		
		this.fileItem.setValue("");
		this.password.setValue("");
	} 
}
