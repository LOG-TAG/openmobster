/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.cloud;

import org.appcelerator.kroll.KrollInvocation;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.titanium.TiContext;

import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;
import org.openmobster.core.mobileCloud.api.model.MobileBean;

import android.app.Activity;

/**
 *
 * @author openmobster@gmail.com
 */
@Kroll.proxy
public final class AddBeanProxy extends KrollProxy
{
	private MobileBean bean;
	
	public AddBeanProxy(TiContext context)
	{
		super(context);
	}
	
	public void setBean(MobileBean bean)
	{
		this.bean = bean;
	}
	
	@Kroll.method
	public void setValue(KrollInvocation invocation,String fieldUri, String value)
	{
		if((fieldUri == null || fieldUri.trim().length() == 0) ||
			(value == null || value.trim().length() == 0)
		)
		{
			return;
		}
		
		Activity activity = invocation.getTiContext().getActivity();
		try
		{
			TitaniumKernel.startApp(activity.getApplicationContext(),activity);
		}
		catch(Throwable t)
		{
			ViewHelper.getOkModalWithCloseApp(activity, "System Error", "CloudManager App is either not installed or not running").
			show();
			
			return;
		}
		
		if(this.bean == null)
		{
			return;
		}
		
		bean.setValue(fieldUri, value);
	}
	
	@Kroll.method
	public String commit(KrollInvocation invocation)
	{
		Activity activity = invocation.getTiContext().getActivity();
		try
		{
			TitaniumKernel.startApp(activity.getApplicationContext(),activity);
		}
		catch(Throwable t)
		{
			ViewHelper.getOkModalWithCloseApp(activity, "System Error", "CloudManager App is either not installed or not running").
			show();
			
			return null;
		}
		
		if(this.bean == null)
		{
			return null;
		}
		
		this.bean.save();
		
		String oid = this.bean.getId();
		
		return oid;
	}
}
