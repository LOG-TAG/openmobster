/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.cloud.console.client.ui.apn;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
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
public class PushAppScreen implements Screen
{
	public String title()
	{
		return "Push Setup";
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
		ListGrid rows = new ListGrid();
		rows.setWidth(500);
		rows.setHeight(224);
		rows.setShowAllRecords(true);
		
		//Activation Status
		ListGridField activeField = new ListGridField("activeIcon","Active",50);
		activeField.setAlign(Alignment.CENTER);
		activeField.setType(ListGridFieldType.IMAGE);
		activeField.setImageURLPrefix("openmobster/");
		activeField.setImageURLSuffix(".png");
		
		//App Id
		ListGridField appIdField = new ListGridField("appId","App Id");
		appIdField.setAlign(Alignment.CENTER);
		
		
		rows.setFields(activeField,appIdField);
		rows.setData(PushAppRecord.load());
		
		//Event handling
		rows.addRecordClickHandler(new RecordClickHandler()
		{
            @Override
            public void onRecordClick(RecordClickEvent event) 
            {
            	PushAppRecord record = (PushAppRecord)event.getRecord();
                
                TransitionService transitionService = FlowServiceRegistry.getTransitionService();
                
                if(!record.isActive())
                {
                	transitionService.transitionActiveWindow(new UploadCertificateDialog((PushAppRecord)record));
                }
                else
                {
                	transitionService.transitionActiveWindow(new DownloadCertificateDialog((PushAppRecord)record));
                }
            }
		});
		
		canvas.addChild(rows);
		
		return canvas;
	}
}
