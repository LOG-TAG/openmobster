/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.rim_native.framework;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.container.FullScreen;

import org.openmobster.core.mobileCloud.api.ui.framework.navigation.Screen;
import org.openmobster.core.mobileCloud.spi.ui.framework.NavigationContextSPI;

/**
 * @author openmobster@gmail.com
 *
 */
final class NativeNavigationContextSPI implements NavigationContextSPI 
{
	NativeNavigationContextSPI()
	{		
	}
	//-------------------------------------------------------------------------------------------------------------------------------------
	public void back(Screen screen) 
	{
		this.display(screen);
	}

	public void home(Screen screen) 
	{
		this.display(screen);
	}

	public void navigate(Screen screen) 
	{
		this.display(screen);
	}
	
	public void refresh()
	{
		UiApplication uiApplication = UiApplication.getUiApplication();
		net.rim.device.api.ui.Screen activeScreen = uiApplication.getActiveScreen();
		
		activeScreen.invalidate();
	}
	//--------------------------------------------------------------------------------------------------------------------------------------
	private void display(Screen screen)
	{
		UiApplication uiApplication = UiApplication.getUiApplication();
		net.rim.device.api.ui.Screen activeScreen = uiApplication.getActiveScreen();		
		
		if(activeScreen != null)
		{
			uiApplication.popScreen(activeScreen);
		}
		uiApplication.pushScreen((FullScreen)screen.getContentPane());
	}
}
