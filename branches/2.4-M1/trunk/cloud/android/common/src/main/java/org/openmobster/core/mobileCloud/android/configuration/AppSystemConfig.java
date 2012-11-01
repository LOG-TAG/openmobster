/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.android.configuration;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.util.GenericAttributeManager;
import org.openmobster.core.mobileCloud.android.util.IOUtil;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

/**
 *
 * @author openmobster@gmail.com
 */
public final class AppSystemConfig
{
	private static AppSystemConfig singleton;
	
	private GenericAttributeManager attrMgr;
	private boolean isActive;
	
	private AppSystemConfig()
	{
		
	}
	
	public static AppSystemConfig getInstance()
	{
		if(AppSystemConfig.singleton == null)
		{
			synchronized(AppSystemConfig.class)
			{
				if(AppSystemConfig.singleton == null)
				{
					AppSystemConfig.singleton = new AppSystemConfig();
				}
			}
		}
		return AppSystemConfig.singleton;
	}
	
	public static void stop()
	{
		AppSystemConfig.singleton = null;
	}
	
	public boolean isActive()
	{
		return this.isActive;
	}
	
	public synchronized void start()
	{
		try
		{
			this.attrMgr = new GenericAttributeManager();
			
			//parse the openmobster-app.xml
			InputStream is = AppSystemConfig.class.getResourceAsStream("/openmobster-app.xml");
			
			if(is == null)
			{
				//configuration not found
				return;
			}
			
			String xml = new String(IOUtil.read(is));
			
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document root = builder.parse(new ByteArrayInputStream(xml.getBytes()));
			
			//Parse <encryption>true/false</encryption>
			NodeList encryptionNodes = root.getElementsByTagName("encryption");
			if(encryptionNodes != null && encryptionNodes.getLength()>0)
			{
				int length = encryptionNodes.getLength();
				for(int i=0; i<length; i++)
				{
					Element local = (Element)encryptionNodes.item(i);
					String encryption = local.getFirstChild().getNodeValue().trim();
					this.attrMgr.setAttribute("encryption", encryption);
				}
			}
			
			/**
			 * Parse the <push>
			 * 				<launch-activity-class></launch-activity-class>
			 * 				<icon-name></icon-name>
			 * 			 </push>
			 */
			NodeList push = root.getElementsByTagName("push");
			if(push != null && push.getLength()>0)
			{
				Element pushElement = (Element)push.item(0);
				Element launchActivityClass = (Element)pushElement.getElementsByTagName("launch-activity-class").item(0);
				Element iconName = (Element)pushElement.getElementsByTagName("icon-name").item(0);
				
				String pushActivityClass = launchActivityClass.getFirstChild().getNodeValue().trim();
				String pushIconName = iconName.getFirstChild().getNodeValue().trim();
				
				this.attrMgr.setAttribute("launch-activity-class",pushActivityClass);
				this.attrMgr.setAttribute("push-icon-name", pushIconName);
			}
			
			/**
			 * Parse
			 * 
			 *<channels>
			 *		<channel name='fuseapp_channel'>
			 *			<sync-push-message>You have {0} Fuse App Messages</sync-push-message>
			 *		</channel>
			 *		<channel name='one_sync_channel'>
			 *			<sync-push-message>You have {0} One Sync Channel Messages</sync-push-message>
			 *		</channel>
			 *	    <channel name='two_sync_channel'>
			 *			<sync-push-message>You have {0} Two Sync Channel Messages</sync-push-message>
			 *		</channel>
			 *</channels> 
			 */
			List<ChannelInfo> channels = new ArrayList<ChannelInfo>();
			this.attrMgr.setAttribute("channels", channels);
			NodeList channelsNodes = root.getElementsByTagName("channels");
			if(channelsNodes != null)
			{
				Element channelsElem = (Element)channelsNodes.item(0);
				NodeList channelNodes = channelsElem.getElementsByTagName("channel");
				int length = channelNodes.getLength();
				for(int i=0; i<length; i++)
				{
					ChannelInfo channelInfo = new ChannelInfo();
					Element channelElem = (Element)channelNodes.item(i);
					channelInfo.channel = channelElem.getAttribute("name");
					NodeList messageNodes = channelElem.getElementsByTagName("sync-push-message");
					if(messageNodes != null)
					{
						Element messageElem = (Element)messageNodes.item(0);
						channelInfo.syncPushMessage = messageElem.getTextContent();
					}
					channels.add(channelInfo);
				}
			}
			
			this.isActive = true;
		}
		catch(Throwable e)
		{
			e.printStackTrace(System.out);
			
			SystemException syse = new SystemException(this.getClass().getName(), "init", new Object[]{
				"Exception: "+e.toString(),
				"Message: "+e.getMessage()
			});
			
			throw syse;
		}
	}
	
	public boolean isEncryptionActivated()
	{
		if(this.attrMgr == null)
		{
			this.start();
		}
		
		String encryption = (String)this.attrMgr.getAttribute("encryption");
		
		if(encryption != null && encryption.trim().length()>0)
		{
			if(encryption.equals(""+Boolean.TRUE))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public String getPushLaunchActivityClassName()
	{
		return (String)this.attrMgr.getAttribute("launch-activity-class");
	}
	
	public String getPushIconName()
	{
		return (String)this.attrMgr.getAttribute("push-icon-name");
	}
	
	public Set<String> getChannels()
	{
		Set<String> registeredChannels = new HashSet<String>();
		
		List<ChannelInfo> channelInfo = (List<ChannelInfo>)this.attrMgr.getAttribute("channels");
		for(ChannelInfo channel:channelInfo)
		{
			registeredChannels.add(channel.channel);
		}
		
		return registeredChannels;
	}
	
	public String getSyncPushMessage(String channel)
	{
		List<ChannelInfo> channelInfo = (List<ChannelInfo>)this.attrMgr.getAttribute("channels");
		for(ChannelInfo local:channelInfo)
		{
			if(local.channel.equals(channel))
			{
				return local.syncPushMessage;
			}
		}
		return null;
	}
	
	private static class ChannelInfo
	{
		private String channel;
		private String syncPushMessage;
	}
}
