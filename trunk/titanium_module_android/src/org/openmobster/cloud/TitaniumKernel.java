/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.cloud;

import org.openmobster.core.mobileCloud.android_native.framework.CloudService;

import android.content.Context;
import android.app.Activity;

/**
 *
 * @author openmobster@gmail.com
 */
public final class TitaniumKernel 
{
	private static TitaniumKernel singleton = null;
	
	private boolean isRunning = false;
	
	private TitaniumKernel()
	{
		
	}
	
	public static TitaniumKernel getInstance()
	{
		if(TitaniumKernel.singleton == null)
		{
			synchronized(TitaniumKernel.class)
			{
				if(TitaniumKernel.singleton == null)
				{
					TitaniumKernel.singleton = new TitaniumKernel();
				}
			}
		}
		return TitaniumKernel.singleton;
	}
	
	public void start(Context context, Activity activity)
	{
		CloudService.getInstance().start(activity);
		this.isRunning = true;
	}
	
	public void stop(Context context)
	{
		CloudService.getInstance().stop(context);
		this.isRunning = false;
	}
	
	public static void startApp(Context context,Activity activity)
	{
		TitaniumKernel kernel = TitaniumKernel.getInstance();
		if(!kernel.isRunning)
		{
			kernel.start(context,activity);
		}
	}
}
