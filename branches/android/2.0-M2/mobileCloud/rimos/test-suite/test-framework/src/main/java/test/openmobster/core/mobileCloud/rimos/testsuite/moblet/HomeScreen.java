/**
 * Copyright (c) {2003,2009} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.core.mobileCloud.rimos.testsuite.moblet;

import net.rim.device.api.ui.container.MainScreen;

import org.openmobster.core.mobileCloud.api.ui.framework.navigation.Screen;


/**
 * @author openmobster@gmail.com
 */
public class HomeScreen extends Screen
{		
	private MainScreen screen;
	
	public HomeScreen()
	{
		this.screen = new MainScreen();										
	}
	//-------------------------------------------------------------------------------------------------------------------------------------------------
	public void render()
	{	
		this.screen.setTitle("OpenMobster TestSuite");
	}
	
	public Object getContentPane() 
	{		
		return this.screen;
	}			
	//---------------------------------------------------------------------------------------------------------------------------------------------------
}
