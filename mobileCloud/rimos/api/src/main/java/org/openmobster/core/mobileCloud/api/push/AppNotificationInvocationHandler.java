/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.api.push;

import java.util.Hashtable;
import java.util.Vector;

import org.openmobster.core.mobileCloud.api.model.MobileBeanMetaData;
import org.openmobster.core.mobileCloud.rimos.module.bus.Bus;
import org.openmobster.core.mobileCloud.rimos.module.bus.Invocation;
import org.openmobster.core.mobileCloud.rimos.module.bus.InvocationHandler;
import org.openmobster.core.mobileCloud.rimos.module.bus.InvocationResponse;
import org.openmobster.core.mobileCloud.rimos.service.Service;
import org.openmobster.core.mobileCloud.rimos.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.rimos.errors.SystemException;
import org.openmobster.core.mobileCloud.rimos.util.GeneralTools;
import org.openmobster.core.mobileCloud.api.ui.framework.AppConfig;


/**
 * @author openmobster@gmail.com
 *
 */
public class AppNotificationInvocationHandler extends Service implements InvocationHandler
{
	private String uri;
	
	public AppNotificationInvocationHandler()
	{
		
	}	
	
	public void start() 
	{
		try
		{
			this.uri = "MobilePushInvocation/"+GeneralTools.generateUniqueId(); 
			Bus.getInstance().register(this);
		}
		catch(Exception e)
		{
			throw new SystemException(this.getClass().getName(), "start", new Object[]{e.getMessage()});
		}
	}
	
	public void stop() 
	{		
	}
	//-------------------------------------------------------------------------------------------------------------------------------------------
	public String getUri() 
	{	
		return this.uri;
	}

	public InvocationResponse handleInvocation(Invocation invocation) 
	{		
		try
		{			
			//System.out.println("AppNotificationInvocationHandler Bus: "+Bus.getInstance().getBusId());
			//System.out.println("Invocation: "+invocation);
			
			Hashtable shared = invocation.getShared();
			String pushMetaDataSize = (String)shared.get("pushMetaDataSize");
			if(pushMetaDataSize != null && pushMetaDataSize.trim().length()>0)
			{
				int size = Integer.parseInt(pushMetaDataSize);				
				//System.out.println("PushMetaData="+size);
				
				Vector beanMetaData = new Vector(); 
				for(int i=0;i<size;i++)
				{
					String service = (String)shared.get("pushMetaData["+i+"].service");
					if(!this.isMyChannel(service))
					{
						continue;
					}
					
					String id = (String)shared.get("pushMetaData["+i+"].id");
					Boolean isDeleted = (Boolean)shared.get("pushMetaData["+i+"].isDeleted");
					
					MobileBeanMetaData beanInfo = new MobileBeanMetaData(service, id);
					beanInfo.setDeleted(isDeleted.booleanValue());
					beanMetaData.addElement(beanInfo);
					
					//System.out.println("Service="+beanInfo.getService());
					//System.out.println("Id="+beanInfo.getId());
					//System.out.println("Deleted="+beanInfo.isDeleted());
				}
						
				PushListener pushListener = MobilePush.getPushListener();
				if(pushListener != null && !beanMetaData.isEmpty())
				{
					MobileBeanMetaData[] metadata = new MobileBeanMetaData[beanMetaData.size()];
					int cour = beanMetaData.size();
					for(int index=0; index<cour; index++)
					{
						metadata[index] = (MobileBeanMetaData)beanMetaData.elementAt(index);
					}
					pushListener.receivePush(new MobilePush(metadata));
				}
			}
			//System.out.println("--------------------------------------------------------------------");
		}		
		catch(Exception e)
		{
			ErrorHandler.getInstance().handle(new SystemException(
					this.getClass().getName(), "handleInvocation", new Object[]{						
						"Exception="+e.toString(),
						"Message="+e.getMessage()
					} 
			));
		}
		return null;
	}
	//-------------------------------------------------------------------------------------------------------------------------------------------
	private boolean isMyChannel(String channel)
	{
		Vector myChannels = AppConfig.getInstance().getChannels();
		
		if(myChannels != null && !myChannels.isEmpty())
		{
			return myChannels.contains(channel);
		}
		
		return false;
	}
}
