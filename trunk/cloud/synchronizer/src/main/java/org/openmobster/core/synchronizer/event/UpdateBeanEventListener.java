/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.synchronizer.event;

import java.util.Set;

import org.apache.log4j.Logger;

import org.openmobster.core.common.event.Event;
import org.openmobster.core.common.event.EventListener;
import org.openmobster.core.synchronizer.server.engine.Tools;
import org.openmobster.cloud.api.sync.MobileBean;
import org.openmobster.cloud.api.ExecutionContext;
import org.openmobster.core.synchronizer.server.SyncContext;
import org.openmobster.core.synchronizer.server.Session;
import org.openmobster.core.synchronizer.server.engine.ServerSyncEngine;
import org.openmobster.core.security.device.Device;
import org.openmobster.core.security.device.DeviceController;
import org.openmobster.core.security.identity.Identity;
import org.openmobster.core.security.identity.IdentityController;

/**
 *
 * @author openmobster@gmail.com
 */
public class UpdateBeanEventListener implements EventListener
{
	private static Logger log = Logger.getLogger(UpdateBeanEventListener.class);
	
	private ServerSyncEngine syncEngine = null;
	
	
	
	public ServerSyncEngine getSyncEngine()
	{
		return syncEngine;
	}



	public void setSyncEngine(ServerSyncEngine syncEngine)
	{
		this.syncEngine = syncEngine;
	}

	public void start()
	{
		
	}
	
	public void stop()
	{
		
	}
	
	public void onEvent(Event event)
	{	
		MobileBean mobileBean = (MobileBean)event.getAttribute("mobile-bean");
		
		if(mobileBean == null)
		{
			return;
		}
		
		String action = (String)event.getAttribute("action");
		if(!action.equalsIgnoreCase("update"))
		{
			return;
		}
		
		SyncContext context = (SyncContext)ExecutionContext.getInstance().getSyncContext();
		Session session = context.getSession();
		DeviceController deviceController = DeviceController.getInstance();
		IdentityController identityController = IdentityController.getInstance();
		
		String deviceId = session.getDeviceId();
		String channel = session.getChannel();
		String operation = ServerSyncEngine.OPERATION_UPDATE;
		String oid = Tools.getOid(mobileBean);
		String app = session.getApp();
		
		log.info("*************************************");
		log.info("Bean Updated: "+oid);
		log.info("DeviceId : "+deviceId);
		log.info("Channel: "+channel);
		log.info("Operation: "+operation);
		log.info("App: "+app);
		
		Device device = deviceController.read(deviceId);
		if(device != null)
		{
			Identity registeredUser = device.getIdentity();
			log.info("User: "+registeredUser.getPrincipal());
			
			Set<Device> allDevices = deviceController.readByIdentity(registeredUser.getPrincipal());
			if(allDevices != null && !allDevices.isEmpty())
			{
				for(Device local:allDevices)
				{
					log.info("DeviceId: "+local.getIdentifier());
				}
			}
		}
		log.info("*************************************");
	}
}
