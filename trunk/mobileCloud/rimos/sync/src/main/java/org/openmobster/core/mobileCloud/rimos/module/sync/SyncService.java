/**
 * Copyright (c) {2003,2009} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.rimos.module.sync;

import java.util.Vector;
import java.io.IOException;

import org.openmobster.core.mobileCloud.rimos.configuration.Configuration;
import org.openmobster.core.mobileCloud.rimos.errors.SystemException;
import org.openmobster.core.mobileCloud.rimos.module.connection.NetSession;
import org.openmobster.core.mobileCloud.rimos.module.connection.NetworkConnector;
import org.openmobster.core.mobileCloud.rimos.module.connection.NetworkException;
import org.openmobster.core.mobileCloud.rimos.module.sync.engine.ChangeLogEntry;
import org.openmobster.core.mobileCloud.rimos.module.sync.engine.SyncEngine;
import org.openmobster.core.mobileCloud.rimos.service.Registry;
import org.openmobster.core.mobileCloud.rimos.service.Service;
import org.openmobster.core.mobileCloud.rimos.module.sync.daemon.LoadProxyDaemon;


/**
 * @author openmobster@gmail.com
 *
 */
public final class SyncService extends Service 
{		
	private SyncEngine syncEngine;
	
	public static String OPERATION_ADD = "Add";
	public static String OPERATION_UPDATE = "Replace";
	public static String OPERATION_DELETE = "Delete";	
	public static String OPERATION_MAP = "Map";
				
	public SyncService()
	{
		
	}
	
	public void start()
	{
		try
		{
			this.syncEngine = new SyncEngine();																	
		}
		catch(Exception e)
		{
			throw new SystemException(this.getClass().getName(), "constructor", new Object[]{
				"Exception="+e.toString(),
				"Message="+e.getMessage()
			});
		}
	}
	
	public void stop()
	{
		this.syncEngine = null;
	}
	
	/**
	 * 
	 * @return
	 */
	public static SyncService getInstance()
	{
		return (SyncService)Registry.getInstance().lookup(SyncService.class);
	}
	
	public String getDeviceId()
	{
		return Configuration.getInstance().getDeviceId();
	}
	
	public String getServerId()
	{
		return Configuration.getInstance().getServerId();
	}
	//---------------------------------------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @param recordId
	 */
	public synchronized void performStreamSync(String serviceId, String recordId, boolean isBackground) throws SyncException
	{		
		NetSession session = null;
		try
		{
			Configuration configuration = Configuration.getInstance();
			boolean secure = configuration.isSSLActivated();
			session = NetworkConnector.getInstance().openSession(secure);
						
			String deviceId = configuration.getDeviceId();
			String authHash = configuration.getAuthenticationHash();
			String sessionInitPayload = "<auth>"+deviceId+"|"+authHash
			+"</auth>&processorid=sync";
			
			String response = session.sendTwoWay(sessionInitPayload);
			
			if(response.indexOf("status=200")!=-1)
			{
				this.performStreamSync(session, serviceId, recordId, isBackground);
			}
		}
		catch(NetworkException e)
		{
			throw new SyncException(this.getClass().getName(), "performStreamSync://NetworkException", 
			new Object[]{serviceId, recordId});
		}
		finally
		{
			if(session != null)
			{
				session.close();
			}
		}
	}
	
	/**
	 * 
	 * @param deviceService
	 * @param serverService
	 * @throws SyncException
	 */
	public synchronized void performTwoWaySync(String deviceService, String serverService, boolean isBackground) throws SyncException
	{
		try
		{
			this.startSync(SyncAdapter.TWO_WAY, deviceService, serverService, isBackground);
		}
		catch(NetworkException e)
		{
			throw new SyncException(this.getClass().getName(), "performTwoWaySync://IOException", 
			new Object[]{SyncAdapter.TWO_WAY, deviceService, serverService});
		}
	}
	
