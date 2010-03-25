/**
 * Copyright (c) {2003,2009} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.examples.offlineapp;

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
@ChannelInfo(uri="/offlineapp/demochannel", 
		   mobileBeanClass="org.openmobster.core.examples.offlineapp.DemoBean")
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
		for(int i=0; i<5; i++)
		{
			DemoBean bean = this.demoRepository.getData().get(""+i);
			list.add(bean);
		}
		
		mockCounter = 5; //resets pushing
		
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
	private static int mockCounter = 5;
	public String[] scanForNew(Device device, Date lastScanTimestamp) 
	{
		if(mockCounter <= 0)
		{
			return null;
		}
		mockCounter --;
		
		DemoBean newBean = this.createNewDemoBean();
		
		System.out.println("Pushing----------------------------------------------------");
		System.out.println("MockCounter :"+mockCounter);
		System.out.println("Device :"+device.getIdentifier());
		System.out.println("DemoBean :"+newBean.getBeanId());
		System.out.println("-----------------------------------------------------------");
		
		return new String[]{newBean.getBeanId()};
		//return null;
	}
	
	public String[] scanForDeletions(Device device, Date lastScanTimestamp) 
	{		
		return null;
	}

	public String[] scanForUpdates(Device device, Date lastScanTimestamp) 
	{		
		return null;
	}
	//--------------Just mock new bean generation to demo Push functionality----------------------------------------------------------------------
	private DemoBean createNewDemoBean()
	{
		String beanId = ""+this.demoRepository.getData().size();
		DemoBean bean = new DemoBean();
		bean.setBeanId(beanId);
		
		//Set the demo string
		bean.setDemoString("/demostring/"+beanId);
		
		//Set the demo array
		String[] demoArray = new String[5];
		for(int index=0; index<demoArray.length; index++)
		{
			demoArray[index] = "/demoarray/"+index+"/"+beanId;
		}
		bean.setDemoArray(demoArray);
		
		//Set the demo list
		List<String> demoList = new ArrayList<String>();
		for(int index=0; index<5; index++)
		{
			demoList.add("/demolist/"+index+"/"+beanId);
		}
		bean.setDemoList(demoList);
		
		this.demoRepository.getData().put(beanId, bean);
		
		return bean;
	}
}
