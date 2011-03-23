/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.cloud.console.client.ui;

import org.openmobster.core.cloud.console.client.common.Constants;
import org.openmobster.core.cloud.console.client.flow.FlowServiceRegistry;
import org.openmobster.core.cloud.console.client.flow.TransitionService;
import org.openmobster.core.cloud.console.client.state.ContextRegistry;
import org.openmobster.core.cloud.console.client.ui.user.ApproveAdminScreen;

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

/**
 *
 * @author openmobster@gmail.com
 */
public class MainScreen implements Screen
{
	public String title()
	{
		return "main";
	}
	
	public Canvas render()
	{
		VLayout main = new VLayout();
		
		//Generate the TopBar
		ToolStrip topBar = new ToolStrip();
        topBar.setHeight(33);
        topBar.setWidth100();
        topBar.addSpacer(6);
        
        //TODO: Add when an official Logo is ready
        /*ImgButton sgwtHomeButton = new ImgButton();
        sgwtHomeButton.setSrc("pieces/24/cube_green.png");
        sgwtHomeButton.setWidth(24);
        sgwtHomeButton.setHeight(24);
        sgwtHomeButton.setPrompt("Smart GWT Project Page");
        sgwtHomeButton.setHoverStyle("interactImageHover");
        sgwtHomeButton.setShowRollOver(false);
        sgwtHomeButton.setShowDownIcon(false);
        sgwtHomeButton.setShowDown(false);
        sgwtHomeButton.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
            public void onClick(ClickEvent event) {
                com.google.gwt.user.client.Window.open("http://code.google.com/p/smartgwt/", "sgwt", null);
            }
        });
        topBar.addMember(sgwtHomeButton);
        topBar.addSpacer(6);*/
        
        //Add a title
        Label title = new Label("OpenMobster Cloud Console");
        title.setStyleName("sgwtTitle");
        title.setWidth(300);
        topBar.addMember(title);
        topBar.addFill();
        
        //Add a Logout button
        ToolStripButton logoutButton = new ToolStripButton();
        logoutButton.setTitle("Logout");
        logoutButton.addClickHandler(new LogoutHandler());
        topBar.addButton(logoutButton);
        
        //Add component to the main window
        main.addMember(topBar);
        
        //Master Layout container
        HLayout hLayout = new HLayout();
        hLayout.setLayoutMargin(5);
        hLayout.setWidth100();
        hLayout.setHeight100();
        
        //Add SideNavigation to the layout
        NavigationController.getInstance().start(); //reset the side navigation
        hLayout.addMember(NavigationController.getInstance().render());
        
        //Start the HomeScreen in the central Tabbed region
        TabController.getInstance().start(); //reset the Tab Controller
        hLayout.addMember(TabController.getInstance().openTab("home",false,new HomeScreen()));
        
        //Get from the Cloud, if Admins are needed to be approved
        if(ContextRegistry.getAppContext().getAttribute(Constants.admin_accounts)!= null)
        {
        	TabController.getInstance().openTab(Constants.admin_accounts,true,new ApproveAdminScreen());
        }
        
        //Add to the main window
        main.addMember(hLayout);
        
        //set the dimensions
        main.setWidth100();
        main.setHeight100();
        
        //set the style...not sure how this will work
        main.setStyleName("tabSetContainer");
        
        return main;
	}
	
	private static class LogoutHandler implements ClickHandler 
	{
	    @Override
	    public void onClick(ClickEvent event) 
	    {
	        TransitionService transitionService = FlowServiceRegistry.getTransitionService();
	        
	        //Display Authentication Screen
	        transitionService.transitionHost(new AuthenticateScreen());
	        
	        //Show the AuthenticationDialog
	        transitionService.transitionActiveWindow(new AuthenticationDialog());
	    }
	}
}