	/**
	 * 
	 * @param deviceService
	 * @param serverService
	 * @throws SyncException
	 */
	public synchronized void performSlowSync(String deviceService, String serverService, boolean isBackground) throws SyncException
	{
		try
		{
			this.startSync(SyncAdapter.SLOW_SYNC, deviceService, serverService, isBackground);
		}
		catch(NetworkException e)
		{
			throw new SyncException(this.getClass().getName(), "performSlowSync://IOException", 
			new Object[]{SyncAdapter.SLOW_SYNC, deviceService, serverService});
		}
	}
	
	/**
	 * 
	 * @param deviceService
	 * @param serverService
	 * @throws SyncException
	 */
	public synchronized void performOneWayServerSync(String deviceService, String serverService, boolean isBackground) throws SyncException
	{
		try
		{
			this.startSync(SyncAdapter.ONE_WAY_SERVER, deviceService, serverService, isBackground);
		}
		catch(NetworkException e)
		{
			throw new SyncException(this.getClass().getName(), "performOneWayServerSync://IOException", 
			new Object[]{SyncAdapter.ONE_WAY_SERVER, deviceService, serverService});
		}
	}
	
	/**
	 * 
	 * @param deviceService
	 * @param serverService
	 * @throws SyncException
	 */
	public synchronized void performOneWayClientSync(String deviceService, String serverService, boolean isBackground) throws SyncException
	{
		try
		{
			this.startSync(SyncAdapter.ONE_WAY_CLIENT, deviceService, serverService, isBackground);
		}
		catch(NetworkException e)
		{
			throw new SyncException(this.getClass().getName(), "performOneWayClient://IOException", 
			new Object[]{SyncAdapter.ONE_WAY_CLIENT, deviceService, serverService});
		}
	}
	
	public synchronized void performBootSync(String deviceService, String serverService, boolean isBackground) throws SyncException
	{
		try
		{
			this.startSync(SyncAdapter.BOOT_SYNC, deviceService, serverService, isBackground);
			LoadProxyDaemon.getInstance().scheduleProxyTask();
		}
		catch(NetworkException e)
		{
			throw new SyncException(this.getClass().getName(), "performBootSync://IOException", 
			new Object[]{SyncAdapter.BOOT_SYNC, deviceService, serverService});
		}
	}
	
	public void updateChangeLog(String service, String operation, String objectId) throws SyncException
	{
		Vector entries = new Vector();
		
		ChangeLogEntry changelogEntry = new ChangeLogEntry();
		changelogEntry.setNodeId(service);
		changelogEntry.setOperation(operation);
		changelogEntry.setRecordId(objectId);
		
		entries.addElement(changelogEntry);
		
		this.syncEngine.addChangeLogEntries(entries);
	}
	//---------------------------------------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @param is
	 * @param os
	 * @param data
	 * @throws SyncException
	 * @throws IOException
	 */
	private void performStreamSync(NetSession session, String serviceId, String streamRecordId, boolean isBackground)
	throws SyncException, NetworkException
	{	
		SyncAdapter syncAdapter = new SyncAdapter();
		syncAdapter.setSyncEngine(syncEngine);
		
    	//Get the initialization payload
		SyncAdapterRequest request = new SyncAdapterRequest();
		request.setAttribute(SyncAdapter.SOURCE, Configuration.getInstance().getDeviceId());
		request.setAttribute(SyncAdapter.TARGET, Configuration.getInstance().getServerId());
		request.setAttribute(SyncAdapter.MAX_CLIENT_SIZE, ""+Configuration.getInstance().getMaxPacketSize());
		request.setAttribute(SyncAdapter.CLIENT_INITIATED, "true");
		request.setAttribute(SyncAdapter.DATA_SOURCE, serviceId);
		request.setAttribute(SyncAdapter.DATA_TARGET, serviceId);
		request.setAttribute(SyncAdapter.SYNC_TYPE, SyncAdapter.STREAM);
		request.setAttribute(SyncAdapter.STREAM_RECORD_ID, streamRecordId);
		request.setAttribute("isBackgroundSync", String.valueOf(isBackground));
		
		//Start the synchronization session
		SyncAdapterResponse response = syncAdapter.start(request);
		
		//Setup the payload to be sent to the server
		String payLoad = (String)response.getAttribute(SyncAdapter.PAYLOAD);		
		
		//Start sync session																		
		String data = session.sendPayloadTwoWay(payLoad);
		
		
		
		//Orchestrate the synchronization session until it is successfully finished
		while(true)
		{
			request = new SyncAdapterRequest();
			request.setAttribute(SyncAdapter.PAYLOAD, data);
			response = syncAdapter.service(request);
						
			payLoad = (String)response.getAttribute(SyncAdapter.PAYLOAD);			
			
			
			if(response.getStatus() == SyncAdapter.RESPONSE_CLOSE)
			{
				break;
			}			
												
			int retry = 5; //retry package sending 5 times..sometimes on mobile networks there is some packet loss
			//if the device is moving through coverage areas, changing cells, etc..
			//if after 5 retries, it still fails, then give up and a new sync session will be tried at a later time
			do
			{
				data = session.sendPayloadTwoWay(payLoad);	
				if(data.indexOf("status=500") == -1)
				{
					//Everything was cool in sending the sync package
					break;
				}
			}while(retry-- > 0);
		}
	}
	
