/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.configuration;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import android.content.Context;

import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.storage.Database;
import org.openmobster.core.mobileCloud.android.storage.Record;

/**
 * 
 * @author openmobster@gmail.com
 */
public class Configuration
{	
	private static Configuration singleton;
	
	private String deviceId;
	private String serverId;
	private String serverIp;
	private String plainServerPort;
	private String secureServerPort;
	private String httpPort;
	private boolean isSSLActive;
	private int maxPacketSize;
	private String authenticationHash;
	private String authenticationNonce;
	private String email;
	private boolean isActive;
	private boolean isSSLCertStored;
	private List<String> myChannels;
	
	private long cometPollInterval;
	private String cometMode;
	
	private Configuration()
	{							
	}
	
	public synchronized void start(Context context) 
	{
		try
		{						
			this.load(context);												
		}
		catch(Exception dbe)
		{
			throw new SystemException(this.getClass().getName(), "start", new Object[]{
				"Exception="+dbe.getMessage()
			});
		}
	}

	public void stop() 
	{	
		this.deviceId = null;
		this.serverId = null;
		this.serverIp = null;
		this.httpPort = null;
		this.plainServerPort = null;
		this.secureServerPort = null;
		this.isSSLActive = false;
		this.maxPacketSize = 0;
		this.authenticationHash = null;
		this.authenticationNonce = null;
		this.email = null;
		this.isActive = false;
		this.isSSLCertStored = false;
		this.myChannels = null;
		this.cometPollInterval = 0;
		this.cometMode = null;
	}
	
	public static Configuration getInstance(Context context)
	{
		if(Configuration.singleton == null)
		{
			synchronized(Configuration.class)
			{
				if(Configuration.singleton == null)
				{
					Configuration.singleton = new Configuration();
					Configuration.singleton.start(context);
				}
			}
		}
		
		Configuration.singleton.load(context); //this makes sure its the most current state
		
		return Configuration.singleton;
	}
	
	public static void stopSingleton()
	{
		Configuration.singleton = null;
	}
	//-------------------------------------------------------------------------------------------------------------------
	public String getDeviceId()
	{
		return this.deviceId;
	}
	
	public int getMaxPacketSize()
	{
		return this.maxPacketSize;
	}
	
	public String getAuthenticationHash()
	{
		String nonce = this.getAuthenticationNonce();
		if(nonce == null || nonce.trim().length()==0)
		{
			//This should be the provisioned username, password based hash
			nonce = this.authenticationHash;
		}
		return nonce;
	}
	
	public String getAuthenticationNonce()
	{
		return this.authenticationNonce;
	}
	
	public String getServerId()
	{
		return this.serverId;
	}
	
	public String getServerIp()
	{
		return this.serverIp;
	}
	
	public String getServerPort()
	{
		if(this.isSSLActivated())
		{
			return this.secureServerPort;
		}
		else
		{
			return this.plainServerPort;
		}
	}
	
	public String getPlainServerPort()
	{
		return this.plainServerPort;
	}
	
	public String getSecureServerPort()
	{
		return this.secureServerPort;
	}
	
	public boolean isSSLActivated()
	{
		return this.isSSLActive;
	}
	
	public String getEmail() 
	{
		return email;
	}
	
	public boolean isActive() 
	{
		return isActive;
	}
	
	public boolean isSSLCertStored() 
	{
		return isSSLCertStored;
	}
	
	public List<String> getMyChannels()
	{
		return this.myChannels;
	}
	
	public long getCometPollInterval() 
	{
		return cometPollInterval;
	}
	
	public String getCometMode() 
	{
		return cometMode;
	}
	
	public boolean isInPushMode()
	{
		if(this.cometMode == null || this.cometMode.trim().length() == 0 || this.cometMode.equalsIgnoreCase("push"))
		{
			return true;
		}
		return false;
	}
	
	public String getHttpPort()
	{		
		return this.httpPort;
	}
	
	public String decidePort()
	{
		if(this.isSSLActivated())
		{
			return this.getSecureServerPort();
		}
		else
		{
			return this.getPlainServerPort();
		}
	}
	//----------------------------------------------------------------------------------------------------------------
	public synchronized void setDeviceId(String deviceId)
	{
		this.deviceId = deviceId;
	}
					
