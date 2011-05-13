/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.android.cloud.app;

import java.lang.reflect.Field;

import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android_native.framework.BaseCloudActivity;
import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * 
 * @author openmobster@gmail.com
 */
public class App extends BaseCloudActivity
{
	@Override
	public void displayMainScreen()
	{
		try
		{
			//render the main screen
			String layoutClass = this.getPackageName()+".R$layout";
			String main = "main";
			Class clazz = Class.forName(layoutClass);
			Field field = clazz.getField(main);
			int screenId = field.getInt(clazz);
			this.setContentView(screenId);
			
			//setup the button
			Button checkCloudStatus = (Button)ViewHelper.findViewById(this,"bootstrap_status");
			checkCloudStatus.setOnClickListener(
					new OnClickListener()
					{
						public void onClick(View clicked)
						{
							//Show Bootstrap status
							if(Registry.isActive())
							{
								Registry registry = Registry.getActiveInstance();
								System.out.println("App-------------------------------------");
								System.out.println("Registry : "+registry);
								System.out.println("Registry isMoblet: "+!(registry.isContainer()));
								System.out.println("-------------------------------------");
							}
						}
					}
			);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
