/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.cloud;

import org.json.JSONArray;

import org.openmobster.core.mobileCloud.api.model.MobileBean;

import org.appcelerator.kroll.KrollInvocation;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;

import org.appcelerator.titanium.TiContext;

/**
 *
 * @author openmobster@gmail.com
 */
@Kroll.proxy
public final class SyncProxy extends KrollProxy
{	
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
	public UpdateBeanProxy readById(KrollInvocation invocation,String channel,String oid)
	{
		if((channel == null || channel.trim().length() == 0) ||
			(oid == null || oid.trim().length() == 0)
		)
		{
			return null;
		}
		
		MobileBean bean = MobileBean.readById(channel, oid);
		if(bean == null)
		{
			return null;
		}
		
		UpdateBeanProxy updateProxy = new UpdateBeanProxy(invocation.getTiContext());
		updateProxy.setBean(bean);
		
		return updateProxy;
	}
	
	@Kroll.method
	public AddBeanProxy newBean(KrollInvocation invocation, String channel)
	{
		if((channel == null || channel.trim().length() == 0) 
			)
		{
			return null;
		}
		
		MobileBean newBean = MobileBean.newInstance(channel);
		AddBeanProxy proxy = new AddBeanProxy(invocation.getTiContext());
		proxy.setBean(newBean);
		
		return proxy;
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
		
		MobileBean bean = MobileBean.readById(channel, oid);
		if(bean == null)
		{
			return null;
		}
		
		String beanId = bean.getId();
		
		bean.delete();
		
		return beanId;
	}
}
