/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.rimos.module.sync.daemon;

import java.util.Vector;
import java.util.TimerTask;

import org.openmobster.core.mobileCloud.rimos.configuration.Configuration;
import org.openmobster.core.mobileCloud.rimos.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.rimos.errors.SystemException;
import org.openmobster.core.mobileCloud.rimos.module.mobileObject.MobileObject;
import org.openmobster.core.mobileCloud.rimos.module.mobileObject.MobileObjectDatabase;
import org.openmobster.core.mobileCloud.rimos.module.sync.SyncException;
import org.openmobster.core.mobileCloud.rimos.module.sync.SyncService;

/**
 * Initiate Sync Task is run by the Daemon to initiate sync session from the device with the server based on appropriate
 * environmental state. Such state would be a modified device changelog indicating change in the state of data on the device, etc
 * 
 * @author openmobster@gmail.com
 *
 */
final class InitiateProxyTask extends TimerTask 
{
	boolean inProgress;
	boolean executionFinished;
	
	public InitiateProxyTask()
	{
		
	}
	
	public void run()
	{
		this.inProgress = true;
		try
		{
			Configuration conf = Configuration.getInstance();
			Vector myChannels = conf.getMyChannels();
			if(myChannels != null && !myChannels.isEmpty())
			{
				int size = myChannels.size();
				for(int i=0;i<size; i++)
				{
					String channel = (String)myChannels.elementAt(i);
					this.loadProxies(channel);
				}
			}
		}
		catch(Exception e)
		{
			SystemException syse = new SystemException(this.getClass().getName(), "InitiateSyncTask", new Object[]{
				"Exception="+e.toString(),
				"Message="+e.getMessage()
			});
			ErrorHandler.getInstance().handle(syse);
		}
		finally
		{
			this.inProgress = false;
			this.executionFinished = true;
		}
	}
	
	private void loadProxies(String channel) throws SyncException
	{
		MobileObjectDatabase deviceDB = MobileObjectDatabase.getInstance();
		Vector allObjects = deviceDB.readAll(channel);
		
		if(allObjects != null)
		{
			int size = allObjects.size();
			for(int i=0; i<size; i++)
			{
				MobileObject mo = (MobileObject)allObjects.elementAt(i);
				if(mo.isProxy())
				{
					//false because it does not need to send any push related notifications
					//just load the data silently and be done with it
					SyncService.getInstance().performStreamSync(channel, mo.getRecordId(), false);
				}
			}
		}
	}
}
