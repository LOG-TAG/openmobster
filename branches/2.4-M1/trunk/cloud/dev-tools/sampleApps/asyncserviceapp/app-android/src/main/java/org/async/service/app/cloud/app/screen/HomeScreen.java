/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.async.service.app.cloud.app.screen;

import java.lang.reflect.Field;

import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.Screen;

import android.app.Activity;

/**
 * The Main Screen of the App. This is rendered when the App is launched. It is registered as the 'bootstrap' screen
 * in 'resources/moblet-app/moblet-app.xml'
 * 
 * @author openmobster@gmail.com
 */
public class HomeScreen extends Screen
{
	private Integer screenId;
	
	@Override
	public void render()
	{
		try
		{
			final Activity currentActivity = Services.getInstance().getCurrentActivity();
			
			//Layout the Home Screen. The layout is specified in 'res/layout/home.xml'
			String layoutClass = currentActivity.getPackageName()+".R$layout";
			String home = "home";
			Class clazz = Class.forName(layoutClass);
			Field field = clazz.getField(home);
			
			this.screenId = field.getInt(clazz);						
		}
		catch(Exception e)
		{
			SystemException se = new SystemException(this.getClass().getName(), "render", new Object[]{
				"Message:"+e.getMessage(),
				"Exception:"+e.toString()
			});
			ErrorHandler.getInstance().handle(se);
			throw se;
		}
	}
	
	@Override
	public Object getContentPane()
	{
		return this.screenId;
	}
	
	@Override
	public void postRender()
	{
		//Asynchronously get a list of emails from the Cloud
		CommandContext commandContext = new CommandContext();
		
		//'/asyncserviceapp/getlist' indicates the URI of the AsyncCommand instance that will be invoked
		//This is registered in the 'resources/moblet-app/moblet-app.xml' file
		commandContext.setTarget("/asyncserviceapp/getlist");
		
		//Making the invocation
		Services.getInstance().getCommandService().execute(commandContext);
	}
}
