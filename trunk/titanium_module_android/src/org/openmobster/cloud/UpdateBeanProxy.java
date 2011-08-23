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

import org.openmobster.android.api.sync.BeanList;
import org.openmobster.android.api.sync.MobileBean;

/**
 *
 * @author openmobster@gmail.com
 */
@Kroll.proxy
public final class UpdateBeanProxy extends KrollProxy
{
	private MobileBean update;
	
	public UpdateBeanProxy(TiContext context)
	{
		super(context);
	}
	
	public void setBean(MobileBean update)
	{
		this.update = update;
	}
	
	@Kroll.method
	public String oid()
	{
		if(this.update == null)
		{
			return null;
		}
		return this.update.getId();
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
		
		
		if(this.update == null)
		{
			return;
		}
		
		this.update.setValue(fieldUri, value);
	}
	
	@Kroll.method
	public void commit(KrollInvocation invocation)
	{
		
		if(this.update == null)
		{
			return;
		}
		
		this.update.save();
	}
	
	@Kroll.method
	public String getValue(KrollInvocation invocation,String fieldUri)
	{
		if((fieldUri == null || fieldUri.trim().length() == 0)
		)
		{
			return null;
		}
		
		
		if(this.update == null)
		{
			return null;
		}
		
		String value = this.update.getValue(fieldUri);
		
		return value;
	}
	
	@Kroll.method
	public int arrayLength(KrollInvocation invocation,String arrayUri)
    {
		if((arrayUri == null || arrayUri.trim().length() == 0)
		)
		{
					return 0;
		}
		
        if(this.update == null)
        {
        	return 0;
        }
        
        BeanList array = this.update.readList(arrayUri);
        if(array != null)
        {
            return array.size();
        }
        
        return 0;
    }
}