	public synchronized void setMaxPacketSize(int maxPacketSize)
	{
		this.maxPacketSize = maxPacketSize;
	}
			
	public synchronized void setAuthenticationHash(String authenticationHash)
	{
		this.authenticationHash = authenticationHash;
	}
			
	public synchronized void setAuthenticationNonce(String authenticationNonce)
	{
		this.authenticationNonce = authenticationNonce;
	}
			
	public synchronized void setServerId(String serverId)
	{
		this.serverId = serverId;
	}
			
	public synchronized void setServerIp(String serverIp)
	{
		this.serverIp = serverIp;
	}
			
	public synchronized void setPlainServerPort(String plainServerPort)
	{
		this.plainServerPort = plainServerPort;
	}
			
	public synchronized void setSecureServerPort(String secureServerPort)
	{
		this.secureServerPort = secureServerPort;
	}
			
	public synchronized void activateSSL()
	{
		this.isSSLActive = true;
	}
	
	public synchronized void deActivateSSL()
	{
		this.isSSLActive = false;
	}	
		
	public synchronized void setEmail(String email) 
	{
		this.email = email;
	}

	public synchronized void setActive(boolean isActive) 
	{
		this.isActive = isActive;
	}
	
	public synchronized void setSSLCertStored(boolean isSSLCertStored) 
	{
		this.isSSLCertStored = isSSLCertStored;
	}
					
	public synchronized boolean addMyChannel(String channel)
	{
		if(this.myChannels == null)
		{
			this.myChannels = new ArrayList<String>();
		}
		
		if(!this.myChannels.contains(channel))
		{
			this.myChannels.add(channel);
			return true;
		}
		
		return false;
	}
		
	public synchronized void setCometPollInterval(long cometPollInterval) 
	{
		this.cometPollInterval = cometPollInterval;
	}
		
	

	public synchronized void setCometMode(String cometMode) 
	{
		this.cometMode = cometMode;
	}
	
	public synchronized void setHttpPort(String httpPort)
	{
		this.httpPort = httpPort;
	}
	//---------------------------------------------------------------------------------------------------------------------
	public synchronized void save(Context context)
	{
		try
		{			
			Database database = Database.getInstance(context);
			
			Set<Record> all = database.selectAll(Database.provisioning_table);
			if(all == null || all.isEmpty())
			{
				//insert
				Record provisioningRecord = new Record();
				this.prepareRecord(provisioningRecord);
				
				database.insert(Database.provisioning_table, 
				provisioningRecord);
			}
			else
			{
				//update
				Record provisioningRecord = all.iterator().next();
				this.prepareRecord(provisioningRecord);
				
				database.update(Database.provisioning_table, 
				provisioningRecord);
			}
		}
		catch(Exception e)
		{
			throw new SystemException(this.getClass().getName(), "save", new Object[]{
				"Configuration Save Exception="+e.getMessage()
			});
		}
	}
	
	private void load(Context context)
	{
		try
		{
			Database database = Database.getInstance(context);
			
			Set<Record> all = database.selectAll(Database.provisioning_table);
			if(all != null && !all.isEmpty())
			{
				Record provisioningRecord = all.iterator().next();
				
				this.prepareConfiguration(provisioningRecord);
			}
		}
		catch(Exception e)
		{
			throw new SystemException(this.getClass().getName(), "load", new Object[]{
				"Configuration Load Exception="+e.getMessage()
			});
		}
	}
	
