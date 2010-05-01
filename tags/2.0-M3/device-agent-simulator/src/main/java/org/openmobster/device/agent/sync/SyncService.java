/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.device.agent.sync;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Vector;

import org.openmobster.core.common.IOUtilities;
import org.openmobster.device.agent.Tools;
import org.openmobster.device.agent.sync.engine.SyncEngine;
import org.openmobster.device.agent.sync.engine.ChangeLogEntry;
import org.openmobster.device.agent.configuration.Configuration;

/**
 * @author openmobster@gmail.com
 */
public class SyncService 
{
	private SyncEngine syncEngine = null;
	
	public static String TWO_WAY = "200";
	public static String SLOW_SYNC = "201";
	public static String ONE_WAY_CLIENT = "202";
	public static String ONE_WAY_SERVER = "204";
	
	public void start()
	{
		
	}
	
	public void stop()
	{
		
	}
	
	public void setSyncEngine(SyncEngine syncEngine)
	{
		this.syncEngine = syncEngine;
	}
	
	public SyncEngine getSyncEngine()
	{
		return this.syncEngine;
	}
	//---------------------------------------------------------------------------------------------------------
	public void startSync(String syncType, String deviceId, String serverId, String deviceService, 
	String serverService)
	{
		try
		{
			Socket socket = null;
			OutputStream os = null;
			InputStream is = null;
			try
			{					
				socket = Tools.getPlainSocket();
				
				is = socket.getInputStream();
				os = socket.getOutputStream();	
								
				String authHash = Configuration.getInstance().getAuthenticationHash();				
				String sessionInitPayload = "<auth>"+deviceId+"|"+authHash
				+"</auth>&processorid=sync";
				
				IOUtilities.writePayLoad(sessionInitPayload, os);
				
				String data = IOUtilities.readServerResponse(is);
				if(data.indexOf("status=200")!=-1)
				{
					this.performSync(syncType, is, os, data, 5000,
				    deviceId, serverId, deviceService, serverService);
				}
			}
			finally
			{					
				if(socket != null)
				{
					socket.close();
				}
			}
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public void updateChangeLog(String service, String operation, String recordId)
	throws SyncException
	{
		Vector changelog = new Vector();
		ChangeLogEntry entry = new ChangeLogEntry();
		entry.setNodeId(service);
		entry.setOperation(operation);
		entry.setRecordId(recordId);
		changelog.add(entry);
		
		this.syncEngine.addChangeLogEntries(changelog);
	}
	//------------------------------------------------------------------------------------------------------------
	private void performSync(String syncType, InputStream is, OutputStream os, String data,
	int maxClientSize,String deviceId, String serverId, String deviceService, 
	String serverService)
	throws SyncException, IOException
	{	
		//Get the Client Simulator
		SyncAdapter clientSyncAdapter = new SyncAdapter();
		clientSyncAdapter.setSyncEngine(syncEngine);
		
    	//Get the initialization payload
		SyncAdapterRequest request = new SyncAdapterRequest();
		request.setAttribute(SyncAdapter.SOURCE,deviceId);
		request.setAttribute(SyncAdapter.TARGET, serverId);
		request.setAttribute(SyncAdapter.DATA_SOURCE, deviceService);
		request.setAttribute(SyncAdapter.DATA_TARGET, serverService);
		request.setAttribute(SyncAdapter.MAX_CLIENT_SIZE, String.valueOf(maxClientSize));
		request.setAttribute(SyncAdapter.CLIENT_INITIATED, "true");		
		request.setAttribute(SyncAdapter.SYNC_TYPE, syncType);
		
		//Start the synchronization session
		SyncAdapterResponse response = clientSyncAdapter.start(request);
		
		//Setup the payload to be sent to the server
		String payLoad = (String)response.getAttribute(SyncAdapter.PAYLOAD);		
		
		//Start sync session
		IOUtilities.writePayLoad(payLoad, os);
																			
		data = IOUtilities.readServerResponse(is);
		
		
		
		//Orchestrate the synchronization session until it is successfully finished
		while(true)
		{
			request = new SyncAdapterRequest();
			request.setAttribute(SyncAdapter.PAYLOAD, data);
			response = clientSyncAdapter.service(request);			
			
			payLoad = (String)response.getAttribute(SyncAdapter.PAYLOAD);			
						
			if(response.getStatus() == SyncAdapter.RESPONSE_CLOSE)
			{
				break;
			}
			
						
			//Send the payload to the server for processing			
			IOUtilities.writePayLoad(payLoad, os);
			
			data = IOUtilities.readServerResponse(is);				
		}
	}			
}
