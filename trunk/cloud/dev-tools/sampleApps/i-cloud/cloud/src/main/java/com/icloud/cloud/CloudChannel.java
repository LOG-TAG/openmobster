/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.icloud.cloud;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Set;

import org.openmobster.cloud.api.sync.Channel;
import org.openmobster.cloud.api.sync.ChannelInfo;
import org.openmobster.cloud.api.sync.MobileBean;
import org.openmobster.core.common.Utilities;
import org.openmobster.core.security.device.Device;

/**
 *
 * @author openmobster@gmail.com
 */
@ChannelInfo(uri="cloud_channel", mobileBeanClass="com.icloud.cloud.CloudBean")
public final class CloudChannel implements Channel
{
	private Map<String,CloudBean> beans;
	
	public CloudChannel()
	{
		this.beans = new HashMap<String,CloudBean>();
	}
	
	public void start()
	{
		CloudBean bean = new CloudBean();
		bean.setBeanId(Utilities.generateUID());
		bean.setName("Sync Apps");
		bean.setValue("Rock");
		
		CloudBean bean2 = new CloudBean();
		bean2.setBeanId(Utilities.generateUID());
		bean2.setName("OpenMobster");
		bean2.setValue("Just Works");
		
		this.beans.put(bean.getBeanId(), bean);
		this.beans.put(bean2.getBeanId(), bean2);
	}
	
	public void stop()
	{
		
	}
	//---------------------------------------------------------------------------------------------------------
	public List<? extends MobileBean> bootup()
	{
		List<CloudBean> bootupBeans = new ArrayList<CloudBean>();
		
		Set<String> keys = this.beans.keySet();
		for(String key:keys)
		{
			CloudBean value = this.beans.get(key);
			bootupBeans.add(value);
		}
		
		return bootupBeans;
	}

	public List<? extends MobileBean> readAll()
	{
		return this.bootup();
	}
	
	public MobileBean read(String id)
	{
		return this.beans.get(id);
	}
	
	public String create(MobileBean mobileBean)
	{
		CloudBean cloudBean = (CloudBean)mobileBean;
		String newBeanId = Utilities.generateUID();
		
		cloudBean.setBeanId(newBeanId);
		this.beans.put(newBeanId, cloudBean);
		
		return newBeanId;
	}
	
	public void update(MobileBean mobileBean)
	{
		CloudBean cloudBean = (CloudBean)mobileBean;
		
		this.beans.put(cloudBean.getBeanId(), cloudBean);
	}
	
	public void delete(MobileBean mobileBean)
	{
		CloudBean cloudBean = (CloudBean)mobileBean;
		
		String beanId = cloudBean.getBeanId();
		
		this.beans.remove(beanId);
	}

	public String[] scanForNew(Device device, Date lastScanTimestamp)
	{
		//Not needed
		return null;
	}
	
	public String[] scanForUpdates(Device device, Date lastScanTimestamp)
	{
		//Not Needed
		return null;
	}
	
	public String[] scanForDeletions(Device device, Date lastScanTimestamp)
	{
		//Not Needed
		return null;
	}
}