	private void prepareRecord(Record provisioningRecord)
	{
		if(this.deviceId != null)
		{
			provisioningRecord.setValue("deviceId", this.deviceId);
		}
		
		if(this.serverId != null)
		{
			provisioningRecord.setValue("serverId", this.serverId);
		}
		
		if(this.serverIp != null)
		{
			provisioningRecord.setValue("serverIp", this.serverIp);
		}
		
		if(this.plainServerPort != null)
		{
			provisioningRecord.setValue("plainServerPort", this.plainServerPort);
		}
		
		if(this.secureServerPort != null)
		{
			provisioningRecord.setValue("secureServerPort", this.secureServerPort);
		}
		
		if(this.authenticationHash != null)
		{
			provisioningRecord.setValue("authenticationHash", this.authenticationHash);
		}
		else
		{
			provisioningRecord.removeValue("authenticationHash");
		}
		
		if(this.authenticationNonce != null)
		{
			provisioningRecord.setValue("authenticationNonce", this.authenticationNonce);
		}
		else
		{
			provisioningRecord.removeValue("authenticationNonce");
		}
		
		if(this.email != null)
		{
			provisioningRecord.setValue("email", this.email);
		}
		
		if(this.cometMode != null)
		{
			provisioningRecord.setValue("cometMode", this.cometMode);
		}
		
		provisioningRecord.setValue("cometPollInterval", ""+this.cometPollInterval);
		
		if(this.httpPort != null)
		{
			provisioningRecord.setValue("httpPort", this.httpPort);
		}
				
		provisioningRecord.setValue("isSSLActive", 
		""+this.isSSLActive);
		
		provisioningRecord.setValue("maxPacketSize", 
		""+this.maxPacketSize);
		
		provisioningRecord.setValue("isActive", ""+this.isActive);
		
		provisioningRecord.setValue("isSSLCertStored", ""+this.isSSLCertStored);
		
		this.serializeChannels(provisioningRecord);
	}
	
	private void serializeChannels(Record record)
	{
		if(this.myChannels == null || this.myChannels.isEmpty())
		{
			return;
		}
		
		int channelCount = this.myChannels.size();
		record.setValue("myChannels:size", ""+channelCount);
		int i = 0;
		for(String channel: this.myChannels)
		{
			record.setValue("myChannels["+(i++)+"]", channel);
		}
	}
	
	private void prepareConfiguration(Record record)
	{				
		this.deviceId = record.getValue("deviceId");
		this.serverId = record.getValue("serverId");
		this.serverIp = record.getValue("serverIp");
		this.plainServerPort = record.getValue("plainServerPort");
		this.secureServerPort = record.getValue("secureServerPort");
		this.authenticationHash = record.getValue("authenticationHash");
		this.authenticationNonce = record.getValue("authenticationNonce");
		this.email = record.getValue("email");
		this.cometMode = record.getValue("cometMode");
		this.httpPort = record.getValue("httpPort");
		
		String cometPollIntervalStr = record.getValue("cometPollInterval");
		if(cometPollIntervalStr != null && cometPollIntervalStr.trim().length()>0)
		{
			this.cometPollInterval = Long.parseLong(cometPollIntervalStr);			
		}
		
		String maxPacketSizeStr = record.getValue("maxPacketSize");
		if(maxPacketSizeStr != null && maxPacketSizeStr.trim().length()>0)
		{
			this.maxPacketSize = Integer.parseInt(maxPacketSizeStr);
		}
				
		String sslStatus = record.getValue("isSSLActive");
		if(sslStatus != null && sslStatus.trim().length()>0)
		{
			this.isSSLActive = Boolean.parseBoolean(sslStatus);
		}
						
		String isActiveStr = record.getValue("isActive");
		if(isActiveStr != null && isActiveStr.trim().length()>0)
		{
			this.isActive = Boolean.parseBoolean(isActiveStr);
		}
		
		String isSSLCertStoredStr = record.getValue("isSSLCertStored");
		if(isSSLCertStoredStr != null && isSSLCertStoredStr.trim().length()>0)
		{
			isSSLCertStored = Boolean.parseBoolean(isSSLCertStoredStr);
		}
		
		this.prepareChannels(record);
	}
	
	private void prepareChannels(Record record)
	{
		String cour = record.getValue("myChannels:size");
		if(cour != null && cour.trim().length()>0)
		{
			int channelCount = Integer.parseInt(cour);
			for(int i=0; i<channelCount; i++)
			{
				String channel = record.getValue("myChannels["+i+"]");
				this.addMyChannel(channel);
			}
		}
	}
}
