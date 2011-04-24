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
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Encoding;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;  

import org.openmobster.core.cloud.console.client.ui.Screen;
import org.openmobster.core.cloud.console.client.flow.FlowServiceRegistry;

import com.google.gwt.user.client.ui.NamedFrame; 
import com.google.gwt.http.client.URL;

/**
 *
 * @author openmobster@gmail.com
 */
public class DownloadCertificateDialog implements Screen
{
	private StaticTextItem appIdText;
	private DynamicForm form;
	private PushAppRecord record;
	
	public DownloadCertificateDialog(PushAppRecord record)
	{
		this.record = record;
	}
	
	public String title()
	{
		return "downloadCertificateDialog";
	}
	
	public Canvas render()
	{
		Window winModal = new Window();
		
		winModal.setWidth(360);
        winModal.setHeight(200);
        winModal.setTitle("Download Push Certificate");
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
        
        this.appIdText = new StaticTextItem();
        this.appIdText.setTitle("App Id");
        this.appIdText.setValue(record.getAppId());
        
        
        Button downloadButton = new Button("Download");
        downloadButton.addClickHandler(new ClickHandler(){
        	public void onClick(ClickEvent e) 
        	{
        		String appId = URL.encode(record.getAppId());
        		com.google.gwt.user.client.Window.open(GWT.getModuleBaseURL() + "apn/certificate/download?appid="+appId,
        		"_blank",null);
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
            }
        });
          
        form.setFields(this.appIdText);
        formLayout.addChild(form);
        
        //Button bar
        HLayout toolbar = new HLayout();
        toolbar.setAlign(Alignment.CENTER);
        toolbar.addMember(downloadButton);
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
