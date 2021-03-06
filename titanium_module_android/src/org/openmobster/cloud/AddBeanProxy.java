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

import org.openmobster.android.api.sync.MobileBean;
import org.openmobster.android.api.sync.CommitException;

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
	
	@Kroll.method
	public String oid()
	{
		if(this.bean == null)
		{
			return null;
		}
		return this.bean.getId();
	}
	
	public void setBean(MobileBean bean)
	{
		this.bean = bean;
	}
	
	@Kroll.method
	public String getValue(KrollInvocation invocation,String fieldUri)
	{
		if((fieldUri == null || fieldUri.trim().length() == 0)
		)
		{
			return null;
		}
		
		
		if(this.bean == null)
		{
			return null;
		}
		
		String value = this.bean.getValue(fieldUri);
		
		return value;
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
		
		if(this.bean == null)
		{
			return;
		}
		
		bean.setValue(fieldUri, value);
	}
	
	@Kroll.method
	public String commit(KrollInvocation invocation)
	{
		if(this.bean == null)
		{
			return null;
		}
		
		try
		{
			this.bean.save();
		}
		catch(CommitException cme)
		{
			throw new RuntimeException(cme);
		}
		
		String oid = this.bean.getId();
		
		return oid;
	}
}
