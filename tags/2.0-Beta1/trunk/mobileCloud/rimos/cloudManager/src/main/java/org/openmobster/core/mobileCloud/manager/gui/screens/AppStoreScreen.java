/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.manager.gui.screens;

import java.util.Vector;

import net.rim.blackberry.api.browser.Browser;
import net.rim.blackberry.api.browser.BrowserSession;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.KeyListener;
import net.rim.device.api.system.Characters;

import org.openmobster.core.mobileCloud.manager.gui.LocaleKeys;
import org.openmobster.core.mobileCloud.rimos.configuration.Configuration;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.SystemLocaleKeys;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.Screen;
import org.openmobster.core.mobileCloud.api.ui.framework.resources.AppResources;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.NavigationContext;



/**
 * @author openmobster@gmail.com
 */
public class AppStoreScreen extends Screen
{		
	private MainScreen screen;
	private ListField listField;
	private Vector appMetaData;
	
	public AppStoreScreen()
	{
		
	}
	//-------------------------------------------------------------------------------------------------------------------------------------------------
	public Object getContentPane() 
	{		
		return this.screen;
	}
	
	public void render()
	{								
		AppResources appResources = Services.getInstance().getResources();					
		this.screen = new MainScreen();
		this.screen.setTitle(appResources.localize(LocaleKeys.app_store, LocaleKeys.app_store));
		
		//Access the screen state
		NavigationContext navContext = NavigationContext.getInstance();
		Vector uris = (Vector)navContext.getAttribute(this.getId(), "uris");
		Vector names = (Vector)navContext.getAttribute(this.getId(), "names");
		Vector descs = (Vector)navContext.getAttribute(this.getId(), "descs");
		Vector downloadUrls = (Vector)navContext.getAttribute(this.getId(), "downloadUrls");
		
		if(uris != null && !uris.isEmpty())
		{
			this.appMetaData = new Vector();
			int size = uris.size();
			
			for(int i=0; i<size; i++)
			{
				AppMetaData cour = new AppMetaData();
				
				cour.uri = (String)uris.elementAt(i);
				cour.name = (String)names.elementAt(i);
				cour.description = (String)descs.elementAt(i);
				cour.downloadUrl = (String)downloadUrls.elementAt(i);
				
				this.appMetaData.addElement(cour);
			}
																			
			listField = new ListField(this.appMetaData.size());
			listField.setCallback(new ListFieldCallbackImpl(this.appMetaData));		
					
			this.screen.add(listField);			
		}
		else
		{
			LabelField appNotFound = new LabelField(
			appResources.localize(LocaleKeys.no_apps_found, LocaleKeys.no_apps_found), LabelField.ELLIPSIS);
			this.screen.add(appNotFound);
		}
		
		this.setMenuItems();
		this.setUpNavigation();
	}
	
	private void setMenuItems()
	{		
		AppResources resources = Services.getInstance().getResources();
		
		if(this.appMetaData != null && !this.appMetaData.isEmpty())
		{
			MenuItem selectItem = new MenuItem(resources.localize(LocaleKeys.select, LocaleKeys.select), 1, 1){
				public void run()
				{
					//UserInteraction/Event Processing...this is where the Commands can be executed
					AppStoreScreen.this.handle();
				}
			};
			this.screen.addMenuItem(selectItem);
		}
		
		MenuItem backItem = new MenuItem(resources.localize(SystemLocaleKeys.back, SystemLocaleKeys.back), 2, 2){
			public void run()
			{
				//Go Home
				Services.getInstance().getNavigationContext().home();
			}
		};								 												
		this.screen.addMenuItem(backItem);
	}	
	
	private void setUpNavigation()
	{
		this.screen.addKeyListener(new KeyListener()
		{
			public boolean keyChar(char key, int status, int time)
			{				
				if(key == Characters.ESCAPE)
				{
					NavigationContext.getInstance().back();
					return true;
				}
				return false;
			}
			
			public boolean keyDown(int keyCode, int time)
			{																
				return false;
			}
			
			public boolean keyUp(int keyCode, int time)
			{				
				return false;
			}
			
			public boolean keyRepeat(int keyCode, int time)
			{				
				return false;
			}

			public boolean keyStatus(int keyCode, int time)
			{				
				return false;
			}						
		});
	}
	
	private void handle()
	{
		int selectedIndex = this.listField.getSelectedIndex();
		AppMetaData selectedApp = (AppMetaData)this.appMetaData.elementAt(selectedIndex);
								
		Configuration configuration = Configuration.getInstance();
		BrowserSession browserSession = Browser.getDefaultSession();
		
		String appUrl = "http://"+configuration.getServerIp()+":"+configuration.getHttpPort()+"/o/apps"+selectedApp.downloadUrl;		
		
		browserSession.displayPage(appUrl);
		
		
		//Go back to the main screen
		NavigationContext.getInstance().home();
	}
	//----------------------------------------------------------------------------------------------------------------------------------
	private static class ListFieldCallbackImpl implements ListFieldCallback
	{
		private Vector appMetaData;	
		
		private ListFieldCallbackImpl(Vector appMetaData)
		{			
			AppResources resources = Services.getInstance().getResources();
			this.appMetaData = appMetaData;					
		}

		public void drawListRow(ListField listField, Graphics graphics, int index,
		int y, int width) 
		{
			String action = ((AppMetaData)this.appMetaData.elementAt(index)).name;									
			graphics.drawText(action, 0, y);
		}
		
		public Object get(ListField listField, int index) 
		{			
			return ((AppMetaData)this.appMetaData.elementAt(index)).name;
		}
		
		public int indexOfList(ListField listField, String prefix, int start) 
		{			
			return this.appMetaData.indexOf(prefix, start);
		}

		public int getPreferredWidth(ListField listField) 
		{			
			return Display.getWidth();
		}				
	}	
	
	private static class AppMetaData
	{
		private String uri;
		private String name;
		private String description;
		private String downloadUrl;
	}
}
