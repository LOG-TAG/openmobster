/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.hello.sync;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import org.openmobster.cloud.api.sync.Channel;
import org.openmobster.cloud.api.sync.ChannelInfo;
import org.openmobster.cloud.api.sync.MobileBean;

import org.openmobster.core.security.device.Device;

/**
 * @author openmobster@gmail.com
 */
@ChannelInfo(uri="hellosync", mobileBeanClass="com.hello.sync.HelloSyncBean")
public class HelloSyncChannel implements Channel 
{
	private boolean pushed;
	
	//This method only returns the essential beans needed to make the App functional...kind of like
	//booting up the App...The rest of the beans are synchronized silently in the background.
	//This allows instant usage of the App without having to dock your device for a few hours for all the data to load
	@Override
	public List<? extends MobileBean> bootup() 
	{
		pushed = false;
		
		List<HelloSyncBean> bootupBeans = new ArrayList<HelloSyncBean>();
		
		//Just using mock data...Usually this will extract the information from a backend service or database
		for(int i=0; i<5; i++)
		{
			HelloSyncBean syncBean = new HelloSyncBean();
			
			syncBean.setOid(""+i);
			syncBean.setMessage("hello from "+syncBean.getOid());
			
			bootupBeans.add(syncBean);
		}
		
		return bootupBeans;
	}
	
	//Usually this would check with the backend service if anything new has popped up on this channel
	//Based on that it would send the new bean ids back, or return null, if nothing new is available
	//If something new is available, this information is automatically synced and notified on the user's mobile device
	//If not, nothing happens on the device side
	@Override
	public String[] scanForNew(Device device, Date lastScanTimestamp) 
	{
		if(!pushed)
		{
			pushed = true;
			
			return new String[]{"push:1",
			"push:2"};
		}
		else
		{
			return null;
		}
	}
	
	@Override
	public MobileBean read(String id) 
	{
		//Just mock data....Usually the bean would be constructed using the data from a backend service or database
		HelloSyncBean syncBean = new HelloSyncBean();
		syncBean.setOid(id);
		syncBean.setMessage("hello from "+syncBean.getOid());
		
		return syncBean;
	}

	@Override
	public List<? extends MobileBean> readAll() 
	{
		return null;
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
	public String[] scanForDeletions(Device device, Date lastScanTimestamp) 
	{
		return null;
	}

	
	@Override
	public String[] scanForUpdates(Device device, Date lastScanTimestamp) 
	{
		return null;
	}
}
