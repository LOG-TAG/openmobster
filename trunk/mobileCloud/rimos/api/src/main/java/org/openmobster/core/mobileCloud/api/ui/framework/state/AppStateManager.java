/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.api.ui.framework.state;

import java.util.Hashtable;

import org.openmobster.core.mobileCloud.api.ui.framework.navigation.Screen;
import org.openmobster.core.mobileCloud.rimos.service.Service;
import org.openmobster.core.mobileCloud.rimos.service.Registry;


/**
 * @author openmobster@gmail
 *
 */
public final class AppStateManager extends Service
{
	private Hashtable screenState; //screenId->ScreenContext mapping
	
	public AppStateManager()
	{		
		this.screenState = new Hashtable();
	}
			
	public void start()
	{
		
	}
	
	public void stop()
	{
		
	}
	
	public static AppStateManager getInstance()
	{
		AppStateManager appStateManager = (AppStateManager)Registry.getInstance().lookup(AppStateManager.class);						
		return appStateManager;
	}	
	//-------------------------------------------------------------------------------------------------------------------------------
	public void createContext(Object scopeIndicator)
	{
		if(scopeIndicator instanceof Screen)
		{
			Screen screen = (Screen)scopeIndicator;
			ScreenContext context = new ScreenContext();
			context.setTarget(screen.getId());
			this.screenState.put(screen.getId(), context);
		}
	}
	
	public void destroyContext(Object scopeIndicator)
	{
		if(scopeIndicator instanceof Screen)
		{
			Screen screen = (Screen)scopeIndicator;
			this.screenState.remove(screen.getId());
		}
	}
	
	public AppState getContext(Object scopeIndicator)
	{
		if(scopeIndicator instanceof Screen)
		{			
			Screen screen = (Screen)scopeIndicator;
						
			AppState screenState = (AppState)this.screenState.get(screen.getId());			
			if(screenState == null)
			{
				this.createContext(screen);
				screenState = (AppState)this.screenState.get(screen.getId());
			}			
			return screenState;
		}
		return null;
	}
	
	public void updateContext(Object scopeIndicator, AppState contextState)
	{
		if(scopeIndicator instanceof Screen && contextState instanceof ScreenContext)
		{			
			Screen screen = (Screen)scopeIndicator;
			ScreenContext update = (ScreenContext)contextState;
			
			ScreenContext state = (ScreenContext)this.getContext(screen);
			if(state == null)
			{
				throw new IllegalStateException("ScreenContext is not created!!");
			}
									
			//Update the current state one attribute at a time
			String[] attributes = update.getNames();
			if(attributes != null)
			{				
				int size = attributes.length;
				for(int i=0; i<size; i++)
				{
					Object attrValue = update.getAttribute(attributes[i]);
					state.setAttribute(attributes[i], attrValue);
				}
			}
		}
	}		
}
