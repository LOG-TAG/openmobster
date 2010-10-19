/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.api.ui.framework.navigation;

import java.util.Hashtable;

import org.openmobster.core.mobileCloud.api.ui.framework.state.AppStateManager;
import org.openmobster.core.mobileCloud.api.ui.framework.state.ScreenContext;

/**
 * @author openmobster@gmail.com
 *
 */
final class ScreenManager 
{
	private static ScreenManager singleton;
	
	private Hashtable registry;
	
	private ScreenManager()
	{
		this.registry = new Hashtable();
	}
	
	static ScreenManager getInstance()
	{
		if(ScreenManager.singleton == null)
		{
			synchronized(ScreenManager.class)
			{
				if(ScreenManager.singleton == null)
				{
					ScreenManager.singleton = new ScreenManager();
				}
			}
		}
		return ScreenManager.singleton;
	}
	//----------------------------------------------------------------------------------------------------------------------------------------------
	void register(String id, Screen screen)
	{
		ScreenMetaData metadata = new ScreenMetaData();
		metadata.screen = screen;
		metadata.isRendered = false;
		screen.setId(id);
		this.registry.put(id, metadata);				
	}
			
	Screen find(String id)
	{
		ScreenMetaData metadata = (ScreenMetaData)this.registry.get(id);
		if(metadata != null)
		{
			return metadata.screen;
		}
		return null;
	}
	
	void render(String id)
	{
		ScreenMetaData metadata = (ScreenMetaData)this.registry.get(id);
		if(metadata != null && !metadata.isRendered)
		{
			metadata.screen.render();
			metadata.isRendered = true;
		}
	}
	
	void forceRender(String id)
	{
		ScreenMetaData metadata = (ScreenMetaData)this.registry.get(id);
		if(metadata != null)
		{
			metadata.screen.render();
			metadata.isRendered = true;
		}
	}
	//----------------------------------------------------------------------------------------------------------------------------------------------
	private static class ScreenMetaData
	{
		private Screen screen;
		private boolean isRendered;		
	}
}