	private void performSync(NetSession session, String syncType, String deviceService, String serverService, boolean isBackground)
	throws SyncException, NetworkException
	{
		SyncAdapter syncAdapter = new SyncAdapter();
		syncAdapter.setSyncEngine(syncEngine);
		
    	//Get the initialization payload
		SyncAdapterRequest request = new SyncAdapterRequest();
		request.setAttribute(SyncAdapter.SOURCE, Configuration.getInstance().getDeviceId());
		request.setAttribute(SyncAdapter.TARGET, Configuration.getInstance().getServerId());
		request.setAttribute(SyncAdapter.MAX_CLIENT_SIZE, ""+Configuration.getInstance().getMaxPacketSize());
		request.setAttribute(SyncAdapter.CLIENT_INITIATED, "true");
		request.setAttribute(SyncAdapter.DATA_SOURCE, deviceService);
		request.setAttribute(SyncAdapter.DATA_TARGET, serverService);
		request.setAttribute(SyncAdapter.SYNC_TYPE, syncType);
		request.setAttribute("isBackgroundSync", String.valueOf(isBackground));
		
		//Start the synchronization session
		SyncAdapterResponse response = syncAdapter.start(request);
		
		//Setup the payload to be sent to the server
		String payLoad = (String)response.getAttribute(SyncAdapter.PAYLOAD);		
		
		//Start sync session
		String data = session.sendPayloadTwoWay(payLoad);
				
		//Orchestrate the synchronization session until it is successfully finished
		while(true)
		{
			request = new SyncAdapterRequest();
			request.setAttribute(SyncAdapter.PAYLOAD, data);
			response = syncAdapter.service(request);
						
			payLoad = (String)response.getAttribute(SyncAdapter.PAYLOAD);			
			
			
			if(response.getStatus() == SyncAdapter.RESPONSE_CLOSE)
			{
				break;
			}			
			
			
			int retry = 5; //retry package sending 5 times..sometimes on mobile networks there is some packet loss
			//if the device is moving through coverage areas, changing cells, etc..
			//if after 5 retries, it still fails, then give up and a new sync session will be tried at a later time
			do
			{
				data = session.sendPayloadTwoWay(payLoad);	
				if(data.indexOf("status=500") == -1)
				{
					//Everything was cool in sending the sync package
					break;
				}
			}while(retry-- > 0);
		}
	}
	
	private void startSync(String syncType, String deviceService, String serverService, boolean isBackground) 
	throws NetworkException, SyncException
	{				
		NetSession session = null;
		try
		{
			Configuration configuration = Configuration.getInstance();
			boolean secure = configuration.isSSLActivated();
			session = NetworkConnector.getInstance().openSession(secure);
			
			String deviceId = configuration.getDeviceId();
			String authHash = configuration.getAuthenticationHash();
			String sessionInitPayload = "<auth>"+deviceId+"|"+authHash
			+"</auth>&processorid=sync";
			
			String response = session.sendTwoWay(sessionInitPayload);
			
			if(response.indexOf("status=200")!=-1)
			{
				this.performSync(session, syncType, deviceService, serverService, isBackground);
			}
		}		
		finally
		{
			if(session != null)
			{
				session.close();
			}
		}
	}	
}
