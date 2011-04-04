/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.sync.app;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import org.openmobster.core.security.device.Device;
import org.openmobster.server.api.model.Channel;
import org.openmobster.server.api.model.ChannelInfo;
import org.openmobster.server.api.model.MobileBean;

/**
 * The SyncBeanChannel mobilizes the 'SyncBean' instances by integrating with the Sync Engine
 * 
 * @author openmobster@gmail.com
 */
@ChannelInfo(uri="sync_bean_channel", mobileBeanClass="org.openmobster.sync.app.SyncBean")
public class SyncBeanChannel implements Channel
{
	private SyncBeanStore store;
	
	public void start()
	{
		//Bootstrapping the Mock Data Store
		this.store = new SyncBeanStore();
		this.store.generateMockBeans();
	}
	
	public void stop()
	{
		
	}

	@Override
	public List<? extends MobileBean> bootup() 
	{
		List<SyncBean> beans = this.store.getAll();
		List<SyncBean> bootupOnly = new ArrayList<SyncBean>();
		
		//Only send first 5 beans during booting up the sync channel
		for(int i=0; i<5; i++)
		{
			bootupOnly.add(beans.get(i));
		}
		
		return bootupOnly;
	}

	@Override
	public MobileBean read(String id) 
	{
		return this.store.get(id);
	}

	@Override
	public List<? extends MobileBean> readAll() 
	{
		return this.store.getAll();
	}
	
	@Override
	public String create(MobileBean mobileBean) 
	{
		return null;
	}

	@Override
	public void update(MobileBean mobileBean) 
	{	
	}
	
	@Override
	public void delete(MobileBean mobileBean) 
	{	
	}
	
	@Override
	public String[] scanForNew(Device device, Date lastScanTimestamp) 
	{
		return null;
	}

	@Override
	public String[] scanForUpdates(Device device, Date lastScanTimestamp) 
	{
		return null;
	}
	
	@Override
	public String[] scanForDeletions(Device device, Date lastScanTimestamp) 
	{
		return null;
	}
}
