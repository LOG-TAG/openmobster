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

import android.content.Context;
import android.content.ContentResolver;
import android.net.Uri;
import android.database.Cursor;
import android.content.ContentValues;

import org.openmobster.core.mobileCloud.android.errors.SystemException;

/**
 * Concurrency Marker: Inter-App Shared State component
 * 
 * @author openmobster@gmail.com
 */
public class Configuration
{
	private static final Uri uri;
	
	static
	{
		uri = Uri.
		parse("content://org.openmobster.core.mobileCloud.android.provider.configuration");
	}
	
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
			ContentResolver resolver = context.getContentResolver();
			ContentValues provisioningRecord = new ContentValues();
					
			if(this.deviceId != null)
			{
				provisioningRecord.put("deviceId", this.deviceId);
			}
			if(this.serverId != null)
			{
				provisioningRecord.put("serverId", this.serverId);
			}
			if(this.serverIp != null)
			{
				provisioningRecord.put("serverIp", this.serverIp);
			}
			if(this.plainServerPort != null)
			{
				provisioningRecord.put("plainServerPort", this.plainServerPort);
			}
			if(this.secureServerPort != null)
			{
				provisioningRecord.put("secureServerPort", this.secureServerPort);
			}			
			if(this.authenticationHash != null)
			{
				provisioningRecord.put("authenticationHash", this.authenticationHash);
			}
			if(this.authenticationNonce != null)
			{
				provisioningRecord.put("authenticationNonce", this.authenticationNonce);
			}
			if(this.email != null)
			{
				provisioningRecord.put("email", this.email);
			}			
			if(this.cometMode != null)
			{
				provisioningRecord.put("cometMode", this.cometMode);
			}
			provisioningRecord.put("cometPollInterval", ""+this.cometPollInterval);
			
			provisioningRecord.put("isSSLActive", ""+this.isSSLActive);
			provisioningRecord.put("maxPacketSize", ""+this.maxPacketSize);
			provisioningRecord.put("isActive", ""+this.isActive);
			provisioningRecord.put("isSSLCertStored", ""+this.isSSLCertStored);
			
			if(this.httpPort != null)
			{
				provisioningRecord.put("httpPort", this.httpPort);
			}		
									
			//persist myChannels
			this.serializeChannels(provisioningRecord);
			
			resolver.insert(uri, provisioningRecord);
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
			ContentResolver resolver = context.getContentResolver();
							
			Cursor cursor = resolver.query(uri, 
			null, 
			null, 
			null, 
			null);
			
			if(cursor == null || cursor.getCount()==0)
			{
				return;
			}
			
			this.myChannels = new ArrayList<String>();
			int nameIndex = cursor.getColumnIndex("name");
			int valueIndex = cursor.getColumnIndex("value");
			cursor.moveToFirst();			
			do
			{
				String name = cursor.getString(nameIndex);
				String value = cursor.getString(valueIndex);
				
				if(name.equals("deviceId"))
				{
					this.deviceId = value;
				}
				else if(name.equals("serverId"))
				{
					this.serverId = value;
				}
				else if(name.equals("serverIp"))
				{
					this.serverIp = value;
				}
				else if(name.equals("plainServerPort"))
				{
					this.plainServerPort = value;
				}
				else if(name.equals("secureServerPort"))
				{
					this.secureServerPort = value;
				}
				else if(name.equals("authenticationHash"))
				{
					this.authenticationHash = value;
				}
				else if(name.equals("authenticationNonce"))
				{
					this.authenticationNonce = value;
				}
				else if(name.equals("email"))
				{
					this.email = value;
				}
				else if(name.equals("cometMode"))
				{
					this.cometMode = value;
				}
				else if(name.equals("httpPort"))
				{
					this.httpPort = value;
				}
				else if(name.equals("cometPollInterval"))
				{
					this.cometPollInterval = Long.parseLong(value);
				}
				else if(name.equals("maxPacketSize"))
				{
					this.maxPacketSize = Integer.parseInt(value);
				}
				else if(name.equals("isSSLActive"))
				{
					this.isSSLActive = value.equals("true")?
					Boolean.TRUE.booleanValue():Boolean.FALSE.booleanValue();
				}
				else if(name.equals("isActive"))
				{
					this.isActive = value.equals("true")?
					Boolean.TRUE.booleanValue():Boolean.FALSE.booleanValue();
				}
				else if(name.equals("isSSLCertStored"))
				{
					this.isSSLCertStored = value.equals("true")?
					Boolean.TRUE.booleanValue():Boolean.FALSE.booleanValue();
				}
				else if(name.startsWith("myChannels["))
				{
					this.myChannels.add(value);
				}
				
				cursor.moveToNext();
			}while(!cursor.isAfterLast());
																											
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
		}
		catch(Exception e)
		{
			throw new SystemException(this.getClass().getName(), "load", new Object[]{
				"Configuration Load Exception="+e.getMessage()
			});
		}
	}
	
	private void serializeChannels(ContentValues record)
	{
		if(this.myChannels == null || this.myChannels.isEmpty())
		{
			return;
		}
		
		int channelCount = this.myChannels.size();
		record.put("myChannels:size", ""+channelCount);
		int i = 0;
		for(String channel: this.myChannels)
		{
			record.put("myChannels["+(i++)+"]", channel);
		}
	}
}
