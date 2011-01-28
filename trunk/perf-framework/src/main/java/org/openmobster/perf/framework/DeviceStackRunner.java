/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.perf.framework;

import org.openmobster.device.agent.sync.SyncAdapterRequest;
import org.openmobster.device.agent.sync.SyncAdapter;
import org.openmobster.device.agent.sync.SyncService;

import org.openmobster.device.agent.test.framework.Configuration;
import org.openmobster.device.agent.test.framework.MobileBeanRunner;

/**
 * 
 * @author openmobster@gmail.com
 */
public class DeviceStackRunner extends MobileBeanRunner
{	
	@Override
	protected void executeSyncProtocol(SyncAdapterRequest initRequest) throws Exception
	{
		SyncService syncService = this.getSyncService();
		Configuration configuration = this.getConfiguration();
		
		String syncType = (String)initRequest.getAttribute(SyncAdapter.SYNC_TYPE);
		String deviceId = configuration.getDeviceId();
		String serverId = configuration.getServerId();
		String channel = (String)initRequest.getAttribute(SyncAdapter.DATA_SOURCE);
		syncService.startSync(this, syncType, deviceId, serverId, channel);
	}
}
