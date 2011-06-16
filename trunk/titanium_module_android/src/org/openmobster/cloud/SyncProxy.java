/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.cloud;

import org.json.JSONArray;
import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;
import org.openmobster.core.mobileCloud.api.model.BeanList;
import org.openmobster.core.mobileCloud.api.model.MobileBean;

import org.appcelerator.kroll.KrollInvocation;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;

import org.appcelerator.titanium.TiContext;

import android.app.Activity;

/**
 *
 * @author openmobster@gmail.com
 */
@Kroll.proxy
public class SyncProxy extends KrollProxy
{
	private MobileBean newBean;
	
	public SyncProxy(TiContext context) 
	{
		super(context);
	}
	
	@Kroll.method
	public String readAll(KrollInvocation invocation,String channel)
	{
		//Validate the input
		if(channel == null || channel.trim().length()==0)
		{
			return null;
		}
		
		Activity activity = invocation.getTiContext().getActivity();
		try
		{
			TitaniumKernel.startApp(activity.getApplicationContext(),activity);
		}
		catch(Throwable t)
		{
			ViewHelper.getOkModal(activity, "System Error", "CloudManager App is either not installed or not running").
			show();
			
			return null;
		}
		
		JSONArray array = new JSONArray();
		MobileBean[] beans = MobileBean.readAll(channel);
		if(beans != null && beans.length > 0)
		{
			for(MobileBean local:beans)
			{
				String oid = local.getId();
				array.put(oid);
			}
		}
		
		return array.toString();
	}
	
	@Kroll.method
	public String getValue(KrollInvocation invocation,String channel, String oid, String fieldUri)
	{
		if((channel == null || channel.trim().length() == 0) ||
		   (oid == null || oid.trim().length() == 0) ||
		   (fieldUri == null || fieldUri.trim().length() == 0)
		)
		{
			return null;
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
			
			return null;
		}
		
		MobileBean bean = MobileBean.readById(channel, oid);
		if(bean == null)
		{
			return null;
		}
		
		String value = bean.getValue(fieldUri);
		
		return value;
	}
	
	@Kroll.method
	public int arrayLength(KrollInvocation invocation,String channel,String oid,String arrayUri)
    {
		if((channel == null || channel.trim().length() == 0) ||
				   (oid == null || oid.trim().length() == 0) ||
				   (arrayUri == null || arrayUri.trim().length() == 0)
				)
		{
					return 0;
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
			
			return 0;
		}
		
        MobileBean bean = MobileBean.readById(channel, oid);
        if(bean == null)
        {
        	return 0;
        }
        
        BeanList array = bean.readList(arrayUri);
        if(array != null)
        {
            return array.size();
        }
        
        return 0;
    }
	
	@Kroll.method
	public String setValue(KrollInvocation invocation,String channel, String oid, String fieldUri, String value)
	{
		if((channel == null || channel.trim().length() == 0) ||
				   (oid == null || oid.trim().length() == 0) ||
				   (fieldUri == null || fieldUri.trim().length() == 0) ||
				   (value == null || value.trim().length() == 0)
				)
		{
			return null;
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
			
			return null;
		}
		
		MobileBean bean = MobileBean.readById(channel, oid);
		if(bean == null)
		{
			return null;
		}
		
		bean.setValue(fieldUri, value);
		bean.save();
		
		return bean.getId();
	}
	
	@Kroll.method
	public String deleteBean(KrollInvocation invocation,String channel, String oid)
	{
		if((channel == null || channel.trim().length() == 0) ||
			(oid == null || oid.trim().length() == 0) 
		)
		{
			return null;
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
			
			return null;
		}
		
		MobileBean bean = MobileBean.readById(channel, oid);
		if(bean == null)
		{
			return null;
		}
		
		String beanId = bean.getId();
		
		bean.delete();
		
		return beanId;
	}
	
	@Kroll.method
	public void newBean(KrollInvocation invocation, String channel)
	{
		if((channel == null || channel.trim().length() == 0) 
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
		
		this.newBean = MobileBean.newInstance(channel);
	}
	
	@Kroll.method
	public void setNewBeanValue(KrollInvocation invocation, String channel, String fieldUri, String value)
	{
		if((channel == null || channel.trim().length() == 0) ||
				   (fieldUri == null || fieldUri.trim().length() == 0) ||
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
		
		if(this.newBean == null)
		{
			return;
		}
		
		newBean.setValue(fieldUri, value);
	}
	
	@Kroll.method
	public String saveNewBean(KrollInvocation invocation)
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
		
		if(this.newBean == null)
		{
			return null;
		}
		
		this.newBean.save();
		
		String oid = this.newBean.getId();
		
		this.newBean = null;
		
		return oid;
	}
}
