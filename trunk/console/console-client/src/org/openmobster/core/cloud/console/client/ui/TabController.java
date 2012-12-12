/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.cloud.console.client.ui;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

/**
 *
 * @author openmobster@gmail.com
 */
public final class TabController 
{
	private static TabController singleton;
	
	private Canvas canvas;
	private TabSet mainTabSet;
	
	private TabController()
	{
		
	}
	
	public static TabController getInstance()
	{
		if(TabController.singleton == null)
		{
			TabController.singleton = new TabController();
		}
		return TabController.singleton;
	}
	
	public void start()
	{
		this.mainTabSet = new TabSet();
        Layout paneContainerProperties = new Layout();
        paneContainerProperties.setLayoutMargin(0);
        paneContainerProperties.setLayoutTopMargin(1);
        mainTabSet.setPaneContainerProperties(paneContainerProperties);
        mainTabSet.setWidth100();
        mainTabSet.setHeight100();
        
        this.canvas = new Canvas();
        canvas.setWidth100();
        canvas.setHeight100();
        canvas.addChild(mainTabSet);
	}
	
	public void stop()
	{
		
	}
	
	public Canvas openTab(String tabId, boolean canBeClosed,Screen screen)
	{
		if(this.mainTabSet == null)
		{
			throw new IllegalStateException("TabController is not running");
		}
		
		//Find a tab with this id...if found, just select it
		Tab[] tabs = this.mainTabSet.getTabs();
		if(tabs != null)
		{
			for(Tab tab:tabs)
			{
				if(tab.getID().equals(tabId))
				{
					this.mainTabSet.selectTab(tabId);
					return this.canvas;
				}
			}
		}
		
		Tab tab = new Tab();
		tab.setID(tabId);
        tab.setTitle(screen.title());

        HLayout mainPanel = new HLayout();
        mainPanel.setHeight100();
        mainPanel.setWidth100();
        mainPanel.setAlign(Alignment.CENTER);
        
        mainPanel.addMember(screen.render());
        
        tab.setPane(mainPanel);
        tab.setCanClose(canBeClosed);
        
        this.mainTabSet.addTab(tab);
        this.mainTabSet.selectTab(tab);
        
        return this.canvas;
	}
	
	public void closeTab(String tabId)
	{
		//Find a tab with this id...if found, just select it
		Tab[] tabs = this.mainTabSet.getTabs();
		if(tabs != null)
		{
			for(Tab tab:tabs)
			{
				if(tab.getID().equals(tabId))
				{
					this.mainTabSet.removeTab(tabId);
				}
			}
		}
	}
}
