/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.sync.app;

import java.util.List;
import java.util.ArrayList;

/**
 * Generates Stub 'SyncBean' instances
 * 
 * @author openmobster@gmail.com
 */
public class SyncBeanStore 
{
	private List<SyncBean> beans;
	
	public SyncBeanStore()
	{
		this.beans = new ArrayList<SyncBean>();
	}
	
	public List<SyncBean> getAll()
	{
		return this.beans;
	}
	
	public SyncBean get(String beanId)
	{
		for(int i=0; i<10; i++)
		{
			SyncBean local = this.beans.get(i);
			if(local.getBeanId().equals(beanId))
			{
				return local;
			}
		}
		return null;
	}
	
	public void generateMockBeans()
	{
		for(int i=0; i<10; i++)
		{
			SyncBean local = new SyncBean();
			
			local.setBeanId(""+i);
			local.setValue1("/bean/value1/"+i);
			local.setValue2("/bean/value2/"+i);
			local.setValue3("/bean/value3/"+i);
			this.beans.add(local);
		}
	}
}
