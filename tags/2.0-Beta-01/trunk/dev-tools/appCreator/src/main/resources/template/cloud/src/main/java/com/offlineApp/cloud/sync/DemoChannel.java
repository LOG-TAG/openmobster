/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.offlineApp.cloud.sync;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import org.openmobster.core.security.device.Device;
import org.openmobster.server.api.model.MobileBean;
import org.openmobster.server.api.model.Channel;
import org.openmobster.server.api.model.ChannelInfo;

/**
 * @author openmobster@gmail.com
 */
@ChannelInfo(uri="offlineapp_demochannel", 
		   mobileBeanClass="com.offlineApp.cloud.sync.DemoBean")
public class DemoChannel implements Channel
{
	private DemoDataRepository demoRepository;
	
	public DemoChannel()
	{
		
	}
	
	public void start()
	{
	
	}
	
	public void stop()
	{
		
	}
	
	
	public DemoDataRepository getDemoRepository()
	{
		return demoRepository;
	}

	public void setDemoRepository(DemoDataRepository demoRepository)
	{
		this.demoRepository = demoRepository;
	}
	//---Channel implementation-------------------------------------------------------------------------------------------------
	//----Synchronization LifeCycle related callbacks---------------------------------------------------------------------------
	public List<? extends MobileBean> bootup() 
	{
		List<MobileBean> list = new ArrayList<MobileBean>();
		
		//Just get only the top 5 beans to bootup the service on device side
		for(int i=0; i<1; i++)
		{
			DemoBean bean = this.demoRepository.getData().get(""+i);
			list.add(bean);
		}
		
		return list;
	}
	
	public List<? extends MobileBean> readAll() 
	{		
		List<MobileBean> list = new ArrayList<MobileBean>();
		
		//Just get only the top 5 beans to bootup the service on device side
		Set<String> beanIds = this.demoRepository.getData().keySet();
		for(String beanId: beanIds)
		{
			list.add(this.demoRepository.getData().get(beanId));
		}
		
		return list;
		
		/*List<MobileBean> list = new ArrayList<MobileBean>();
		
		//Just get only the top 5 beans to bootup the service on device side
		for(int i=0; i<1; i++)
		{
			DemoBean bean = this.demoRepository.getData().get(""+i);
			list.add(bean);
		}
		
		return list;*/
	}
	
	public MobileBean read(String id) 
	{		
		return this.demoRepository.getData().get(id);
	}

	public String create(MobileBean mobileBean) 
	{
		DemoBean newBean = (DemoBean)mobileBean;
		
		//Generate a new unique bean Id. This bean was created on the device side is being
		//synchronized with the backend service
		String newBeanId = String.valueOf(this.getDemoRepository().getData().size());
		newBean.setBeanId(newBeanId);
		
		this.demoRepository.getData().put(newBeanId, newBean);
		
		return newBeanId;
	}
	
	public void update(MobileBean mobileBean) 
	{
		DemoBean updatedBean = (DemoBean)mobileBean;
		
		this.demoRepository.getData().put(updatedBean.getBeanId(), updatedBean);
	}	

	public void delete(MobileBean mobileBean) 
	{		
		DemoBean deletedBean = (DemoBean)mobileBean;
		this.demoRepository.getData().remove(deletedBean.getBeanId());
	}
	//---Comet Lifecycle related callbacks-----------------------------------------------------------------------------------------------
	public String[] scanForNew(Device device, Date lastScanTimestamp) 
	{
		
		List<DemoBean> newBeans = this.demoRepository.getNewBeans();
		if(newBeans != null && !newBeans.isEmpty())
		{
			System.out.println("Starting Push---------------------------------------------------------------");
			
			//Just wait a little to make sure the device is probably in standby after triggering the push
			//This is used to demonstrate that 'Realtime Push' is delivered even when the device is in standby mode
			//try{Thread.currentThread().sleep(60000);}catch(Exception e){}
			
			List<String> newIds = new ArrayList<String>();
			for(DemoBean newBean:newBeans)
			{
				System.out.println("Pushing: "+newBean.getBeanId());
				newIds.add(newBean.getBeanId());
			}
			this.demoRepository.cleanNewBeans();
			System.out.println("-------------------------------------------------------------------------");
			
			return newIds.toArray(new String[0]);
		}
		
		return null;
	}
	
	public String[] scanForDeletions(Device device, Date lastScanTimestamp) 
	{		
		return null;
	}

	public String[] scanForUpdates(Device device, Date lastScanTimestamp) 
	{		
		return null;
	}
}
