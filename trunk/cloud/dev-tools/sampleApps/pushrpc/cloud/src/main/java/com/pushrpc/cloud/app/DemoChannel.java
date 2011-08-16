/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.pushrpc.cloud.app;

import java.util.Date;
import java.util.List;

import org.openmobster.cloud.api.model.Channel;
import org.openmobster.cloud.api.model.ChannelInfo;
import org.openmobster.cloud.api.model.MobileBean;
import org.openmobster.core.common.Utilities;
import org.openmobster.core.dataService.push.PushCommandContext;
import org.openmobster.core.dataService.push.PushRPC;
import org.openmobster.core.security.device.Device;



/**
 * @author openmobster@gmail.com
 */
@ChannelInfo(uri="offlineapp_demochannel", 
		   mobileBeanClass="com.pushrpc.cloud.app.DemoBean")
public class DemoChannel implements Channel
{	
	public DemoChannel()
	{
		
	}
	
	public void start()
	{
	
	}
	
	public void stop()
	{
		
	}
	
	public String[] scanForDeletions(Device device, Date lastScanTimestamp) 
	{	
		try
		{
			//This is only for demo purposes. This is not the ideal place to 
			//make remote push invocations. I only chose this so that I don't 
			//have to write a proper Web App with GUI ;)
			
			//Prepare the push info. Notice: /cloud/push/demo is the id of the push
			//component specified in the moblet-app.xml file
			System.out.println("************************************");
			System.out.println("Starting PusRPC");
			System.out.println("************************************");
			
			PushCommandContext context = new PushCommandContext("/cloud/push/demo");
			context.setAttribute("push", "Hello://"+Utilities.generateUID());
			
			PushRPC.startPush("blah2@gmail.com", context);
			
			Thread.currentThread().sleep(45000);
			
			return null;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	//----Synchronization LifeCycle related callbacks---------------------------------------------------------------------------
	public List<? extends MobileBean> bootup() 
	{
		return null;
	}
	
	public List<? extends MobileBean> readAll() 
	{		
		return null;
	}
	
	public MobileBean read(String id) 
	{		
		return null;
	}

	public String create(MobileBean mobileBean) 
	{
		return null;
	}
	
	public void update(MobileBean mobileBean) 
	{
	}	

	public void delete(MobileBean mobileBean) 
	{		
	}
	//---Comet Lifecycle related callbacks-----------------------------------------------------------------------------------------------
	public String[] scanForNew(Device device, Date lastScanTimestamp) 
	{
		return null;
	}
	
	public String[] scanForUpdates(Device device, Date lastScanTimestamp) 
	{		
		return null;
	}
}
