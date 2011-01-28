/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.rimos.configuration;

import java.util.Vector;

import org.openmobster.core.mobileCloud.rimos.errors.SystemException;
import org.openmobster.core.mobileCloud.rimos.storage.Database;
import org.openmobster.core.mobileCloud.rimos.storage.Record;

/**
 * Concurrency Marker: Inter-App Shared State component
 * 
 * @author openmobster@gmail.com
 * 
 *
 */
public final class Configuration
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
	private Vector myChannels;
	
	private long cometPollInterval;
	private String cometMode;
	
	private Configuration()
	{							
	}
	
	public synchronized void start() 
	{	
		try
		{
			Database database = Database.getInstance();
			
			//Initialize the provisioning table
			if(!database.doesTableExist(Database.provisioning_table))
			{
				database.createTable(Database.provisioning_table);
				
				//Create an empty provisioning record
				database.insert(Database.provisioning_table, new Record());
			}	
			
			this.load();												
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
	
	public static Configuration getInstance()
	{
		if(Configuration.singleton == null)
		{
			synchronized(Configuration.class)
			{
				if(Configuration.singleton == null)
				{
					Configuration.singleton = new Configuration();
					Configuration.singleton.start();
				}
			}
		}
		Configuration.singleton.load(); //this makes sure its the most current state
		return Configuration.singleton;
	}
	//--------------------------------------------------------------------------------------------------------------------------------------
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
	
	public Vector getMyChannels()
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
	//--------------------------------------------------------------------------------------------------------------------------------------		
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
			this.myChannels = new Vector();
		}
		
		if(!this.myChannels.contains(channel))
		{
			this.myChannels.addElement(channel);
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
	//-----------Database related operations------------------------------------------------------------------------------------------------------------
	public synchronized void save()
	{
		try
		{			
			Record provisioningRecord = (Record)Database.getInstance().selectAll(Database.provisioning_table).nextElement();
		
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
			if(this.authenticationNonce != null)
			{
				provisioningRecord.setValue("authenticationNonce", this.authenticationNonce);
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
			
			provisioningRecord.setValue("isSSLActive", ""+this.isSSLActive);
			provisioningRecord.setValue("maxPacketSize", ""+this.maxPacketSize);
			provisioningRecord.setValue("isActive", ""+this.isActive);
			provisioningRecord.setValue("isSSLCertStored", ""+this.isSSLCertStored);
			
			if(this.httpPort != null)
			{
				provisioningRecord.setValue("httpPort", this.httpPort);
			}		
									
			//persist myChannels
			this.serializeChannels(provisioningRecord);
			
			Database.getInstance().update(Database.provisioning_table, provisioningRecord);
		}
		catch(Exception e)
		{
			throw new SystemException(this.getClass().getName(), "save", new Object[]{
				"Configuration Save Exception="+e.getMessage()
			});
		}
	}
	
	private void load()
	{
		try
		{
			Record provisioningRecord = (Record)Database.getInstance().selectAll(Database.provisioning_table).nextElement();
			
			this.deviceId = provisioningRecord.getValue("deviceId");
			this.serverId = provisioningRecord.getValue("serverId");
			this.serverIp = provisioningRecord.getValue("serverIp");
			this.plainServerPort = provisioningRecord.getValue("plainServerPort");
			this.secureServerPort = provisioningRecord.getValue("secureServerPort");						
			this.authenticationHash = provisioningRecord.getValue("authenticationHash");
			this.authenticationNonce = provisioningRecord.getValue("authenticationNonce");
			this.email = provisioningRecord.getValue("email");			
			this.cometMode = provisioningRecord.getValue("cometMode");
			this.httpPort = provisioningRecord.getValue("httpPort");
			
			String cometPollIntervalStr = provisioningRecord.getValue("cometPollInterval");
			if(cometPollIntervalStr != null && cometPollIntervalStr.trim().length()>0)
			{
				this.cometPollInterval = Long.parseLong(cometPollIntervalStr);
			}
			else
			{
				this.cometPollInterval = 0; //interval not set
			}
			
			String maxPacketSizeStr = provisioningRecord.getValue("maxPacketSize");
			if(maxPacketSizeStr != null && maxPacketSizeStr.trim().length()>0)
			{
				this.maxPacketSize = Integer.parseInt(maxPacketSizeStr);
			}
			else
			{
				this.maxPacketSize = 0; //system default value
			}
			
			String sslStatus = provisioningRecord.getValue("isSSLActive");
			if(sslStatus != null && sslStatus.trim().length()>0)
			{
				this.isSSLActive = sslStatus.equals("true")?Boolean.TRUE.booleanValue():Boolean.FALSE.booleanValue();
			}
			else
			{
				this.isSSLActive = true; //system default value
			}
			
			//This is default out-of-the-box server configuration
			if(this.plainServerPort == null || this.plainServerPort.trim().length() == 0)
			{
				this.plainServerPort = "1502"; //non-ssl port for the cloud server
			}
			if(this.secureServerPort == null || this.secureServerPort.trim().length() == 0)
			{
				this.secureServerPort = "1500"; //ssl port for the cloud server
			}
			
			if(this.httpPort == null || this.httpPort.trim().length() == 0)
			{
				this.httpPort = "80"; //http port by default
			}
			
			String isActiveStr = provisioningRecord.getValue("isActive");
			if(isActiveStr != null && isActiveStr.trim().length()>0)
			{
				this.isActive = isActiveStr.equals("true")?Boolean.TRUE.booleanValue():Boolean.FALSE.booleanValue();
			}
			
			String isSSLCertStoredStr = provisioningRecord.getValue("isSSLCertStored");
			if(isSSLCertStoredStr != null && isSSLCertStoredStr.trim().length()>0)
			{
				this.isSSLCertStored = isSSLCertStoredStr.equals("true")?Boolean.TRUE.booleanValue():Boolean.FALSE.booleanValue();
			}
			
			//load myChannels
			this.myChannels = this.deserializeChannels(provisioningRecord);
		}
		catch(Exception e)
		{
			throw new SystemException(this.getClass().getName(), "load", new Object[]{
				"Configuration Save Exception="+e.getMessage()
			});
		}
	}
	//-------------------------------------------------------------------------------------------------------------------------------
	private void serializeChannels(Record record)
	{
		if(this.myChannels == null || this.myChannels.isEmpty())
		{
			return;
		}
		
		int channelCount = this.myChannels.size();
		record.setValue("myChannels:size", ""+channelCount);
		for(int i=0; i<channelCount; i++)
		{
			record.setValue("myChannels["+i+"]", (String)this.myChannels.elementAt(i));
		}
	}
	
	private Vector deserializeChannels(Record record)
	{
		Vector channels = new Vector();
		
		String cour = record.getValue("myChannels:size");
		if(cour != null && cour.trim().length()>0)
		{
			int channelCount = Integer.parseInt(cour);
			for(int i=0; i<channelCount; i++)
			{
				channels.addElement(record.getValue("myChannels["+i+"]"));
			}
		}
		
		return channels;
	}
}
