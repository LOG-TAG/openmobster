/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.cloud.console.client.ui.accounts;

import org.openmobster.core.cloud.console.client.ui.NavigationController;
import org.openmobster.core.cloud.console.client.ui.Screen;
import org.openmobster.core.cloud.console.client.ui.TabController;

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tile.TileGrid;
import com.smartgwt.client.widgets.tile.events.RecordClickEvent;
import com.smartgwt.client.widgets.tile.events.RecordClickHandler;
import com.smartgwt.client.widgets.tree.TreeNode;
import com.smartgwt.client.widgets.viewer.DetailFormatter;
import com.smartgwt.client.widgets.viewer.DetailViewerField;
import com.smartgwt.client.data.Record;



/**
 *
 * @author openmobster@gmail.com
 */
public class ManageDeviceScreen implements Screen
{
    private String title;
    
    public ManageDeviceScreen(String title)
    {
        this.title = title;
    }
    
	public String title()
	{
		return this.title;
	}
	
	public Canvas render()
	{
		VLayout canvas = new VLayout();
		
		canvas.setWidth100();
		
		TileGrid tileGrid = new TileGrid();
		tileGrid.setShowEdges(true);
        tileGrid.setTileWidth(140);
        tileGrid.setTileHeight(120);
        tileGrid.setWidth100();
        tileGrid.setHeight100();
        tileGrid.setShowAllRecords(true);
        tileGrid.setAutoFetchData(false);
        tileGrid.setAnimateTileChange(true);
        
        DetailViewerField nameField = new DetailViewerField("name");
        nameField.setDetailFormatter(new DetailFormatter() {
            public String format(Object value, Record record, DetailViewerField field) {
                return value.toString();
            }
        });
        nameField.setCellStyle("thumbnailTitle");
        
        DetailViewerField iconField = new DetailViewerField("thumbnail");
        iconField.setType("image");
        iconField.setImageHeight(89);
        iconField.setImageWidth(119);
        iconField.setCellStyle("thumbnail");

        tileGrid.setFields(iconField, nameField);
        
        tileGrid.setData(manageDevices);
        
        tileGrid.addRecordClickHandler(new HomeScreenHandler());
        
        canvas.addMember(tileGrid);
		
		return canvas;
	}
	
	private static TreeNode[] manageDevices = new TreeNode[]{
		NavigationController.createNavigationNode("Remote Lock", "remoteLock", "remote_lock", "silk/layout_content.png", "thumbnails/featured_complete_app.gif", true),
		NavigationController.createNavigationNode("Remote Wipe", "remoteWipe", "remote_wipe", "silk/layout_content.png", "thumbnails/featured_complete_app.gif", true)
	};
	
	private static class HomeScreenHandler implements RecordClickHandler
	{
	    @Override
	    public void onRecordClick(RecordClickEvent event) 
	    {   
	        Record record = event.getRecord();
	    }
	}
}
