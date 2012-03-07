/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.phonegap.plugin.jquery;

import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android_native.framework.CloudService;

import android.os.Bundle;

import com.phonegap.DroidGap;

/**
 *
 * @author openmobster@gmail.com
 */
public class App extends DroidGap 
{
	@Override
	public void onCreate(Bundle bundle) 
	{
		super.onCreate(bundle);
		
		super.setIntegerProperty("loadUrlTimeoutValue", 60000);
		super.loadUrl("file:///android_asset/www/index.html");
	}
	
	@Override
	protected void onStart()
	{
		try
		{
			super.onStart();
			CloudService.getInstance().start(this);
		}
		catch (Exception e)
		{
			e.printStackTrace(System.out);
			ErrorHandler.getInstance().handle(new SystemException(this.getClass().getName(), "onStart", new Object[]{
				"Message:"+e.getMessage(),
				"Exception:"+e.toString()
			}));
		}
	}
}
