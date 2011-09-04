/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.synchronizer.event;

import org.apache.log4j.Logger;

import org.openmobster.core.common.event.Event;
import org.openmobster.core.common.event.EventListener;
import org.openmobster.core.synchronizer.server.engine.Tools;
import org.openmobster.cloud.api.sync.MobileBean;
import org.openmobster.core.synchronizer.server.engine.ServerSyncEngine;



/**
 *
 * @author openmobster@gmail.com
 */
public class CreateBeanEventListener implements EventListener
{
	private static Logger log = Logger.getLogger(CreateBeanEventListener.class);
	
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
		
		if(mobileBean != null)
		{
			String action = (String)event.getAttribute("action");
			if(action.equalsIgnoreCase("create"))
			{
				log.info("*************************************");
				log.info("New Bean Added: "+Tools.getOid(mobileBean));
				log.info("*************************************");
			}
		}
	}
}
